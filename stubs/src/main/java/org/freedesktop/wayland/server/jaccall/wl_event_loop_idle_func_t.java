package org.freedesktop.wayland.server.jaccall;

import com.github.zubnix.jaccall.Functor;
import com.github.zubnix.jaccall.Ptr;

@Functor
public interface wl_event_loop_idle_func_t {
    void $(@Ptr(void.class) long data);
}
