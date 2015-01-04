package org.freedesktop.wayland.server.jna;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

public interface wl_event_loop_fd_func_t extends Callback {
    int apply(int fd,
              int mask,
              Pointer data);
}
