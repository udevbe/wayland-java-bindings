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

import com.github.zubnix.jaccall.Pointer;
import com.github.zubnix.jaccall.Ptr;
import org.freedesktop.wayland.HasNative;
import org.freedesktop.wayland.server.jaccall.Pointerwl_notify_func_t;
import org.freedesktop.wayland.server.jaccall.WaylandServerCore;
import org.freedesktop.wayland.server.jaccall.wl_listener;
import org.freedesktop.wayland.server.jaccall.wl_notify_func_t;
import org.freedesktop.wayland.util.ObjectCache;

import static com.github.zubnix.jaccall.Pointer.ref;

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
abstract class Listener implements HasNative<Pointer<wl_listener>> {

    private static final wl_notify_func_t WL_NOTIFY_FUNC = new wl_notify_func_t() {

        @Override
        public void $(@Ptr(wl_listener.class) final long listenerPointer,
                      @Ptr(void.class) final long data) {
            final Listener listener = ObjectCache.from(Pointer.wrap(listenerPointer));
            listener.handle();
        }
    };

    private final Pointer<wl_listener> pointer;

    public Listener() {
        this.pointer = Pointer.malloc(wl_listener.SIZE,
                                      wl_listener.class);
        this.pointer.dref()
                    .notify$(Pointerwl_notify_func_t.nref(WL_NOTIFY_FUNC));
        ObjectCache.store(getNative(),
                          this);
    }

    public Pointer<wl_listener> getNative() {
        return this.pointer;
    }

    public void remove() {
        WaylandServerCore.INSTANCE()
                         .wl_list_remove(ref(getNative().dref()
                                                        .link()).address);
    }

    public void free() {
        ObjectCache.remove(getNative());
        this.pointer.close();
    }

    //called from jni
    public abstract void handle();

    @Override
    public int hashCode() {
        return getNative().hashCode();
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
}

