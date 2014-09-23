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

import org.freedesktop.wayland.util.HasPointer;
import org.freedesktop.wayland.util.ObjectCache;

public class EventSource implements HasPointer {
    private final long pointer;

    protected EventSource(final long pointer) {
        this.pointer = pointer;
        ObjectCache.store(getPointer(),
                          this);
    }

    protected static EventSource create(final long pointer) {
        return new EventSource(pointer);
    }

    public int updateFileDescriptor(final int mask) {
        return WlServerJNI.updateFileDescriptor(getPointer(),
                                                mask);
    }

    public int updateTimer(final int msDelay) {
        return WlServerJNI.updateTimer(getPointer(),
                                       msDelay);
    }

    public int remove() {
        ObjectCache.remove(getPointer());
        return WlServerJNI.remove(getPointer());
    }

    public void check() {
        WlServerJNI.check(getPointer());
    }

    public long getPointer() {
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

        return getPointer() == that.getPointer();

    }

    @Override
    public int hashCode() {
        return (int) getPointer();
    }
}
