/*
 * Copyright © 2014 Erik De Rijcke
 *
 * Permission to use, copy, modify, distribute, and sell this software and its
 * documentation for any purpose is hereby granted without fee, provided that
 * the above copyright notice appear in all copies and that both that copyright
 * notice and this permission notice appear in supporting documentation, and
 * that the name of the copyright holders not be used in advertising or
 * publicity pertaining to distribution of the software without specific,
 * written prior permission.  The copyright holders make no representations
 * about the suitability of this software for any purpose.  It is provided "as
 * is" without express or implied warranty.
 *
 * THE COPYRIGHT HOLDERS DISCLAIM ALL WARRANTIES WITH REGARD TO THIS SOFTWARE,
 * INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS, IN NO
 * EVENT SHALL THE COPYRIGHT HOLDERS BE LIABLE FOR ANY SPECIAL, INDIRECT OR
 * CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE,
 * DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 * TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE
 * OF THIS SOFTWARE.
 */
package org.freedesktop.wayland.server;

import com.sun.jna.Pointer;
import org.freedesktop.wayland.HasNative;
import org.freedesktop.wayland.server.jna.WaylandServerLibrary;
import org.freedesktop.wayland.server.jna.wl_listener;
import org.freedesktop.wayland.server.jna.wl_notify_func_t;
import org.freedesktop.wayland.util.ObjectCache;

import java.util.HashSet;
import java.util.Set;

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
        if(isValid()) {
            this.valid = false;
            ObjectCache.remove(getNative().getPointer());
            WaylandServerLibrary.INSTANCE()
                .wl_list_remove(this.pointer.link.getPointer());
        }
    }

    public void destroy() {
        ObjectCache.remove(getNative().getPointer());
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

