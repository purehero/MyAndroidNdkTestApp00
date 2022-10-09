#include "jni_helper.h"
#include <string>

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    LOGT();
    LOGD( "PID:%d, PPID:%d", getpid(), getppid());

    return JNI_VERSION_1_6;
}
