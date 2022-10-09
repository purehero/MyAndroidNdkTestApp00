//
// Created by purehero on 2022-09-27.
//

#include "TestNativeModule.h"
#include "..\jni_helper.h"

int GetListSize( JNIEnv * env, jclass cls_list, jobject obj_list );

void doTest( JNIEnv* env, jobject obj, jobject app_context ) {
    LOGT();

    jclass cls_context = env->FindClass("android/content/ContextWrapper");
    jmethodID method_id = env->GetMethodID( cls_context, "getPackageManager", "()Landroid/content/pm/PackageManager;" );
    jobject obj_package_manager = env->CallObjectMethod( app_context, method_id );

    jclass cls_package_manager = env->FindClass( "android/content/pm/PackageManager" );
    // List<ApplicationInfo> getInstalledApplications(PackageManager.ApplicationInfoFlags flags) 메소드를 찾아 본다. ( SDK 33 )
    method_id = env->GetMethodID( cls_package_manager, "getInstalledApplications", "(Landroid/content/pm/PackageManager$ApplicationInfoFlags;)Ljava/util/List;" );
    if ( env->ExceptionOccurred() ) {
        env->ExceptionDescribe();
        env->ExceptionClear();
    }

    jobject obj_list = NULL;
    if( method_id != NULL ) {
        LOGD("List<ApplicationInfo> getInstalledApplications(PackageManager.ApplicationInfoFlags flags) 메소드로 처리");
        // 있다면 SDK33 으로 처리( List<ApplicationInfo> getInstalledApplications(PackageManager.ApplicationInfoFlags flags) )
        jclass cls_applicationInfoFlags = env->FindClass( "android/content/pm/PackageManager$ApplicationInfoFlags" );
        jmethodID mt_of = env->GetStaticMethodID( cls_applicationInfoFlags, "of", "(J)Landroid/content/pm/PackageManager$ApplicationInfoFlags;");
        jobject appFlags = env->CallStaticObjectMethod( cls_applicationInfoFlags, mt_of, 0x00000080 );  // android.content.pm.PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA)
        obj_list = env->CallObjectMethod( obj_package_manager, method_id, appFlags );	// call getInstalledApplications()
    } else {
        // 없다면 이전처럼 List<ApplicationInfo> getInstalledApplications(int) 메소드로 처리
        LOGD("List<ApplicationInfo> getInstalledApplications(int) 메소드로 처리");
        method_id = env->GetMethodID(cls_package_manager, "getInstalledApplications", "(I)Ljava/util/List;");
        obj_list = env->CallObjectMethod( obj_package_manager, method_id, 0x00000080 );	// PackageManager.GET_META_DATA
    }

    if( obj_list == NULL ) {
        LOGE("[FAILED] GetApplicationInfo List");
        return;
    }

    jclass cls_list = env->FindClass( "java/util/List");
    method_id = env->GetMethodID( cls_list, "get", "(I)Ljava/lang/Object;" );
    int list_size = GetListSize( env, cls_list, obj_list );
    jclass cls_application_info = env->FindClass( "android/content/pm/ApplicationInfo" );
    jfieldID package_name_id = env->GetFieldID( cls_application_info , "packageName", "Ljava/lang/String;" );

    LOGD( "[JNI] Application inofs ==========================================" );
    for ( int i = 0; i < list_size; i++ ) {
        jobject obj_app_info = env->CallObjectMethod( obj_list, method_id, i );
        jstring jstr_package_name = (jstring) env->GetObjectField( obj_app_info, package_name_id );
        const char * str_package_name = env->GetStringUTFChars( jstr_package_name, NULL );

        LOGD( "[JNI] PackageName : %s", str_package_name );

        env->ReleaseStringUTFChars( jstr_package_name, str_package_name );
        env->DeleteLocalRef( jstr_package_name );
        env->DeleteLocalRef( obj_app_info );
    }
    LOGD( "[JNI] ============================================================" );
}

int GetListSize( JNIEnv * env, jclass cls_list, jobject obj_list ) {
    jmethodID cls_list_size_method_id = env->GetMethodID( cls_list, "size", "()I" );
    return (int) env->CallIntMethod( obj_list, cls_list_size_method_id );
}

jobject getListObject( JNIEnv * env, jclass clsList, jobject objList, int idx )
{
    static jmethodID clsListGetMethodID = env->GetMethodID( clsList, "get", "(I)Ljava/lang/Object;" );
    if( clsListGetMethodID == NULL ) {
        LOGE( "Can't get MethodID for \"java.util.List.get(int location)" );
        return (NULL);
    }
    return env->CallObjectMethod( objList, clsListGetMethodID, idx );
}

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    LOGT();
    LOGD( "PID:%d, PPID:%d", getpid(), getppid());

    JNIEnv* env = NULL;
    if(vm->GetEnv((void**) &env, JNI_VERSION_1_6) != JNI_OK) {
        LOGE("ERROR: GetEnv failed");
        return -1;
    }

    const char * str_native_classname = "com/purehero/myndkapp00/test/TestNativeModule";
    jclass clazz = env->FindClass( str_native_classname );
    if( clazz == NULL ) {
        LOGE( "ERROR: FineClass => %s", str_native_classname );
        return JNI_FALSE;
    }
    JNINativeMethod native_methods [1] = {
            { "doTest", "(Landroid/content/Context;)V", (void*) doTest }
    };

    if( env->RegisterNatives( clazz, native_methods, sizeof(native_methods)/sizeof(native_methods[0])) < 0 ) {
        LOGE( "ERROR: RegisterNatives => %s %d method", str_native_classname, (int)(sizeof(native_methods)/sizeof(native_methods[0])));
        return JNI_FALSE;
    }

    return JNI_VERSION_1_6;
}
