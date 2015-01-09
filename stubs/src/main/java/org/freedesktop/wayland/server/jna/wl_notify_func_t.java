package org.freedesktop.wayland.server.jna;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

public interface wl_notify_func_t extends Callback {
    void apply(wl_listener listener,
               long data);
}
