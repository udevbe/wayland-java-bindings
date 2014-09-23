#include <stdlib.h>
#include <wayland-client.h>

#include "WlClientJNI.h"
#include "../util/WlUtilJNI.h"

JavaVM* jvm;

JNIEXPORT
jint JNI_OnLoad(JavaVM *vm, void *reserved){
    jvm = vm;
    return JNI_VERSION_1_6;
}

/*
 * Class:     org_freedesktop_wayland_client_WlClientJNI
 * Method:    connect
 * Signature: (Ljava/lang/String;)J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_client_WlClientJNI_connect__Ljava_lang_String_2(JNIEnv *const env,
                                                                                           const jclass  class,
                                                                                           const jstring display_name){
    return (jlong)(intptr_t)wl_display_connect((*env)->GetStringUTFChars(env,
                                                                         display_name,
                                                                         0));
}

/*
 * Class:     org_freedesktop_wayland_client_WlClientJNI
 * Method:    connect
 * Signature: (I)J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_client_WlClientJNI_connect__I(JNIEnv *const env,
                                                                         const jclass  class,
                                                                         const jint    fd){
    return (jlong)(intptr_t)wl_display_connect_to_fd((int)fd);
}

/*
 * Class:     org_freedesktop_wayland_client_WlClientJNI
 * Method:    disconnect
 * Signature: (J)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_client_WlClientJNI_disconnect(JNIEnv *const env,
                                                                        const jclass  class,
                                                                        const jlong   display_p){
    wl_display_disconnect((struct wl_display*)(intptr_t)display_p);
}

/*
 * Class:     org_freedesktop_wayland_client_WlClientJNI
 * Method:    getFD
 * Signature: (J)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_client_WlClientJNI_getFD(JNIEnv *const env,
                                                                   const jclass  class,
                                                                   const jlong   display_p){
    return (jint)wl_display_get_fd((struct wl_display*)(intptr_t)display_p);
}

/*
 * Class:     org_freedesktop_wayland_client_WlClientJNI
 * Method:    dispatch
 * Signature: (J)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_client_WlClientJNI_dispatch(JNIEnv *const env,
                                                                      const jclass  class,
                                                                      const jlong   display_p){
    return (jint)wl_display_dispatch((struct wl_display*)(intptr_t)display_p);
}

/*
 * Class:     org_freedesktop_wayland_client_WlClientJNI
 * Method:    dispatchPending
 * Signature: (J)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_client_WlClientJNI_dispatchPending(JNIEnv *const env,
                                                                             const jclass  class,
                                                                             const jlong   display_p){
    return (jint)wl_display_dispatch_pending((struct wl_display*)(intptr_t)display_p);
}

/*
 * Class:     org_freedesktop_wayland_client_WlClientJNI
 * Method:    dispatchQueue
 * Signature: (JJ)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_client_WlClientJNI_dispatchQueue(JNIEnv *const env,
                                                                           const jclass  class,
                                                                           const jlong   display_p,
                                                                           const jlong   queue_p){
    return (jint)wl_display_dispatch_queue((struct wl_display*)(intptr_t)display_p,
                                           (struct wl_event_queue*)(intptr_t)queue_p);
}

/*
 * Class:     org_freedesktop_wayland_client_WlClientJNI
 * Method:    dispatchQueuePending
 * Signature: (JJ)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_client_WlClientJNI_dispatchQueuePending(JNIEnv *const env,
                                                                                  const jclass  class,
                                                                                  const jlong   display_p,
                                                                                  const jlong   queue_p){
    return (jint)wl_display_dispatch_queue_pending((struct wl_display*)(intptr_t)display_p,
                                                   (struct wl_event_queue*)(intptr_t)queue_p);
}

/*
 * Class:     org_freedesktop_wayland_client_WlClientJNI
 * Method:    flush
 * Signature: (J)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_client_WlClientJNI_flush(JNIEnv *const env,
                                                                   const jclass  class,
                                                                   const jlong   display_p){
    return (jint)wl_display_flush((struct wl_display*)(intptr_t)display_p);
}

/*
 * Class:     org_freedesktop_wayland_client_WlClientJNI
 * Method:    roundtrip
 * Signature: (J)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_client_WlClientJNI_roundtrip(JNIEnv *const env,
                                                                       const jclass  class,
                                                                       const jlong   display_p){
    return (jint)wl_display_roundtrip((struct wl_display*)(intptr_t)display_p);
}

/*
 * Class:     org_freedesktop_wayland_client_WlClientJNI
 * Method:    createQueue
 * Signature: (J)J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_client_WlClientJNI_createQueue(JNIEnv *const env,
                                                                          const jclass  class,
                                                                          const jlong   display_p){
    return (jlong)(intptr_t)wl_display_create_queue((struct wl_display*)(intptr_t)display_p);
}

/*
 * Class:     org_freedesktop_wayland_client_WlClientJNI
 * Method:    getError
 * Signature: (J)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_client_WlClientJNI_getError(JNIEnv *const env,
                                                                      const jclass  class,
                                                                      const jlong   display_p){
    return (jint)wl_display_get_error((struct wl_display*)(intptr_t)display_p);
}

/*
 * Class:     org_freedesktop_wayland_client_WlClientJNI
 * Method:    prepareReadQueue
 * Signature: (JJ)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_client_WlClientJNI_prepareReadQueue(JNIEnv *const env,
                                                                              const jclass  class,
                                                                              const jlong   display_p,
                                                                              const jlong   queue_p){
    return (jint)wl_display_prepare_read_queue((struct wl_display*)(intptr_t)display_p,
                                               (struct wl_event_queue*)(intptr_t)queue_p);
}

/*
 * Class:     org_freedesktop_wayland_client_WlClientJNI
 * Method:    prepareRead
 * Signature: (J)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_client_WlClientJNI_prepareRead(JNIEnv *const env,
                                                                         const jclass  class,
                                                                         const jlong   display_p){
    return (jint)wl_display_prepare_read((struct wl_display*)(intptr_t)display_p);
}

/*
 * Class:     org_freedesktop_wayland_client_WlClientJNI
 * Method:    cancelRead
 * Signature: (J)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_client_WlClientJNI_cancelRead(JNIEnv *const env,
                                                                        const jclass  class,
                                                                        const jlong   display_p){
    wl_display_cancel_read((struct wl_display*)(intptr_t)display_p);
}

/*
 * Class:     org_freedesktop_wayland_client_WlClientJNI
 * Method:    readEvents
 * Signature: (J)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_client_WlClientJNI_readEvents(JNIEnv *const env,
                                                                        const jclass  class,
                                                                        const jlong   display_p){
    return (jint)wl_display_read_events((struct wl_display*)(intptr_t)display_p);
}

/*
 * Class:     org_freedesktop_wayland_client_WlClientJNI
 * Method:    destroyEventQueue
 * Signature: (J)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_client_WlClientJNI_destroyEventQueue(JNIEnv *const env,
                                                                               const jclass  class,
                                                                               const jlong   queue_p){
    wl_event_queue_destroy((struct wl_event_queue*)(intptr_t)queue_p);
}

/*
 * Class:     org_freedesktop_wayland_client_WlClientJNI
 * Method:    marshal
 * Signature: (JIJ)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_client_WlClientJNI_marshal(JNIEnv *const env,
                                                                     const jclass  class,
                                                                     const jlong   proxy_p,
                                                                     const jint    opcode,
                                                                     const jlong   arguments_p){
        wl_proxy_marshal_array((struct wl_proxy*)(intptr_t)proxy_p,
                               (uint32_t)opcode,
                               (union wl_argument*)(intptr_t)arguments_p);
}

/*
 * Class:     org_freedesktop_wayland_client_WlClientJNI
 * Method:    destroy
 * Signature: (J)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_client_WlClientJNI_destroy(JNIEnv *const env,
                                                                     const jclass  class,
                                                                     const jlong   proxy_p){
    wl_proxy_destroy((struct wl_proxy*)(intptr_t)proxy_p);
}

/*
 * Class:     org_freedesktop_wayland_client_WlClientJNI
 * Method:    getListener
 * Signature: (J)Ljava/lang/Object;
 */
JNIEXPORT
jobject JNICALL Java_org_freedesktop_wayland_client_WlClientJNI_getListener(JNIEnv *const env,
                                                                            const jclass  class,
                                                                            const jlong   proxy_p){
    return (jobject)wl_proxy_get_listener((struct wl_proxy*)(intptr_t)proxy_p);
}

/*
 * Class:     org_freedesktop_wayland_client_WlClientJNI
 * Method:    getProxyId
 * Signature: (J)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_client_WlClientJNI_getProxyId(JNIEnv *const env,
                                                                        const jclass  class,
                                                                        const jlong   proxy_p){
    return (jint)wl_proxy_get_id((struct wl_proxy*)(intptr_t)proxy_p);
}

/*
 * Class:     org_freedesktop_wayland_client_WlClientJNI
 * Method:    setQueue
 * Signature: (JJ)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_client_WlClientJNI_setQueue(JNIEnv *const env,
                                                                      const jclass  class,
                                                                      const jlong   proxy_p,
                                                                      const jlong   queue_p){
    wl_proxy_set_queue((struct wl_proxy*)(intptr_t)proxy_p,
                       (struct wl_event_queue*)(intptr_t)queue_p);
}

/*
 * Class:     org_freedesktop_wayland_client_WlClientJNI
 * Method:    marshalConstructor
 * Signature: (JIJJ)J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_client_WlClientJNI_marshalConstructor(JNIEnv *const env,
                                                                                 const jclass  class,
                                                                                 const jlong   proxy_p,
                                                                                 const jint    opcode,
                                                                                 const jlong   interface_p,
                                                                                 const jlong   arguments_p){
        return (jlong)(intptr_t)wl_proxy_marshal_array_constructor((struct wl_proxy*)(intptr_t)proxy_p,
                                                                   (uint32_t)opcode,
                                                                   (union wl_argument*)(intptr_t)arguments_p,
                                                                   (const struct wl_interface*)(intptr_t)interface_p);
}

/*
 * Class:     org_freedesktop_wayland_client_WlClientJNI
 * Method:    addDispatcher
 * Signature: (JLjava/lang/Object;)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_client_WlClientJNI_addDispatcher(JNIEnv *const env,
                                                                           const jclass  class,
                                                                           const jlong   proxy_p,
                                                                           const jobject impl){
    jobject implementation = (*env)->NewGlobalRef(env,
                                                  impl);
    wl_proxy_add_dispatcher((struct wl_proxy*)(intptr_t)proxy_p,
                            &wl_dispatcher_func,
                            (void*)implementation,
                            NULL);
}
