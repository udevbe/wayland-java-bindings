package org.freedesktop.wayland.server.jna;

import com.sun.jna.Callback;

public interface wl_notify_func_t extends Callback {
    void apply();
}
