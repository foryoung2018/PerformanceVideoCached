#include <jni.h>
#include <string>
#include <android/log.h>
#include "mmap_util.h"
#include "Mwrite.h"
//#define  LOG(...) __android_log_print(ANDROID_LOG_VERBOSE,"cached_server",__VA_ARGS__);

static const char* const kClassDocScanner = "com/bisu/serverlibrary/io/NativeHelper";

extern "C"
JNIEXPORT jlong init(JNIEnv *env, jclass clazz, jstring data){
    const char*  hello = env->GetStringUTFChars(data ,NULL);
    Mwrite* mwrite = new Mwrite();
    LOG(hello);
    return reinterpret_cast<long>(mwrite);
}

extern "C"
JNIEXPORT void JNICALL
mmapWrite(JNIEnv *env, jclass type, jstring data_, jstring path_,
jlong writePtr, jstring fileName_ ) {
    LOG("data_ %s , path_ %s writeprt %d filename %s", data_,path_,writePtr,fileName_ );
    const char *data = env->GetStringUTFChars(data_, 0);
    const char *path = env->GetStringUTFChars(path_, 0);
    const char *fileName = env->GetStringUTFChars(fileName_, 0);
    LOG("ptr = %d", writePtr);
    Mwrite* ptr = reinterpret_cast<Mwrite*>(writePtr);
    LOG("ptr = %d", ptr);
    ptr ->mmap_write(data,env->GetStringUTFLength(data_),path,fileName);
//    mmap_write(data,env->GetStringUTFLength(data_),path,fileName);//这里有个陷进，如果使用GetStringLength的话可能会因为编码问题导致数据丢失
    env->ReleaseStringUTFChars(data_, data);
    env->ReleaseStringUTFChars(path_, path);
    env->ReleaseStringUTFChars(fileName_, fileName);
}

extern "C"
JNIEXPORT void JNICALL
mmapWriteByte(JNIEnv *env, jclass type, jbyteArray data_,
                                               jstring path_,
          jlong writePtr,
          jstring fileName_) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);
    const char *path = env->GetStringUTFChars(path_, 0);
    const char *fileName = env->GetStringUTFChars(fileName_, 0);
    Mwrite* ptr = reinterpret_cast<Mwrite*>(writePtr);
    ptr ->mmap_write(data,env->GetArrayLength(data_),path,fileName);

    env->ReleaseByteArrayElements(data_, data, 0);
    env->ReleaseStringUTFChars(path_, path);
    env->ReleaseStringUTFChars(fileName_, fileName);
}


static JNINativeMethod gMethods[] = {

        {
                "init",
                "(Ljava/lang/String;)J",
                (void*)init
        },
        {
                "mmapWrite",
                "(Ljava/lang/String;Ljava/lang/String;JLjava/lang/String;)V",
                (void*)mmapWrite
        },
        {
                "mmapWriteByte",
                "([BLjava/lang/String;JLjava/lang/String;)V",
                (void*)mmapWriteByte
        }
};

extern "C"
JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv *env = NULL;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return JNI_FALSE;
    }
    jclass classDocScanner = env->FindClass(kClassDocScanner);
    if(env -> RegisterNatives(classDocScanner, gMethods, sizeof(gMethods)/ sizeof(gMethods[0])) < 0) {
        return JNI_FALSE;
    }
    return JNI_VERSION_1_4;
}