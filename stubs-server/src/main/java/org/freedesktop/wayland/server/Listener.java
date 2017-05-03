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
package org.freedesktop.wayland.server;

import org.freedesktop.jaccall.Pointer;
import org.freedesktop.wayland.server.jaccall.Pointerwl_notify_func_t;
import org.freedesktop.wayland.server.jaccall.WaylandServerCore;
import org.freedesktop.wayland.server.jaccall.wl_listener;
import org.freedesktop.wayland.server.jaccall.wl_notify_func_t;
import org.freedesktop.wayland.util.ObjectCache;

import static org.freedesktop.jaccall.Pointer.ref;

/**
 * A single listener for Wayland signals
 * <p/>
 * {@code Listener} provides the means to listen for {@code wl_signal} notifications. Many
 * Wayland objects use {@code Listener} for notification of significant events like
 * object destruction.
 * <p/>
 * Clients should create {@code Listener} objects manually and can register them as
 * listeners to signals using #wl_signal_add, assuming the signal is
 * directly accessible. For opaque structs like wl_event_loop, adding a
 * listener should be done through provided accessor methods. A listener can
 * only listen to one signal at a time.
 */
abstract class Listener {

    private static final Pointer<wl_notify_func_t> WL_NOTIFY_FUNC = Pointerwl_notify_func_t.nref((wl_notify_func_t) (listenerPointer, data) -> {
        final Listener listener = ObjectCache.from(listenerPointer);
        listener.handle();
    });

    public final Pointer<wl_listener> pointer;

    public Listener() {
        this.pointer = Pointer.malloc(wl_listener.SIZE,
                                      wl_listener.class);
        this.pointer.get()
                    .setNotify$(WL_NOTIFY_FUNC);
        ObjectCache.store(this.pointer.address,
                          this);
    }

    public void remove() {
        WaylandServerCore.INSTANCE()
                         .wl_list_remove(ref(this.pointer.get()
                                                         .link()).address);
    }

    public void free() {
        ObjectCache.remove(this.pointer.address);
        this.pointer.close();
    }

    public abstract void handle();

    @Override
    public int hashCode() {
        return new Long(this.pointer.address).hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Listener)) {
            return false;
        }

        final Listener listener = (Listener) o;

        return this.pointer.address == listener.pointer.address;
    }
}

