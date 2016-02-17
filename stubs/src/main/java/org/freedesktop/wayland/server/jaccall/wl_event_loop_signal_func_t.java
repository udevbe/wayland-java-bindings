package org.freedesktop.wayland.server.jaccall;

import com.github.zubnix.jaccall.Functor;
import com.github.zubnix.jaccall.Ptr;

@FunctionalInterface
@Functor
public interface wl_event_loop_signal_func_t {
    int $(int signal_number, @Ptr(void.class) long data);
}
