package org.freedesktop.wayland.client.egl.jna;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class wl_egl_window extends PointerType {
    public wl_egl_window(final Pointer address) {
        super(address);
    }

    public wl_egl_window() {
        super();
    }
}
