package org.freedesktop.wayland.server.jna;

import com.sun.jna.Callback;

public interface wl_resource_destroy_func_t extends Callback {
    void apply(wl_resource resource);
}
