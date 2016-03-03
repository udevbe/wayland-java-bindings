package org.freedesktop.wayland.server.jaccall;

import com.github.zubnix.jaccall.Functor;
import com.github.zubnix.jaccall.Ptr;
import com.github.zubnix.jaccall.Unsigned;

@Functor
public interface wl_global_bind_func_t {
    void $(@Ptr long client,
           @Ptr(Object.class) long data,
           @Unsigned int version,
           @Unsigned int id);
}
