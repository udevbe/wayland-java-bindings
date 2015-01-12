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
import org.freedesktop.wayland.server.jna.*;
import org.freedesktop.wayland.util.ObjectCache;

import java.util.Map;
import java.util.WeakHashMap;

public class EventLoop implements HasNative<Pointer> {

    public static final int EVENT_READABLE = 0x01;
    public static final int EVENT_WRITABLE = 0x02;
    public static final int EVENT_HANGUP   = 0x04;
    public static final int EVENT_ERROR    = 0x08;

    private final Map<Object, Object> weakNativeCallbackReferences = new WeakHashMap<Object, Object>();

    private final Pointer pointer;

    protected EventLoop(final Pointer pointer) {
        this.pointer = pointer;
        ObjectCache.store(getNative(),
                          this);
    }

    public static EventLoop create() {
        return new EventLoop(WaylandServerLibrary.INSTANCE()
                                                 .wl_event_loop_create());
    }

    public EventSource addFileDescriptor(final int fd,
                                         final int mask,
                                         final FileDescriptorEventHandler handler) {
        final wl_event_loop_fd_func_t nativeCallback = new wl_event_loop_fd_func_t() {
            @Override
            public int apply(final int fd,
                             final int mask,
                             final Pointer data) {
                handler.handle(fd,
                               mask);
                return 0;
            }
        };
        this.weakNativeCallbackReferences.put(handler,
                                              nativeCallback);
        final Pointer wlEventSource = WaylandServerLibrary.INSTANCE()
                                                          .wl_event_loop_add_fd(getNative(),
                                                                                fd,
                                                                                mask,
                                                                                nativeCallback,
                                                                                Pointer.NULL);
        return EventSource.create(wlEventSource);
    }

    public EventSource addTimer(final TimerEventHandler handler) {
        final wl_event_loop_timer_func_t nativeCallback = new wl_event_loop_timer_func_t() {
            @Override
            public int apply(final Pointer data) {
                handler.handle();
                return 0;
            }
        };
        this.weakNativeCallbackReferences.put(handler,
                                              nativeCallback);
        return EventSource.create(WaylandServerLibrary.INSTANCE()
                                                      .wl_event_loop_add_timer(getNative(),
                                                                               nativeCallback,
                                                                               Pointer.NULL));
    }

    public EventSource addSignal(final int signalNumber,
                                 final SignalEventHandler handler) {
        final wl_event_loop_signal_func_t callback = new wl_event_loop_signal_func_t() {

            @Override
            public int apply(final int signal_number,
                             final Pointer data) {
                handler.handle(signalNumber);
                return 0;
            }
        };
        this.weakNativeCallbackReferences.put(handler,
                                              callback);
        final Pointer wlEventSource = WaylandServerLibrary.INSTANCE()
                                                          .wl_event_loop_add_signal(getNative(),
                                                                                    signalNumber,
                                                                                    callback,
                                                                                    Pointer.NULL);
        return EventSource.create(wlEventSource);
    }

    public EventSource addIdle(final IdleHandler handler) {
        final wl_event_loop_idle_func_t callback = new wl_event_loop_idle_func_t() {
            @Override
            public void apply(final Pointer data) {
                handler.handle();
            }
        };
        this.weakNativeCallbackReferences.put(handler,
                                              callback);
        final Pointer wlEventSource = WaylandServerLibrary.INSTANCE()
                                                          .wl_event_loop_add_idle(getNative(),
                                                                                  callback,
                                                                                  Pointer.NULL);
        return EventSource.create(wlEventSource);
    }

    public int dispatch(final int timeout) {
        return WaylandServerLibrary.INSTANCE()
                                   .wl_event_loop_dispatch(getNative(),
                                                           timeout);
    }

    public void dispatchIdle() {
        WaylandServerLibrary.INSTANCE()
                            .wl_event_loop_dispatch_idle(getNative());
    }

    public int getFileDescriptor() {
        return WaylandServerLibrary.INSTANCE()
                                   .wl_event_loop_get_fd(getNative());
    }

    public void addDestroyListener(final Listener listener) {
        WaylandServerLibrary.INSTANCE()
                            .wl_event_loop_add_destroy_listener(getNative(),
                                                                listener.getNative());
    }

    public void destroy() {
        ObjectCache.remove(getNative());
        WaylandServerLibrary.INSTANCE()
                            .free(getNative());
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

        final EventLoop eventLoop = (EventLoop) o;

        return getNative().equals(eventLoop.getNative());
    }

    @Override
    public int hashCode() {
        return getNative().hashCode();
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

