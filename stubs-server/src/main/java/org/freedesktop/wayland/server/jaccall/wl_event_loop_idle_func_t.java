package org.freedesktop.wayland.server.jaccall;

import org.freedesktop.jaccall.Functor;
import org.freedesktop.jaccall.Ptr;

@Functor
public interface wl_event_loop_idle_func_t {
    void invoke(@Ptr(void.class) long data);
}
