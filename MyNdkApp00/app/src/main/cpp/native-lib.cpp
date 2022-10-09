#include "jni_helper.h"
#include <string>
#include <pthread.h>
#include "Util/Utils.h"

jstring stringFromJNI ( JNIEnv* env, jobject ) {
    LOGT();

    Utils utils;
    return env->NewStringUTF( utils.getMessage());
}

void *ThreadAssureSuicide(void * p_msec ) {
    LOGT();

    int * p_kill_msec = (int *) p_msec;

    const int timer = p_kill_msec[0];
    LOGI("after %dmsec, kill process", timer);
    usleep(1000 * timer );

    delete [] p_kill_msec;

    kill(getpid(), SIGKILL);
    exit(0);

    return NULL;
}
void killMyProcess( JNIEnv* env, jobject obj, jint sec ) {
    int * p_kill_msec = new int[1];
    p_kill_msec[0] = sec * 1000;

    if( sec < 1 ) {
        p_kill_msec[0] = 300; // 300 msec
    }

    pthread_t thread_id;
    pthread_create( &thread_id, NULL, ThreadAssureSuicide, p_kill_msec );
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

    const char * str_native_classname = "com/purehero/myndkapp00/NativeLibrary";
    jclass clazz = env->FindClass( str_native_classname );
    if( clazz == NULL ) {
        LOGE( "ERROR: FineClass => %s", str_native_classname );
        return JNI_FALSE;
    }
    JNINativeMethod native_methods [] = {
            { "stringFromJNI", "()Ljava/lang/String;", (void*) stringFromJNI },
            { "killMyProcess", "(I)V", (void*) killMyProcess }
    };

    if( env->RegisterNatives( clazz, native_methods, sizeof(native_methods)/sizeof(native_methods[0])) < 0 ) {
        LOGE( "ERROR: RegisterNatives => %s %d method", str_native_classname, (int)(sizeof(native_methods)/sizeof(native_methods[0])));
        return JNI_FALSE;
    }

    return JNI_VERSION_1_6;
}
