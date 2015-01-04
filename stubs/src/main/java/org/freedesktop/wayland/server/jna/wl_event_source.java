package org.freedesktop.wayland.server.jna;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class wl_event_source extends PointerType {
    public wl_event_source(final Pointer address) {
        super(address);
    }
    public wl_event_source() {
        super();
    }
}
