package org.freedesktop.wayland.server.jna;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

public interface wl_resource_destroy_func_t extends Callback {
    void apply(Pointer resource);
}
