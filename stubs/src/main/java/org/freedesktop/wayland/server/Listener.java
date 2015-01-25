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

import org.freedesktop.wayland.HasNative;
import org.freedesktop.wayland.server.jna.WaylandServerLibrary;
import org.freedesktop.wayland.server.jna.wl_listener;
import org.freedesktop.wayland.server.jna.wl_notify_func_t;
import org.freedesktop.wayland.util.ObjectCache;

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
public abstract class Listener implements HasNative<wl_listener> {

    private final wl_listener pointer;

    private boolean valid;

    public Listener() {
        this.pointer = new wl_listener();
        this.valid = true;
        this.pointer.notify$ = new wl_notify_func_t() {
            @Override
            public void apply() {
                Listener.this.handle();
            }
        };
        ObjectCache.store(getNative().getPointer(),
                          this);
    }

    public void remove() {
        if (isValid()) {
            this.valid = false;
            ObjectCache.remove(getNative().getPointer());
            WaylandServerLibrary.INSTANCE()
                                .wl_list_remove(this.pointer.link.getPointer());
        }
    }

    //called from jni
    public abstract void handle();

    public wl_listener getNative() {
        return this.pointer;
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

        return getNative().equals(listener.getNative());
    }

    @Override
    public int hashCode() {
        return getNative().hashCode();
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }
}

