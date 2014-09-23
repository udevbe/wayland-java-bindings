#include <stdlib.h>
#include <string.h>

#include <wayland-util.h>

#include "WlUtilJNI.h"

static jclass dispatcher_class;

static jmethodID j_func_dispatch_callback;

int wl_dispatcher_func(const void *data,
                       void *object,
                       uint32_t opcode,
                       const struct wl_message *message,
                       union wl_argument *arguments){
    JNIEnv *const env;
    GET_ATTACHED_JENV(env);
    (*env)->CallStaticVoidMethod(env,
	    				         dispatcher_class,
                                 j_func_dispatch_callback,
                                 (jobject)data,
                                 (jlong)(intptr_t)object,
                                 (jint)opcode,
                                 (jlong)(intptr_t)message,
                                 (jlong)(intptr_t)arguments);
    return 0;
}

/*
 * Class:     org_freedesktop_wayland_util_WlUtilJNI
 * Method:    register
 * Signature: (Ljava/lang/Class;)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_util_WlUtilJNI_register(JNIEnv *env,
                                                                  jclass class,
                                                                  jclass dispatcher_class_t){
    dispatcher_class = (*env)->NewGlobalRef(env,
                                            dispatcher_class_t);
    j_func_dispatch_callback = (*env)->GetStaticMethodID(env,
                                                         dispatcher_class_t,
                                                         "dispatch",
                                                         "(Ljava/lang/Object;JIJJ)V");
}

/*
 * Class:     org_freedesktop_wayland_util_WlUtilJNI
 * Method:    createArguments
 * Signature: (I)J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_util_WlUtilJNI_createArguments(JNIEnv *env,
                                                                          jclass class,
                                                                          jint size){
    return (jlong)(intptr_t)malloc(size*sizeof(union wl_argument));
}

/*
 * Class:     org_freedesktop_wayland_util_WlUtilJNI
 * Method:    destroyArgument
 * Signature: (J)J
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_util_WlUtilJNI_destroyArgument(JNIEnv *env,
                                                                         jclass class,
                                                                         jlong argument_p){
    free((union wl_argument*)(intptr_t)argument_p);
}

/*
 * Class:     org_freedesktop_wayland_util_WlUtilJNI
 * Method:    getIArgument
 * Signature: (JI)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_util_WlUtilJNI_getIArgument(JNIEnv *env,
                                                                      jclass class,
                                                                      jlong argument_p,
                                                                      jint index){
    union wl_argument* arguments = (union wl_argument*)(intptr_t)argument_p;
    return (jint)arguments[index].i;
}

/*
 * Class:     org_freedesktop_wayland_util_WlUtilJNI
 * Method:    getUArgument
 * Signature: (JI)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_util_WlUtilJNI_getUArgument(JNIEnv *env,
                                                                      jclass class,
                                                                      jlong argument_p,
                                                                      jint index){
    union wl_argument* arguments = (union wl_argument*)(intptr_t)argument_p;
    return (jint)arguments[index].u;
}

/*
 * Class:     org_freedesktop_wayland_util_WlUtilJNI
 * Method:    getFArgument
 * Signature: (JI)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_util_WlUtilJNI_getFArgument(JNIEnv *env,
                                                                      jclass class,
                                                                      jlong argument_p,
                                                                      jint index){
    union wl_argument* arguments = (union wl_argument*)(intptr_t)argument_p;
    return (jint)arguments[index].f;
}

/*
 * Class:     org_freedesktop_wayland_util_WlUtilJNI
 * Method:    getSArgument
 * Signature: (JI)Ljava/lang/String;
 */
JNIEXPORT
jstring JNICALL Java_org_freedesktop_wayland_util_WlUtilJNI_getSArgument(JNIEnv *env,
                                                                         jclass class,
                                                                         jlong argument_p,
                                                                         jint index){
    union wl_argument* arguments = (union wl_argument*)(intptr_t)argument_p;
    return (*env)->NewStringUTF(env, arguments[index].s);
}

/*
 * Class:     org_freedesktop_wayland_util_WlUtilJNI
 * Method:    getOArgument
 * Signature: (JI)J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_util_WlUtilJNI_getOArgument(JNIEnv *env,
                                                                       jclass class,
                                                                       jlong argument_p,
                                                                       jint index){
    union wl_argument* arguments = (union wl_argument*)(intptr_t)argument_p;
    return (jlong)(intptr_t)arguments[index].o;
}

/*
 * Class:     org_freedesktop_wayland_util_WlUtilJNI
 * Method:    getNArgument
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_org_freedesktop_wayland_util_WlUtilJNI_getNArgument(JNIEnv *env,
                                                                                jclass class,
                                                                                jlong argument_p,
                                                                                jint index){
    union wl_argument* arguments = (union wl_argument*)(intptr_t)argument_p;
    return (jint)arguments[index].n;
}

/*
 * Class:     org_freedesktop_wayland_util_WlUtilJNI
 * Method:    getAArgument
 * Signature: (JI)[B
 */
JNIEXPORT
jbyteArray JNICALL Java_org_freedesktop_wayland_util_WlUtilJNI_getAArgument(JNIEnv *env,
                                                                            jclass class,
                                                                            jlong argument_p,
                                                                            jint index){
    union wl_argument* arguments = (union wl_argument*)(intptr_t)argument_p;
    struct wl_array* array = arguments[index].a;

    jbyteArray result;
    result = (*env)->NewByteArray(env, array->size);
    if (result == NULL) {
        return NULL; /* out of memory error thrown */
    }
    (*env)->SetByteArrayRegion(env, result, 0, array->size, array->data);
    return result;
}

/*
 * Class:     org_freedesktop_wayland_util_WlUtilJNI
 * Method:    getHArgument
 * Signature: (JI)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_util_WlUtilJNI_getHArgument(JNIEnv *env,
                                                                      jclass class,
                                                                      jlong argument_p,
                                                                      jint index){
    union wl_argument* arguments = (union wl_argument*)(intptr_t)argument_p;
    return (jint)arguments[index].h;
}

/*
 * Class:     org_freedesktop_wayland_util_WlUtilJNI
 * Method:    setIUNHArgument
 * Signature: (JII)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_util_WlUtilJNI_setIUNHArgument(JNIEnv *env,
                                                                         jclass class,
                                                                         jlong argument_p,
                                                                         jint index ,
                                                                         jint iunh){
    union wl_argument* arguments = (union wl_argument*)(intptr_t)argument_p;
    arguments[index].i = (int32_t)iunh;
}

/*
 * Class:     org_freedesktop_wayland_util_WlUtilJNI
 * Method:    setOArgument
 * Signature: (JIJ)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_util_WlUtilJNI_setOArgument(JNIEnv *env,
                                                                      jclass class,
                                                                      jlong argument_p,
                                                                      jint index,
                                                                      jlong object_p){
    union wl_argument* arguments = (union wl_argument*)(intptr_t)argument_p;
    arguments[index].o = (struct wl_object*)(intptr_t)object_p;
}

/*
 * Class:     org_freedesktop_wayland_util_WlUtilJNI
 * Method:    setFArgument
 * Signature: (JII)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_util_WlUtilJNI_setFArgument(JNIEnv *env,
                                                                      jclass class,
                                                                      jlong argument_p,
                                                                      jint index,
                                                                      jint raw){
    union wl_argument* arguments = (union wl_argument*)(intptr_t)argument_p;
    arguments[index].f = (wl_fixed_t)raw;
}

/*
 * Class:     org_freedesktop_wayland_util_WlUtilJNI
 * Method:    setSArgument
 * Signature: (JILjava/lang/String;)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_util_WlUtilJNI_setSArgument(JNIEnv *env,
                                                                      jclass class,
                                                                      jlong argument_p,
                                                                      jint index,
                                                                      jstring string){
    union wl_argument* arguments = (union wl_argument*)(intptr_t)argument_p;
    arguments[index].s = (*env)->GetStringUTFChars(env, string, 0);
}

/*
 * Class:     org_freedesktop_wayland_util_WlUtilJNI
 * Method:    setAArgument
 * Signature: (JILjava/nio/ByteBuffer;)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_util_WlUtilJNI_setAArgument(JNIEnv *env,
                                                                      jclass class,
                                                                      jlong argument_p,
                                                                      jint index,
                                                                      jobject jarray){
    union wl_argument* arguments = (union wl_argument*)(intptr_t)argument_p;
    void * data = (*env)->GetDirectBufferAddress(env, jarray);
    jlong capacity = (*env)->GetDirectBufferCapacity(env, jarray);

    struct wl_array array;
    wl_array_init(&array);
    void* array_data = wl_array_add(&array, (size_t)capacity);
    memcpy(array_data, data, (size_t)capacity);

    arguments[index].a = &array;
}

/*
 * Class:     org_freedesktop_wayland_util_WlUtilJNI
 * Method:    initMessage
 * Signature: (JILjava/lang/String;Ljava/lang/String;[J)V
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_util_WlUtilJNI_initMessage(JNIEnv *env,
                                                                      jclass class,
                                                                      jlong message_p,
                                                                      jint msg_index,
                                                                      jstring name,
                                                                      jstring signature,
                                                                      jlongArray interface_p_a){
    struct wl_message message;
    message.name = (*env)->GetStringUTFChars(env,
                                             name,
                                             0);
    message.signature = (*env)->GetStringUTFChars(env,
                                                  signature,
                                                  0);

    const jsize len = (*env)->GetArrayLength(env,
                                             interface_p_a);
    jlong *body = (*env)->GetLongArrayElements(env,
                                               interface_p_a,
                                               0);
    const struct wl_interface** interfaces = malloc((size_t)len*sizeof(struct wl_interface*));
    int i = 0;
    for(;i < len;i++){
        interfaces[i] = (struct wl_interface*)(intptr_t)body[i];
    }
    message.types = interfaces;
    ((struct wl_message*)(intptr_t) message_p)[(int)msg_index] = message;

    return (jlong)(intptr_t)&((struct wl_message*)(intptr_t) message_p)[(int)msg_index];
}

/*
 * Class:     org_freedesktop_wayland_util_WlUtilJNI
 * Method:    createInterface
 * Signature: (Ljava/lang/String;IJIJI)J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_util_WlUtilJNI_createInterface(JNIEnv *env,
                                                                          jclass class,
                                                                          jstring name,
                                                                          jint version,
                                                                          jlong method_p,
                                                                          jint method_count,
                                                                          jlong event_p,
                                                                          jint event_count){

    struct wl_interface* interface = malloc(sizeof(struct wl_interface));
    interface->name = (*env)->GetStringUTFChars(env,
                                                name,
                                                0);
    interface->version = (int)version;
    interface->method_count = (int)method_count;
    interface->methods = (const struct wl_message *)(intptr_t)method_p;
    interface->event_count = (int)event_count;
    interface->events = (const struct wl_message *)(intptr_t)event_p;

    return (jlong)(intptr_t)interface;
}

/*
 * Class:     org_freedesktop_wayland_util_WlUtilJNI
 * Method:    getName
 * Signature: (J)Ljava/lang/String;
 */
JNIEXPORT
jstring JNICALL Java_org_freedesktop_wayland_util_WlUtilJNI_getName(JNIEnv *env,
                                                                    jclass class,
                                                                    jlong  interface_p){
    struct wl_interface* interface = (struct wl_interface*)(intptr_t)interface_p;
    return (*env)->NewStringUTF(env,
                                interface->name);
}


/*
 * Class:     org_freedesktop_wayland_util_WlUtilJNI
 * Method:    allocateMessages
 * Signature: (I)J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_util_WlUtilJNI_allocateMessages(JNIEnv *env,
                                                                           jclass class,
                                                                           jint count){
    return (jlong)(intptr_t)malloc(count*sizeof(struct wl_message));
}

/*
 * Class:     org_freedesktop_wayland_util_WlUtilJNI
 * Method:    free
 * Signature: (J)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_util_WlUtilJNI_free(JNIEnv *env,
                                                              jclass class,
                                                              jlong pointer){
    free((void*)(intptr_t)pointer);
}
