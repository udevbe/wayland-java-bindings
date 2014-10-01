#include <wayland-egl.h>
#include "WlEglJNI.h"

/*
 * Class:     org_freedesktop_wayland_client_egl_WlEglJNI
 * Method:    createEglWindow
 * Signature: (JII)J
 */
JNIEXPORT
jlong JNICALL Java_org_freedesktop_wayland_client_egl_WlEglJNI_createEglWindow(JNIEnv *const env,
                                                                               const jclass  class,
                                                                               const jlong  surface_p,
                                                                               const jint   width,
                                                                               const jint   height){
}

/*
 * Class:     org_freedesktop_wayland_client_egl_WlEglJNI
 * Method:    destroyEglWindow
 * Signature: (J)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_client_egl_WlEglJNI_destroyEglWindow(JNIEnv *const env,
                                                                               const jclass  class,
                                                                               const jlong   egl_window_p){
}

/*
 * Class:     org_freedesktop_wayland_client_egl_WlEglJNI
 * Method:    resize
 * Signature: (JIIII)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_client_egl_WlEglJNI_resize(JNIEnv *const env,
                                                                     const jclass  class,
                                                                     const jlong   egl_window_p,
                                                                     const jint    width,
                                                                     const jint    height,
                                                                     const jint    dx,
                                                                     const jint    dy){
}

/*
 * Class:     org_freedesktop_wayland_client_egl_WlEglJNI
 * Method:    getAttachedSize
 * Signature: (J[I)V
 */
JNIEXPORT
void JNICALL Java_org_freedesktop_wayland_client_egl_WlEglJNI_getAttachedSize(JNIEnv *const env,
                                                                              const jclass  class,
                                                                              const jlong   egl_window_p,
                                                                              jintArray     size){
}