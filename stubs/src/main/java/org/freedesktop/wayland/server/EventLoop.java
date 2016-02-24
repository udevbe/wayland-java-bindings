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

import com.github.zubnix.jaccall.JObject;
import com.github.zubnix.jaccall.Pointer;
import com.github.zubnix.jaccall.Ptr;
import com.github.zubnix.jaccall.Unsigned;
import org.freedesktop.wayland.server.jaccall.WaylandServerCore;
import org.freedesktop.wayland.server.jaccall.wl_event_loop_fd_func_t;
import org.freedesktop.wayland.server.jaccall.wl_event_loop_idle_func_t;
import org.freedesktop.wayland.server.jaccall.wl_event_loop_signal_func_t;
import org.freedesktop.wayland.server.jaccall.wl_event_loop_timer_func_t;
import org.freedesktop.wayland.util.ObjectCache;

import java.util.HashSet;
import java.util.Set;

import static com.github.zubnix.jaccall.Pointer.malloc;
import static com.github.zubnix.jaccall.Pointer.wrap;
import static com.github.zubnix.jaccall.Size.sizeof;
import static org.freedesktop.wayland.server.jaccall.Pointerwl_event_loop_fd_func_t.nref;
import static org.freedesktop.wayland.server.jaccall.Pointerwl_event_loop_idle_func_t.nref;
import static org.freedesktop.wayland.server.jaccall.Pointerwl_event_loop_signal_func_t.nref;
import static org.freedesktop.wayland.server.jaccall.Pointerwl_event_loop_timer_func_t.nref;

public class EventLoop {

    private static final Pointer<wl_event_loop_fd_func_t> WL_EVENT_LOOP_FD_FUNC = nref(new wl_event_loop_fd_func_t() {
        @Override
        public int $(final int fd,
                     @Unsigned final int mask,
                     @Ptr(JObject.class) final long data) {
            final Pointer<JObject> objectPointer = wrap(JObject.class,
                                                        data);
            final FileDescriptorEventHandler handler = (FileDescriptorEventHandler) objectPointer.dref()
                                                                                                 .pojo();
            return handler.handle(fd,
                                  mask);
        }
    });

    private static final Pointer<wl_event_loop_timer_func_t> WL_EVENT_LOOP_TIMER_FUNC = nref(new wl_event_loop_timer_func_t() {
        @Override
        public int $(@Ptr(JObject.class)
                     final long data) {
            final Pointer<JObject> objectPointer = wrap(JObject.class,
                                                        data);
            final TimerEventHandler handler = (TimerEventHandler) objectPointer.dref()
                                                                               .pojo();
            return handler.handle();
        }
    });

    private static final Pointer<wl_event_loop_signal_func_t> WL_EVENT_LOOP_SIGNAL_FUNC = nref(new wl_event_loop_signal_func_t() {
        @Override
        public int $(final int signal_number,
                     @Ptr(JObject.class) final long data) {
            final Pointer<JObject> objectPointer = wrap(JObject.class,
                                                        data);
            final SignalEventHandler handler = (SignalEventHandler) objectPointer.dref()
                                                                                 .pojo();
            return handler.handle(signal_number);
        }
    });

    private static final Pointer<wl_event_loop_idle_func_t> WL_EVENT_LOOP_IDLE_FUNC = nref(new wl_event_loop_idle_func_t() {
        @Override
        public void $(@Ptr(JObject.class) final long data) {
            final Pointer<JObject> objectPointer = wrap(JObject.class,
                                                        data);
            final IdleHandler handler = (IdleHandler) objectPointer.dref()
                                                                   .pojo();
            handler.handle();
        }
    });


    public final long pointer;
    private final Set<DestroyListener> destroyListeners = new HashSet<DestroyListener>();
    private final Set<JObject>         handlers         = new HashSet<JObject>();

    private EventLoop(final long pointer) {
        this.pointer = pointer;
        addDestroyListener(new Listener() {
            @Override
            public void handle() {
                notifyDestroyListeners();
                EventLoop.this.destroyListeners.clear();
                ObjectCache.remove(EventLoop.this.pointer);
                free();

                releaseHandlers();
            }
        });
        ObjectCache.store(this.pointer,
                          this);
    }

    private void releaseHandlers() {
        for (final JObject handler : this.handlers) {
            handler.close();
        }
        this.handlers.clear();
    }

    private void addDestroyListener(final Listener listener) {
        WaylandServerCore.INSTANCE()
                         .wl_event_loop_add_destroy_listener(this.pointer,
                                                             listener.pointer.address);
    }

    private void notifyDestroyListeners() {
        for (final DestroyListener listener : new HashSet<DestroyListener>(this.destroyListeners)) {
            listener.handle();
        }
    }

    public static EventLoop create() {
        return EventLoop.get(WaylandServerCore.INSTANCE()
                                              .wl_event_loop_create());
    }

    public static EventLoop get(final long pointer) {
        if (pointer == 0L) {
            return null;
        }
        EventLoop eventLoop = ObjectCache.from(pointer);
        if (eventLoop == null) {
            eventLoop = new EventLoop(pointer);
        }
        return eventLoop;
    }

    public EventSource addFileDescriptor(final int fd,
                                         final int mask,
                                         final FileDescriptorEventHandler handler) {
        return EventSource.create(WaylandServerCore.INSTANCE()
                                                   .wl_event_loop_add_fd(this.pointer,
                                                                         fd,
                                                                         mask,
                                                                         WL_EVENT_LOOP_FD_FUNC.address,
                                                                         handlerObjectPointer(handler).address));
    }

    private Pointer<JObject> handlerObjectPointer(final Object handler) {
        final JObject jObject = new JObject(handler);
        this.handlers.add(jObject);

        final Pointer<JObject> jObjectPointer = malloc(sizeof((JObject) null),
                                                       JObject.class);
        jObjectPointer.write(jObject);
        return jObjectPointer;
    }

    public EventSource addTimer(final TimerEventHandler handler) {
        return EventSource.create(WaylandServerCore.INSTANCE()
                                                   .wl_event_loop_add_timer(this.pointer,
                                                                            WL_EVENT_LOOP_TIMER_FUNC.address,
                                                                            handlerObjectPointer(handler).address));
    }

    public EventSource addSignal(final int signalNumber,
                                 final SignalEventHandler handler) {
        return EventSource.create(WaylandServerCore.INSTANCE()
                                                   .wl_event_loop_add_signal(this.pointer,
                                                                             signalNumber,
                                                                             WL_EVENT_LOOP_SIGNAL_FUNC.address,
                                                                             handlerObjectPointer(handler).address));
    }

    public EventSource addIdle(final IdleHandler handler) {
        return EventSource.create(WaylandServerCore.INSTANCE()
                                                   .wl_event_loop_add_idle(this.pointer,
                                                                           WL_EVENT_LOOP_IDLE_FUNC.address,
                                                                           handlerObjectPointer(handler).address));
    }

    public int dispatch(final int timeout) {
        return WaylandServerCore.INSTANCE()
                                .wl_event_loop_dispatch(this.pointer,
                                                        timeout);
    }

    public void dispatchIdle() {
        WaylandServerCore.INSTANCE()
                         .wl_event_loop_dispatch_idle(this.pointer);
    }

    public int getFileDescriptor() {
        return WaylandServerCore.INSTANCE()
                                .wl_event_loop_get_fd(this.pointer);
    }

    public void register(final DestroyListener destroyListener) {
        this.destroyListeners.add(destroyListener);
    }

    public void unregister(final DestroyListener destroyListener) {
        this.destroyListeners.remove(destroyListener);
    }

    @Override
    public int hashCode() {
        return new Long(this.pointer).hashCode();
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

        return this.pointer == eventLoop.pointer;
    }

    public void destroy() {
        WaylandServerCore.INSTANCE()
                         .wl_event_loop_destroy(this.pointer);
        ObjectCache.remove(this.pointer);
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

