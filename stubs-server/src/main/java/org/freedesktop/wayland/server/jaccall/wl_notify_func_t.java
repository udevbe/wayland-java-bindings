package org.freedesktop.wayland.server.jaccall;

import org.freedesktop.jaccall.Functor;
import org.freedesktop.jaccall.Ptr;

@Functor
public interface wl_notify_func_t {
    void invoke(@Ptr(wl_listener.class) long listener,
                @Ptr(void.class) long data);
}
