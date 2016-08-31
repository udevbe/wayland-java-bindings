package org.freedesktop.wayland.server.jaccall;

import org.freedesktop.jaccall.Functor;
import org.freedesktop.jaccall.Ptr;
import org.freedesktop.jaccall.Unsigned;

@Functor
public interface wl_global_bind_func_t {
    void $(@Ptr long client,
           @Ptr(Object.class) long data,
           @Unsigned int version,
           @Unsigned int id);
}
