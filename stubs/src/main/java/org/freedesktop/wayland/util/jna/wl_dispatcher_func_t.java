package org.freedesktop.wayland.util.jna;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

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
              wl_argument.ByReference wl_arguments);
}
