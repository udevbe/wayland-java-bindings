package org.freedesktop.wayland.server.jna;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

public interface wl_event_loop_signal_func_t extends Callback {
    int apply(int signal_number,
              long data);
}
