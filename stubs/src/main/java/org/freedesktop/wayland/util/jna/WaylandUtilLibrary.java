package org.freedesktop.wayland.util.jna;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;

public interface WaylandUtilLibrary extends Library {

    /**
     * \brief A function pointer type for a dispatcher.
     * <p/>
     * A dispatcher is a function that handles the emitting of callbacks in client
     * code.  For programs directly using the C library, this is done by using
     * libffi to call function pointers.  When binding to languages other than C,
     * dispatchers provide a way to abstract the function calling process to be
     * friendlier to other function calling systems.
     * <p/>
     * A dispatcher takes five arguments:  The first is the dispatcher-specific
     * implementation data associated with the target object.  The second is the
     * object on which the callback is being invoked (either wl_proxy or
     * wl_resource).  The third and fourth arguments are the opcode the wl_messsage
     * structure corresponding to the callback being emitted.  The final argument
     * is an array of arguments recieved from the other process via the wire
     * protocol.
     */
    public interface wl_dispatcher_func_t extends Callback {
        int apply(Pointer implementation,
                  Pointer wlObject,
                  int opcode,
                  wl_message wlMessage,
                  wl_argument wl_arguments);
    }

    public interface wl_log_func_t extends Callback {
        void apply(Pointer charPtr1,
                   Object... va_list1);
    }

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
