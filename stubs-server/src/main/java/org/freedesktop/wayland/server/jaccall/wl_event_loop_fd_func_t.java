package org.freedesktop.wayland.server.jaccall;

import org.freedesktop.jaccall.Functor;
import org.freedesktop.jaccall.Ptr;
import org.freedesktop.jaccall.Unsigned;

@Functor
public interface wl_event_loop_fd_func_t {
    int invoke(int fd,
               @Unsigned int mask,
               @Ptr(Object.class) long data);
}
