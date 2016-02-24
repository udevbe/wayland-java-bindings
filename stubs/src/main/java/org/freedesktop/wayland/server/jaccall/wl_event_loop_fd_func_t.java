package org.freedesktop.wayland.server.jaccall;

import com.github.zubnix.jaccall.Functor;
import com.github.zubnix.jaccall.JObject;
import com.github.zubnix.jaccall.Ptr;
import com.github.zubnix.jaccall.Unsigned;

@Functor
public interface wl_event_loop_fd_func_t {
    int $(int fd,
          @Unsigned int mask,
          @Ptr(JObject.class) long data);
}
