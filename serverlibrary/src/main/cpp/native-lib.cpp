#include <jni.h>
#include <string>
#include <android/log.h>
#include "mmap_util.h"
#define  LOG(...) __android_log_print(ANDROID_LOG_VERBOSE,"cached_server",__VA_ARGS__);

extern "C"
JNIEXPORT void JNICALL
Java_com_bisu_serverlibrary_io_NativeHelper_init(JNIEnv *env, jclass clazz,jstring data) {
    const char*  hello = env->GetStringUTFChars(data ,NULL);
    LOG(hello);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_bisu_serverlibrary_io_NativeHelper_mmapWrite(JNIEnv *env, jclass type, jstring data_, jstring path_,
                                           jstring fileName_) {
    const char *data = env->GetStringUTFChars(data_, 0);
    const char *path = env->GetStringUTFChars(path_, 0);
    const char *fileName = env->GetStringUTFChars(fileName_, 0);
    mmap_write(data,env->GetStringUTFLength(data_),path,fileName);//这里有个陷进，如果使用GetStringLength的话可能会因为编码问题导致数据丢失
    env->ReleaseStringUTFChars(data_, data);
    env->ReleaseStringUTFChars(path_, path);
    env->ReleaseStringUTFChars(fileName_, fileName);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_bisu_serverlibrary_io_NativeHelper_mmapWriteByte(JNIEnv *env, jclass type, jbyteArray data_,
                                               jstring path_, jstring fileName_) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);
    const char *path = env->GetStringUTFChars(path_, 0);
    const char *fileName = env->GetStringUTFChars(fileName_, 0);

    mmap_write(data,env->GetArrayLength(data_),path,fileName);

    env->ReleaseByteArrayElements(data_, data, 0);
    env->ReleaseStringUTFChars(path_, path);
    env->ReleaseStringUTFChars(fileName_, fileName);
}