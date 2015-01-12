package org.freedesktop.wayland.server.jna;

import com.sun.jna.Callback;

public interface wl_event_loop_idle_func_t extends Callback {
    void apply(long data);
}
