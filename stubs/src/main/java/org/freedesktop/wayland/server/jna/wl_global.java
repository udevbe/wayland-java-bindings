package org.freedesktop.wayland.server.jna;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class wl_global extends PointerType {
    public wl_global(final Pointer address) {
        super(address);
    }

    public wl_global() {
        super();
    }
}
