package org.freedesktop.wayland.server.jna;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

public interface wl_global_bind_func_t extends Callback {
    void apply(wl_client client,
               Pointer data,
               int version,
               int id);
}
