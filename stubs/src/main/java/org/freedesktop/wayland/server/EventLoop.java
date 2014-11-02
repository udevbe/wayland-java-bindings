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

import org.freedesktop.wayland.HasPointer;
import org.freedesktop.wayland.util.ObjectCache;
import org.freedesktop.wayland.util.WlUtilJNI;

public class EventLoop implements HasPointer {

    public static final int EVENT_READABLE = 0x01;
    public static final int EVENT_WRITABLE = 0x02;
    public static final int EVENT_HANGUP   = 0x04;
    public static final int EVENT_ERROR    = 0x08;

    private final long pointer;

    protected EventLoop(final long pointer) {
        this.pointer = pointer;
        ObjectCache.store(getPointer(),
                          this);
    }

    public static EventLoop create() {
        return new EventLoop(WlServerJNI.createEventLoop());
    }

    public EventSource addFileDescriptor(final int fd,
                                         final int mask,
                                         final FileDescriptorEventHandler handler) {
        return EventSource.create(WlServerJNI.addFileDescriptor(getPointer(),
                                                                fd,
                                                                mask,
                                                                handler));
    }

    public EventSource addTimer(final TimerEventHandler handler) {
        return EventSource.create(WlServerJNI.addTimer(getPointer(),
                                                       handler));
    }

    public EventSource addSignal(final int signalNumber,
                                 final SignalEventHandler handler) {
        return EventSource.create(WlServerJNI.addSignal(getPointer(),
                                                        signalNumber,
                                                        handler));
    }

    public EventSource addIdle(final IdleHandler handler) {
        return EventSource.create(WlServerJNI.addIdle(getPointer(),
                                                      handler));
    }

    public int dispatch(final int timeout) {
        return WlServerJNI.dispatch(getPointer(),
                                    timeout);
    }

    public void dispatchIdle() {
        WlServerJNI.dispatchIdle(getPointer());
    }

    public int getFileDescriptor() {
        return WlServerJNI.getFileDescriptor(getPointer());
    }

    public void addDestroyListener(final Listener listener) {
        WlServerJNI.addEventLoopDestroyListener(getPointer(),
                                                listener.getPointer());
    }

    public void destroy() {
        ObjectCache.remove(getPointer());
        WlUtilJNI.free(getPointer());
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

        final EventLoop eventLoop = (EventLoop) o;

        return getPointer() == eventLoop.getPointer();
    }

    @Override
    public int hashCode() {
        return (int) getPointer();
    }

    public interface FileDescriptorEventHandler {
        int handle(int fd,
                   int mask);
    }

    public interface TimerEventHandler {
        int handle();
    }

    public interface SignalEventHandler {
        int handle(int signalNumber);
    }

    public interface IdleHandler {
        void handle();
    }
}

