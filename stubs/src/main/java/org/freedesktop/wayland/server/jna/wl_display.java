package org.freedesktop.wayland.server.jna;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class wl_display extends PointerType {
    public wl_display(final Pointer address) {
        super(address);
    }

    public wl_display() {
        super();
    }
}
