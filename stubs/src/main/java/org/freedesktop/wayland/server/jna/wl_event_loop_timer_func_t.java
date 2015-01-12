package org.freedesktop.wayland.server.jna;

import com.sun.jna.Callback;

public interface wl_event_loop_timer_func_t extends Callback {
    int apply(long data);
}
