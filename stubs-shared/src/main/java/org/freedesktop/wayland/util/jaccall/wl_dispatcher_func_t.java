//Copyright 2015 Erik De Rijcke
//
//Licensed under the Apache License,Version2.0(the"License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing,software
//distributed under the License is distributed on an"AS IS"BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
package org.freedesktop.wayland.util.jaccall;

import org.freedesktop.jaccall.Functor;
import org.freedesktop.jaccall.Ptr;

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
@Functor
public interface wl_dispatcher_func_t {
    int invoke(@Ptr(Object.class) long implementation,
               @Ptr long wlObject,
               int opcode,
               @Ptr(wl_message.class) long wlMessage,
               @Ptr(wl_argument.class) long wl_arguments);
}
