package org.freedesktop.wayland.util.jna;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class wl_object extends PointerType {
    public wl_object(final Pointer address) {
        super(address);
    }

    public wl_object() {
        super();
    }
}
