package org.freedesktop.wayland.util.jna;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

public interface WaylandUtilLibrary extends Library {

    void wl_list_init(Pointer list);

    void wl_list_insert(Pointer list,
                        Pointer elm);

    void wl_list_remove(Pointer elm);

    int wl_list_length(Pointer list);

    int wl_list_empty(Pointer list);

    void wl_list_insert_list(Pointer list,
                             Pointer other);

    void wl_array_init(Pointer array);

    void wl_array_release(Pointer array);

    Pointer wl_array_add(Pointer array,
                         long size);

    int wl_array_copy(Pointer array,
                      Pointer source);

    void free(Pointer pointer);
}
