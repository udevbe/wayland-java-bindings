package org.freedesktop.wayland.util.jna;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

public interface wl_log_func_t extends Callback {
    void apply(Pointer charPtr1,
               Object... va_list1);
}
