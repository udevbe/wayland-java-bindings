package org.freedesktop.wayland.server.jna;

import com.sun.jna.Callback;

public interface wl_global_bind_func_t extends Callback {
    void apply(long client,
               long data,
               int version,
               int id);
}
