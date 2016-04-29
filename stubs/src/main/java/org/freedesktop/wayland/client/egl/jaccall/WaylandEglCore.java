package org.freedesktop.wayland.client.egl.jaccall;


import org.freedesktop.jaccall.Lib;
import org.freedesktop.jaccall.Ptr;

@Lib("wayland-egl")
public class WaylandEglCore {

    private static WaylandEglCore INSTANCE;

    public static WaylandEglCore INSTANCE() {
        if (INSTANCE == null) {
            new WaylandEglCore_Symbols().link();
            INSTANCE = new WaylandEglCore();
        }
        return INSTANCE;
    }

    public static final int WL_EGL_PLATFORM = 1;

    @Ptr
    public native long wl_egl_window_create(@Ptr long surface,
                                            int width,
                                            int height);

    public native void wl_egl_window_destroy(@Ptr long egl_window);

    public native void wl_egl_window_resize(@Ptr long egl_window,
                                            int width,
                                            int height,
                                            int dx,
                                            int dy);

    public native void wl_egl_window_get_attached_size(@Ptr long egl_window,
                                                       @Ptr(int.class) long width,
                                                       @Ptr(int.class) long height);
}
