package org.freedesktop.wayland.server.jna;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class wl_resource extends PointerType {

    public wl_resource() {
    }

    public wl_resource(final Pointer p) {
        super(p);
    }
}
