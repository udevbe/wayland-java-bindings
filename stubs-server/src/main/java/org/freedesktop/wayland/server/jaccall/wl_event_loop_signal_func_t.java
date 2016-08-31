package org.freedesktop.wayland.server.jaccall;

import org.freedesktop.jaccall.Functor;
import org.freedesktop.jaccall.Ptr;

@Functor
public interface wl_event_loop_signal_func_t {
    int $(int signal_number,
          @Ptr(Object.class) long data);
}
