package org.freedesktop.wayland.server.jaccall;

import com.github.zubnix.jaccall.Functor;
import com.github.zubnix.jaccall.Ptr;

@Functor
public interface wl_event_loop_timer_func_t {
    int $(@Ptr(Object.class) long data);
}
