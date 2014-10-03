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
	return (jlong)(intptr_t)wl_egl_window_create((struct wl_surface*)(intptr_t)surface_p,
												 (int)width,
												 (int)height);
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
	wl_egl_window_destroy((struct wl_egl_window*)(intptr_t)egl_window_p);
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
	wl_egl_window_resize((struct wl_egl_window*)(intptr_t)egl_window_p,
						 (int)width,
						 (int)height,
						 (int)dx,
						 (int)dy);
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
                                                                              jintArray     size_arr){
	int width;
	int height;
	wl_egl_window_get_attached_size((struct wl_egl_window*)(intptr_t)egl_window_p,
									&width,
									&height);
	jint *size = (*env)->GetIntArrayElements(env,
											 size_arr,
											 0);
	size[0] = width;
	size[1] = height;
}
