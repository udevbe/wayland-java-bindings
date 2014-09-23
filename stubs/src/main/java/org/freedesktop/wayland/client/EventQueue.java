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
package org.freedesktop.wayland.client;

import org.freedesktop.wayland.util.HasPointer;
import org.freedesktop.wayland.util.ObjectCache;

/**
 * A queue for {@link Proxy} object events.
 * <p/>
 * Event queues allows the events on a display to be handled in a thread-safe
 * manner.
 *
 * @see Display
 */
public class EventQueue implements HasPointer {
    private final long pointer;

    protected EventQueue(final long pointer) {
        this.pointer = pointer;
        ObjectCache.store(pointer,
                          this);
    }

    public long getPointer() {
        return this.pointer;
    }

    /**
     * Destroy an event queue
     * <p/>
     * Destroy the given event queue. Any pending event on that queue is
     * discarded.
     * <p/>
     * The {@link Display} object used to create the queue should not be
     * destroyed until all event queues created with it are destroyed with
     * this function.
     */
    public void destroy() {
        WlClientJNI.destroyEventQueue(getPointer());
        ObjectCache.remove(getPointer());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final EventQueue that = (EventQueue) o;

        return getPointer() == that.getPointer();
    }

    @Override
    public int hashCode() {
        return (int) getPointer();
    }
}