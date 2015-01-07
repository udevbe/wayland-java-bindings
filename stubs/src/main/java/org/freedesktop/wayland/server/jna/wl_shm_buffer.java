package org.freedesktop.wayland.server.jna;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class wl_shm_buffer extends PointerType {
    public wl_shm_buffer(final Pointer address) {
        super(address);
    }

    public wl_shm_buffer() {
        super();
    }
}
