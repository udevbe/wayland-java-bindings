package org.freedesktop.wayland.util.jna;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

public interface WaylandUtilLibrary extends Library {

    void wl_list_init(wl_list list);

    void wl_list_insert(wl_list list,
                        wl_list elm);

    void wl_list_remove(wl_list elm);

    int wl_list_length(wl_list list);

    int wl_list_empty(wl_list list);

    void wl_list_insert_list(wl_list list,
                             wl_list other);

    void wl_array_init(wl_array array);

    void wl_array_release(wl_array array);

    Pointer wl_array_add(wl_array array,
                         long size);

    int wl_array_copy(wl_array array,
                      wl_array source);

    void free(Pointer pointer);
}
