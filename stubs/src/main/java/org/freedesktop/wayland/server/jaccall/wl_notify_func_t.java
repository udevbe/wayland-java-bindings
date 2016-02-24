package org.freedesktop.wayland.server.jaccall;

import com.github.zubnix.jaccall.Functor;
import com.github.zubnix.jaccall.Ptr;
@Functor
public interface wl_notify_func_t {
    void $(@Ptr(wl_listener.class) long listener, @Ptr(void.class) long data);
}
