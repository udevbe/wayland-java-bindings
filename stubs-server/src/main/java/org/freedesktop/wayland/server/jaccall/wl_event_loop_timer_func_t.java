package org.freedesktop.wayland.server.jaccall;

import org.freedesktop.jaccall.Functor;
import org.freedesktop.jaccall.Ptr;

@Functor
public interface wl_event_loop_timer_func_t {
    int invoke(@Ptr(Object.class) long data);
}
