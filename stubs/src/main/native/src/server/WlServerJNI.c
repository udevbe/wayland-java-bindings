#include <stdlib.h>
#include <wayland-server.h>

#include "WlServerJNI.h"
#include "../util/WlUtilJNI.h"

JavaVM* jvm;

static jclass listener_class;
static jclass fd_class;
static jclass timer_class;
static jclass signal_class;
static jclass idle_class;
static jclass global_class;

static jmethodID j_func_listener_callback;
static jmethodID j_func_fd_callback;
static jmethodID j_func_timer_callback;
static jmethodID j_func_signal_callback;
static jmethodID j_func_idle_callback;
static jmethodID j_func_bind_callback;

struct wl_listener_jobject{
	struct wl_listener listener_p;
	jobject jobject_callback;
};

//listener
static
void jni_notify_func(struct wl_listener *listener,
					 void *data){
		struct wl_listener_jobject* jobject_listener = wl_container_of(listener,
		                                                               jobject_listener,
		                                                               listener_p);
	    JNIEnv *const env;
	    GET_ATTACHED_JENV(env);
	    (*env)->CallVoidMethod(env,
	    					   jobject_listener->jobject_callback,
	    					   j_func_listener_callback);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    addShmFormat
 * Signature: (JI)J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_addShmFormat(JNIEnv *const env,
                                                                           const jclass  class,
                                                                           const jlong   display_p,
                                                                           const jint    format){
    return (jlong)(intptr_t)wl_display_add_shm_format((struct wl_display*)(intptr_t)display_p,
                                                      (uint32_t)format);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    initShm
 * Signature: (J)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_initShm(JNIEnv *const env,
                                                                     const jclass  class,
                                                                     const jlong   display_p){
    return (jint)wl_display_init_shm((struct wl_display*)(intptr_t)display_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    destroyGlobal
 * Signature: (J)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_destroyGlobal(JNIEnv *const env,
                                                                           const jclass  class,
                                                                           const jlong   global_p){
    wl_global_destroy((struct wl_global*)(intptr_t)global_p);
}

static
void jni_resource_destroy_func(struct wl_resource *resource){
    jobject implementation = resource->object.implementation;
    JNIEnv *const env;
    GET_ATTACHED_JENV(env);
    (*env)->DeleteGlobalRef(env,
                            implementation);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    setDispatcher
 * Signature: (JJ)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_setDispatcher(JNIEnv *const env,
                                                                           const jclass  class,
                                                                           const jlong   resource_p,
                                                                           const jobject impl){
    wl_resource_set_dispatcher((struct wl_resource*)(intptr_t)resource_p,
                               &wl_dispatcher_func,
                               (void*)(*env)->NewGlobalRef(env,
                                       	   	   	   	   	   impl),
                               NULL,
                               &jni_resource_destroy_func);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    createShmBuffer
 * Signature: (JIIIII)J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_createShmBuffer(JNIEnv *const env,
                                                                              const jclass  class,
                                                                              const jlong   client_p,
                                                                              const jint    id,
                                                                              const jint    width,
                                                                              const jint    height,
                                                                              const jint    stride,
                                                                              const jint    format){
    return (jlong)(intptr_t)wl_shm_buffer_create((struct wl_client*)(intptr_t)client_p,
                                                 (uint32_t)id,
                                                 (int32_t)width,
                                                 (int32_t)height,
                                                 (int32_t)stride,
                                                 (uint32_t)format);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    beginAccess
 * Signature: (J)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_beginAccess(JNIEnv *const env,
                                                                         const jclass  class,
                                                                         const jlong   shm_buffer_p){
    wl_shm_buffer_begin_access((struct wl_shm_buffer*)(intptr_t)shm_buffer_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    endAccess
 * Signature: (J)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_endAccess(JNIEnv *const env,
                                                                       const jclass  class,
                                                                       const jlong   shm_buffer_p){
    wl_shm_buffer_end_access((struct wl_shm_buffer*)(intptr_t)shm_buffer_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    getData
 * Signature: (JJ)Ljava/nio/ByteBuffer;
 */
JNIEXPORT
jobject JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_getData(JNIEnv *const env,
                                                                        const jclass  class,
                                                                        const jlong   shm_buffer_p,
                                                                        const jlong   size){
    void * data = wl_shm_buffer_get_data((struct wl_shm_buffer*)(intptr_t)shm_buffer_p);
    GET_ATTACHED_JENV(env);
    return (*env)->NewDirectByteBuffer(env,
                                       data,
                                       size);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    getStride
 * Signature: (J)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_getStride(JNIEnv *const env,
                                                                       const jclass  class,
                                                                       const jlong   shm_buffer_p){
    return (jint)wl_shm_buffer_get_stride((struct wl_shm_buffer*)(intptr_t)shm_buffer_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    getFormat
 * Signature: (J)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_getFormat(JNIEnv *const env,
                                                                       const jclass  class,
                                                                       const jlong   shm_buffer_p){
    return (jint)wl_shm_buffer_get_format((struct wl_shm_buffer*)(intptr_t)shm_buffer_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    getWidth
 * Signature: (J)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_getWidth(JNIEnv *const env,
                                                                      const jclass  class,
                                                                      const jlong   shm_buffer_p){
    return (jint)wl_shm_buffer_get_width((struct wl_shm_buffer*)(intptr_t)shm_buffer_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    getHeight
 * Signature: (J)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_getHeight(JNIEnv *const env,
                                                                       const jclass  class,
                                                                       const jlong   shm_buffer_p){
    return (jint)wl_shm_buffer_get_height((struct wl_shm_buffer*)(intptr_t)shm_buffer_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    get
 * Signature: (J)J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_get(JNIEnv *const env,
                                                                  const jclass  class,
                                                                  const jlong   resource_p){
    return (jlong)(intptr_t)wl_shm_buffer_get((struct wl_resource*)(intptr_t)resource_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    getDataAsPointer
 * Signature: (J)J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_getDataAsPointer(JNIEnv *const env,
                                                                               const jclass  class,
                                                                               const jlong   shm_buffer_p){
    return (jlong)(intptr_t)wl_shm_buffer_get_data((struct wl_shm_buffer*)(intptr_t)shm_buffer_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    createListener
 * Signature: ()J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_createListener(JNIEnv *const env,
																			 const jclass  class,
																			 const jobject listener_cb){
	struct wl_listener_jobject* listener_jobject = malloc(sizeof(struct wl_listener_jobject));
	listener_jobject->listener_p.notify = &jni_notify_func;
	listener_jobject->jobject_callback = (*env)->NewGlobalRef(env,
	                                                          listener_cb);

	return (jlong)(intptr_t) listener_jobject;
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    removeListener
 * Signature: (J)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_removeListener(JNIEnv *const env,
                                                                            const jclass  class,
                                                                            const jlong listener_jobject_p){
    struct wl_listener_jobject* listener_jobject = (struct wl_listener_jobject*)(intptr_t)listener_jobject_p;
    wl_list_remove(&listener_jobject->listener_p.link);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    registerListenerHandler
 * Signature: (Ljava/lang/Class;)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_registerListenerHandler(JNIEnv *const env,
																					 const jclass  class,
																					 const jclass  listener_class_t){
	listener_class = (*env)->NewGlobalRef(env,
                                          listener_class_t);
    j_func_listener_callback = (*env)->GetMethodID(env,
                                                   listener_class,
                                                   "handle",
                                                   "()V");
}

//display

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    getEventLoop
 * Signature: (J)J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_getEventLoop(JNIEnv *const env,
                                                                           const jclass  class,
                                                                           const jlong   display_p){
    return (jlong)(intptr_t) wl_display_get_event_loop((struct wl_display *)(intptr_t)display_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    addSocket
 * Signature: (JLjava/lang/String;)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_addSocket(JNIEnv *const env,
                                                                       const jclass  class,
                                                                       const jlong   display_p,
                                                                       const jstring name){
    const char *name_p = (*env)->GetStringUTFChars(env,
                                                   name,
                                                   JNI_FALSE);
    const int fd = wl_display_add_socket((struct wl_display *)(intptr_t)display_p,
                                         name_p);
    (*env)->ReleaseStringUTFChars(env,
                                  name,
                                  name_p);
    return (jint)fd;
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    terminate
 * Signature: (J)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_terminate(JNIEnv *const env,
                                                                       const jclass  class,
                                                                       const jlong   display_p){
    wl_display_terminate((struct wl_display *)(intptr_t)display_p);
}


/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    run
 * Signature: (J)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_run(JNIEnv *const env,
                                                                 const jclass  class,
                                                                 const jlong   display_p){
    wl_display_run((struct wl_display *)(intptr_t)display_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    flushClients
 * Signature: (J)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_flushClients(JNIEnv *const env,
                                                                          const jclass  class,
                                                                          const jlong   display_p){
    wl_display_flush_clients((struct wl_display *)(intptr_t)display_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    getSerial
 * Signature: (J)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_getSerial(JNIEnv *const env,
                                                                       const jclass  class,
                                                                       const jlong   display_p){
    return (jint)wl_display_get_serial((struct wl_display *)(intptr_t)display_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    nextSerial
 * Signature: (J)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_nextSerial(JNIEnv *const env,
                                                                        const jclass  class,
                                                                        const jlong   display_p){
    return (jint)wl_display_next_serial((struct wl_display *)(intptr_t)display_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    createDisplay
 * Signature: ()J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_createDisplay(JNIEnv *const env,
                                                                            const jclass  class){
    return (jlong)(intptr_t)wl_display_create();
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    destroy
 * Signature: (J)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_destroy(JNIEnv *const env,
                                                                     const jclass  class,
                                                                     const jlong   display_p){
    wl_display_destroy((struct wl_display *)(intptr_t)display_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    addDisplayDestroyListener
 * Signature: (JLorg/freedesktop/wayland/server/Listener;)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_addDisplayDestroyListener(JNIEnv *const env,
																					   const jclass  class,
																					   const jlong	 display_p,
																					   const jlong	 listener_p){
	struct wl_listener_jobject* listener_jobject = (struct wl_listener_jobject*)(intptr_t)listener_p;
	wl_display_add_destroy_listener((struct wl_display *)(intptr_t)display_p,
									&listener_jobject->listener_p);
}

//
/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    registerFDHandler
 * Signature: (Ljava/lang/Class;)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_registerFDHandler(JNIEnv *const env,
                                                                               const jclass  class,
                                                                               const jclass  fd_class_t){
    fd_class = (*env)->NewGlobalRef(env,
                                    fd_class_t);
    j_func_fd_callback = (*env)->GetMethodID(env,
                                             fd_class,
                                             "handle",
                                             "(II)I");
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    registerTimerHandler
 * Signature: (Ljava/lang/Class;)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_registerTimerHandler(JNIEnv *const env,
                                                                                  const jclass  class,
                                                                                  const jclass  timer_class_t){
    timer_class = (*env)->NewGlobalRef(env,
                                       timer_class_t);
    j_func_timer_callback = (*env)->GetMethodID(env,
                                                timer_class,
                                                "handle",
                                                "()I");
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    registerSignalHandler
 * Signature: (Ljava/lang/Class;)V
 */
JNIEXPORT void
JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_registerSignalHandler(JNIEnv *const env,
                                                                              const jclass  class,
                                                                              const jclass  signal_class_t){
    signal_class = (*env)->NewGlobalRef(env,
                                        signal_class_t);
    j_func_signal_callback = (*env)->GetMethodID(env,
                                                 signal_class,
                                                 "handle",
                                                 "(I)I");
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    registerIdleHandler
 * Signature: (Ljava/lang/Class;)V
 */
JNIEXPORT void
JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_registerIdleHandler(JNIEnv *const env,
                                                                            const jclass  class,
                                                                            const jclass  idle_class_t){
    idle_class = (*env)->NewGlobalRef(env,
                                      idle_class_t);
    j_func_idle_callback = (*env)->GetMethodID(env,
                                               idle_class,
                                               "handle",
                                               "()V");
}

static
int jni_handle_event_loop_fd_call(int fd,
                              uint32_t mask,
                              void *data){
    jobject callback = data;
    JNIEnv *const env;
    GET_ATTACHED_JENV(env);
    const int ret = (*env)->CallIntMethod(env,
                                          callback,
                                          j_func_fd_callback,
                                          (jint)fd,
                                          (jint)mask);
    //TODO: Handle java Exceptions
    //TODO cleanup global ref when we remove the event source
    return ret;
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    addFileDescriptor
 * Signature: (JIILorg/freedesktop/wayland/server/EventLoop/FileDescriptorEventHandler;)J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_addFileDescriptor(JNIEnv *const env,
                                                                                const jclass  class,
                                                                                const jlong   event_loop_p,
                                                                                const jint    fd,
                                                                                const jint    mask,
                                                                                const jobject callback){
    return (jlong)(intptr_t) wl_event_loop_add_fd((struct wl_event_loop *)(intptr_t)event_loop_p,
                                                  (int)fd,
                                                  (uint32_t)mask,
                                                  jni_handle_event_loop_fd_call,
                                                  (*env)->NewGlobalRef(env,
                                                                       callback));
}


/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    updateFileDescriptor
 * Signature: (JJI)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_updateFileDescriptor(JNIEnv *const env,
                                                                                  const jclass  class,
                                                                                  const jlong   event_source_p,
                                                                                  const jint    mask){
	return (jint) wl_event_source_fd_update((struct wl_event_source *)(intptr_t)event_source_p,
							  	     	 	(uint32_t)mask);
}

static
int jni_handle_event_loop_timer_call(void *data){
    jobject callback = data;
    JNIEnv *const env;
    GET_ATTACHED_JENV(env);
    int ret = (*env)->CallIntMethod(env,
                                    callback,
                                    j_func_timer_callback);
    // TODO: Handle Exceptions
    //TODO cleanup global ref when we remove the event source
    return ret;
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    addTimer
 * Signature: (JLorg/freedesktop/wayland/server/EventLoop/TimerEventHandler;)J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_addTimer(JNIEnv *const   env,
                                                                       const   jclass  class,
                                                                       const   jlong   event_loop_p,
                                                                       const   jobject callback){
    struct wl_event_source *const source   = wl_event_loop_add_timer((struct wl_event_loop *)(intptr_t)event_loop_p,
                                                                     jni_handle_event_loop_timer_call,
                                                                     (*env)->NewGlobalRef(env,
                                                                                          callback));
    if (source == NULL) {
        //TODO handle error, send OOM error?
        return (jlong)(intptr_t)NULL;
    }
    return (jlong)(intptr_t)source;
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    updateTimer
 * Signature: (JJI)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_updateTimer(JNIEnv *const env,
                                                                         const jclass  class,
                                                                         const jlong   event_source_p,
                                                                         const jint    milli_seconds){
	return (jint)wl_event_source_timer_update((struct wl_event_source *)(intptr_t) event_source_p,
								 	 	 	  (int)milli_seconds);
}

static
int jni_handle_event_loop_signal_call(int signal_number,
                                  void * data){
    jobject callback = data;
    JNIEnv *const env;
    GET_ATTACHED_JENV(env);
    int ret = (*env)->CallIntMethod(env,
                                    callback,
                                    j_func_signal_callback,
                                    (jint)signal_number);
    // TODO: Handle Exceptions
    //TODO cleanup global ref when we remove the event source
    return ret;
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    addSignal
 * Signature: (JILorg/freedesktop/wayland/server/EventLoop/SignalEventHandler;)J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_addSignal(JNIEnv *const env,
                                                                        const jclass  class,
                                                                        const jlong   event_loop_p,
                                                                        const jint    signal_numer,
                                                                        const jobject callback){
    return (jlong)(intptr_t)wl_event_loop_add_signal((struct wl_event_loop*)(intptr_t)event_loop_p,
            										 (int) signal_numer,
            										 jni_handle_event_loop_signal_call,
            										 (*env)->NewGlobalRef(env,
            												 	 	      callback));
}

static
void handle_event_loop_idle_call(void * data){
    jobject callback = data;
    JNIEnv *const env;
    GET_ATTACHED_JENV(env);
    (*env)->CallVoidMethod(env,
                           callback,
                           j_func_idle_callback);
    // TODO: Handle Exceptions
    //TODO cleanup global ref when we remove the event source
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    addIdle
 * Signature: (JLorg/freedesktop/wayland/server/EventLoop/IdleHandler;)J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_addIdle(JNIEnv *const env,
                                                                      const jclass  class,
                                                                      const jlong   event_loop_p,
                                                                      const jobject callback){
	struct wl_event_source *source = wl_event_loop_add_idle((struct wl_event_loop*)(intptr_t)event_loop_p,
                                    						handle_event_loop_idle_call,
                                    						(*env)->NewGlobalRef(env,
                                    											 callback));
    if (source == NULL) {
        //TODO handle error, send OOM error?
        return (jlong)(intptr_t)NULL; /* Exception Thrown */
    }
    return (jlong)(intptr_t)source;
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    remove
 * Signature: (JJ)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_remove(JNIEnv *const env,
                                                                    const jclass  class,
                                                                    const jlong   event_source_p){
	return (jint)wl_event_source_remove((struct wl_event_source *)(intptr_t)event_source_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    check
 * Signature: (JJ)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_check(JNIEnv *const env,
                                                                   const jclass  class,
                                                                   const jlong   event_source_p){
	wl_event_source_check((struct wl_event_source *)(intptr_t)event_source_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    dispatch
 * Signature: (JI)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_dispatch(JNIEnv *const env,
                                                                      const jclass  class,
                                                                      const jlong   event_loop_p,
                                                                      const jint    timeout){
    return (jint) wl_event_loop_dispatch((struct wl_event_loop *)(intptr_t)event_loop_p,
                                         timeout);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    dispatchIdle
 * Signature: (J)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_dispatchIdle(JNIEnv *const env,
                                                                          const jclass  class,
                                                                          const jlong   event_loop_p){
    wl_event_loop_dispatch_idle((struct wl_event_loop *)(intptr_t)event_loop_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    createEventLoop
 * Signature: ()J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_createEventLoop(JNIEnv *const env,
                                                                              const jclass  class){
    struct wl_event_loop * event_loop_p = wl_event_loop_create();
    if (event_loop_p == NULL) {
        //TODO handle OOM error, send OOM error?
        return (jlong)(intptr_t)NULL;
    }
    return (jlong)(intptr_t)event_loop_p;
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    getFileDescriptor
 * Signature: (J)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_getFileDescriptor(JNIEnv *const env,
                                                                               const jclass  class,
                                                                               const jlong event_loop_p){
    return (jint)wl_event_loop_get_fd((struct wl_event_loop*)(intptr_t)event_loop_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    addEventLoopDestroyListener
 * Signature: (JLorg/freedesktop/wayland/server/Listener;)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_addEventLoopDestroyListener(JNIEnv *const env,
                                                                                         const jclass  class,
                                                                                         const jlong   event_loop_p,
                                                                                         const jlong   listener_p){
    struct wl_listener_jobject* listener_jobject = (struct wl_listener_jobject*)(intptr_t)listener_p;
    wl_event_loop_add_destroy_listener((struct wl_event_loop*)(intptr_t)event_loop_p,
                                       &listener_jobject->listener_p);
}

static
void jni_global_bind_func(struct wl_client *client,
                         void *data,
                         uint32_t version,
                         uint32_t id){
    const jobject jglobal = (jobject) data;
    JNIEnv *const env;
    GET_ATTACHED_JENV(env);
    (*env)->CallVoidMethod(env,
                           jglobal,
                           j_func_bind_callback,
                           (jlong)(intptr_t)client,
                           (jint)version,
                           (jint)id);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    createGlobal
 * Signature: (JLorg/freedesktop/wayland/Interface;I)J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_createGlobal(JNIEnv *const env,
                                                                           const jclass  class,
                                                                           const jlong   display_p,
                                                                           const jlong   interface_p,
                                                                           const jint    version,
                                                                           const jobject jglobal){
    return (jlong)(intptr_t)wl_global_create((struct wl_display*)(intptr_t)display_p,
                                             (struct wl_interface*)(intptr_t)interface_p,
                                             (int)version,
                                             (*env)->NewGlobalRef(env,
                                                                  jglobal),
                                             &jni_global_bind_func);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    registerBindClientHandler
 * Signature: (Ljava/lang/Class;)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_registerBindClientHandler(JNIEnv *const env,
                                                                                       const jclass  class,
                                                                                       const jclass  global_class_t){
    global_class = (*env)->NewGlobalRef(env,
                                        global_class_t);
    j_func_bind_callback = (*env)->GetMethodID(env,
                                               global_class,
                                               "bindClient",
                                               "(JII)V");
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    createResource
 * Signature: (JLorg/freedesktop/wayland/Interface;II)J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_createResource(JNIEnv *const env,
                                                                             const jclass  class,
                                                                             const jlong   client_p,
                                                                             const jlong   interface_p,
                                                                             const jint    version,
                                                                             const jint    id){
    return (jlong)(intptr_t)wl_resource_create((struct wl_client*)(intptr_t)client_p,
                                               (struct wl_interface*)(intptr_t)interface_p,
                                               (int)version,
                                               (uint32_t)id);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    getId
 * Signature: (J)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_getId(JNIEnv *const env,
                                                                   const jclass  class,
                                                                   const jlong   resource_p){
    return (jint)wl_resource_get_id((struct wl_resource*)(intptr_t)resource_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    getVersion
 * Signature: (J)I
 */
JNIEXPORT
jint JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_getVersion(JNIEnv *const env,
                                                                        const jclass  class,
                                                                        const jlong   resource_p){
    return (jint)wl_resource_get_version((struct wl_resource*)(intptr_t)resource_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    getInterface
 * Signature: (J)J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_getInterface(JNIEnv *const env,
                                                                           const jclass  class,
                                                                           const jlong   resource_p){
    struct wl_resource* resource = (struct wl_resource*)(intptr_t)resource_p;
    return (jlong)(intptr_t)resource->object.interface;
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    addResourceDestroyListener
 * Signature: (JLorg/freedesktop/wayland/server/DestroyListener;)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_addResourceDestroyListener(JNIEnv *const env,
                                                                                        const jclass  class,
                                                                                        const jlong   resource_p,
                                                                                        const jlong   listener_p){
	struct wl_listener_jobject* listener_jobject = (struct wl_listener_jobject*)(intptr_t)listener_p;
	wl_resource_add_destroy_listener((struct wl_resource*)(intptr_t)resource_p,
									 &listener_jobject->listener_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    destroyResource
 * Signature: (J)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_destroyResource(JNIEnv *const env,
                                                                             const jclass  class,
                                                                             const jlong   resource_p){
	wl_resource_destroy((struct wl_resource*)(intptr_t)resource_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    postEvent
 * Signature: (JJIJ)V
 */
 JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_postEvent(JNIEnv *const env,
                                                                       const jclass  class,
                                                                       const jlong   resource_p,
                                                                       const jint    opcode,
                                                                       const jlong   args){
	wl_resource_post_event_array((struct wl_resource*)(intptr_t)resource_p,
								 (uint32_t)opcode,
								 (union wl_argument *)(intptr_t)args);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    postError
 * Signature: (JILjava/lang/String;)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_postError(JNIEnv *const env,
                                                                       const jclass  class,
                                                                       const jlong   resource_p,
                                                                       const jint    code,
                                                                       const jstring msg){
    const char *msg_p = (*env)->GetStringUTFChars(env,
                                                  msg,
                                                  JNI_FALSE);
	wl_resource_post_error((struct wl_resource*)(intptr_t)resource_p,
			               (uint32_t)code,
			               msg_p);
    (*env)->ReleaseStringUTFChars(env,
                                  msg,
                                  msg_p);
}



/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    getClient
 * Signature: (J)J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_getClient(JNIEnv *const env,
                                                                        const jclass  class,
                                                                        const jlong   resource_p){
	return (jlong)(intptr_t)wl_resource_get_client((struct wl_resource*)(intptr_t)resource_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    getImplementation
 * Signature: (J)Ljava/lang/Object;
 */
JNIEXPORT
jobject JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_getImplementation(JNIEnv *const env,
                                                                                  const jclass  class,
                                                                                  const jlong   resource_p){
    struct wl_resource* resource = (struct wl_resource*)(intptr_t)resource_p;
    return (jobject)resource->object.implementation;
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    createClient
 * Signature: (JI)J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_createClient(JNIEnv *const env,
                                                                           const jclass  class,
                                                                           const jlong   display_p,
                                                                           const jint    fd){
    return (jlong)(intptr_t)wl_client_create((struct wl_display*)(intptr_t)display_p,
                                             (int)fd);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    flush
 * Signature: (J)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_flush(JNIEnv *const env,
                                                                   const jclass  class,
                                                                   const jlong   client_p){
	return wl_client_flush((struct wl_client*)(intptr_t)client_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    addClientDestroyListener
 * Signature: (JLorg/freedesktop/wayland/server/DestroyListener;)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_addClientDestroyListener(JNIEnv *const env,
                                                                                      const jclass  class,
                                                                                      const jlong   client_p,
                                                                                      const jlong	listener_p){
	struct wl_listener_jobject* listener_jobject = (struct wl_listener_jobject*)(intptr_t)listener_p;
	wl_client_add_destroy_listener((struct wl_client*)(intptr_t)client_p,
								   &listener_jobject->listener_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    destroyClient
 * Signature: (J)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_destroyClient(JNIEnv *const env,
                                                                           const jclass  class,
                                                                           const jlong   client_p){
	wl_client_destroy((struct wl_client*)(intptr_t)client_p);
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    getDisplay
 * Signature: (J)J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_getDisplay(JNIEnv *const env,
                                                                         const jclass  class,
                                                                         const jlong   client_p){
	return (jlong)(intptr_t)wl_client_get_display((struct wl_client*)(intptr_t)client_p);
}

//wl_message

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    createMessage
 * Signature: (Ljava/lang/String;Ljava/lang/String;[J)J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_createMessage(JNIEnv *const    env,
                                                                            const jclass     class,
                                                                            const jstring    signature,
                                                                            const jstring    name,
                                                                            const jlongArray type_interface_pointers){
    const char *signature_p = (*env)->GetStringUTFChars(env,
                                                        signature,
                                                        JNI_FALSE);
    const char *name_p = (*env)->GetStringUTFChars(env,
                                                   name,
                                                   JNI_FALSE);
    const jsize len = (*env)->GetArrayLength(env,
                                             type_interface_pointers);
    jlong *body = (*env)->GetLongArrayElements(env,
                                               type_interface_pointers,
                                               0);

    const struct wl_interface** types = malloc(len * sizeof(struct wl_interface*));
    int i;
    for(i=0;i < len;i++){
        types[i] = (struct wl_interface*)(intptr_t)body[i];
    }

    struct wl_message* message = malloc(sizeof(struct wl_message));
    message->name = name_p;
    message->signature = signature_p;
    message->types = types;

    return (jlong)(intptr_t)message;
}

/*
 * Class:     org_freedesktop_wayland_server_WlServerJNI
 * Method:    createInterface
 * Signature: (Ljava/lang/String;I[J[J)J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_server_WlServerJNI_createInterface(JNIEnv *const    env,
                                                                              const jclass     class,
                                                                              const jstring    name,
                                                                              const jint       version,
                                                                              const jlongArray method_message_pointers,
                                                                              const jlongArray event_message_pointers){
    const char *name_p = (*env)->GetStringUTFChars(env,
                                                   name,
                                                   JNI_FALSE);
    const jsize method_len = (*env)->GetArrayLength(env,
                                                    method_message_pointers);
    jlong *method_body = (*env)->GetLongArrayElements(env,
                                                      method_message_pointers,
                                                      0);
    struct wl_message * methods = malloc(method_len * sizeof(struct wl_message*));
    int i;
    for(i = 0; i < method_len;i++){
        methods[i] = *((struct wl_message *)(intptr_t)method_body[i]);
    }

    const jsize event_len = (*env)->GetArrayLength(env,
                                                   event_message_pointers);
    jlong *event_body = (*env)->GetLongArrayElements(env,
                                                     event_message_pointers,
                                                     0);
    struct wl_message * events = malloc(event_len * sizeof(struct wl_message*));
    int j;
    for(j=0;j < event_len;j++){
        events[j] = *((struct wl_message *)(intptr_t)event_body[j]);
    }

    struct wl_interface* interface = malloc(sizeof(struct wl_interface));
    interface->name = name_p;
    interface->method_count = method_len;
    interface->methods = methods;
    interface->event_count = event_len;
    interface->events = events;

    return (jlong)(intptr_t) interface;
}

JNIEXPORT
jint JNI_OnLoad(JavaVM *vm, void *reserved){
    jvm = vm;
    return JNI_VERSION_1_6;
}
