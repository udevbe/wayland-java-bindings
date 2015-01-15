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
import org.freedesktop.wayland.util.ObjectCache;

public class EventSource implements HasNative<Pointer> {
    private final Pointer pointer;

    protected EventSource(final Pointer pointer) {
        this.pointer = pointer;
    }

    protected static EventSource create(final Pointer pointer) {
        return new EventSource(pointer);
    }

    public int updateFileDescriptor(final int mask) {
        return WaylandServerLibrary.INSTANCE()
                                   .wl_event_source_fd_update(getNative(),
                                                              mask);
    }

    public int updateTimer(final int msDelay) {
        return WaylandServerLibrary.INSTANCE()
                                   .wl_event_source_timer_update(getNative(),
                                                                 msDelay);
    }

    public int remove() {
        ObjectCache.remove(getNative());
        return WaylandServerLibrary.INSTANCE()
                                   .wl_event_source_remove(getNative());
    }

    public void check() {
        WaylandServerLibrary.INSTANCE()
                            .wl_event_source_check(getNative());
    }

    public Pointer getNative() {
        return this.pointer;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final EventSource that = (EventSource) o;

        return getNative().equals(that.getNative());

    }

    @Override
    public int hashCode() {
        return getNative().hashCode();
    }

    @Override
    public boolean isValid() {
        //no way to track event source lifecycle, so it's always valid to us.
        return true;
    }
}
