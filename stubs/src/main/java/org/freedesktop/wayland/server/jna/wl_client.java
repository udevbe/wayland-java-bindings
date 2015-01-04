package org.freedesktop.wayland.server.jna;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class wl_client extends PointerType {
    public wl_client(final Pointer address) {
        super(address);
    }
    public wl_client() {
        super();
    }
}
