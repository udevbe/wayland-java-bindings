package org.freedesktop.wayland.client.jna;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class wl_proxy extends PointerType {
    public wl_proxy(final Pointer address) {
        super(address);
    }

    public wl_proxy() {
        super();
    }
}
