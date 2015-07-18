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

import com.sun.jna.Pointer;
import org.freedesktop.wayland.HasNative;
import org.freedesktop.wayland.server.jna.WaylandServerLibrary;
import org.freedesktop.wayland.server.jna.wl_event_loop_fd_func_t;
import org.freedesktop.wayland.server.jna.wl_event_loop_idle_func_t;
import org.freedesktop.wayland.server.jna.wl_event_loop_signal_func_t;
import org.freedesktop.wayland.server.jna.wl_event_loop_timer_func_t;
import org.freedesktop.wayland.util.ObjectCache;

import java.util.Map;
import java.util.WeakHashMap;

public class EventLoop implements HasNative<Pointer> {

    //the following three maps are used to implement proper garbage collection in regards to event source and
    //event callback:

    //This map maps different pointers with same address but separate instance to a single instance.
    private static final Map<Pointer, Pointer>     HANDLER_REF_CACHE         = new WeakHashMap<Pointer, Pointer>();
    //This map maps a single pointer instance to the java object that will be used as handler object.
    private static final Map<Pointer, Object>      HANDLER_REFS              = new WeakHashMap<Pointer, Object>();
    //this map is used to link handler object lifecycle to an event source lifecycle.
    private static final Map<EventSource, Pointer> EVENT_SOURCE_HANDLER_REFS = new WeakHashMap<EventSource, Pointer>();

    private static final wl_event_loop_fd_func_t WL_EVENT_LOOP_FD_FUNC = new wl_event_loop_fd_func_t() {
        @Override
        public int apply(final int fd,
                         final int mask,
                         final Pointer data) {
            final FileDescriptorEventHandler handler = (FileDescriptorEventHandler) HANDLER_REFS.get(data);
            return handler.handle(fd,
                                  mask);
        }
    };

    private static final wl_event_loop_timer_func_t WL_EVENT_LOOP_TIMER_FUNC = new wl_event_loop_timer_func_t() {
        @Override
        public int apply(final Pointer data) {
            final TimerEventHandler handler = (TimerEventHandler) HANDLER_REFS.get(data);
            return handler.handle();
        }
    };

    private static final wl_event_loop_signal_func_t WL_EVENT_LOOP_SIGNAL_FUNC = new wl_event_loop_signal_func_t() {
        @Override
        public int apply(final int signalNumber,
                         final Pointer data) {
            final SignalEventHandler handler = (SignalEventHandler) HANDLER_REFS.get(data);
            return handler.handle(signalNumber);
        }
    };

    private static final wl_event_loop_idle_func_t WL_EVENT_LOOP_IDLE_FUNC = new wl_event_loop_idle_func_t() {
        @Override
        public void apply(final Pointer data) {
            final IdleHandler handler = (IdleHandler) HANDLER_REFS.get(data);
            handler.handle();
        }
    };

    public static final int EVENT_READABLE = 0x01;
    public static final int EVENT_WRITABLE = 0x02;
    public static final int EVENT_HANGUP   = 0x04;
    public static final int EVENT_ERROR    = 0x08;

    private final Pointer pointer;

    private boolean valid;

    protected EventLoop(final Pointer pointer) {
        this.pointer = pointer;
        this.valid = true;
        addDestroyListener(new Listener() {
            @Override
            public void handle() {
                remove();
                EventLoop.this.valid = false;
                //We leak memory here as libwayland does not provide us with a real destructor hook.
                //ObjectCache.remove(Client.this.getNative());
            }
        });
        ObjectCache.store(getNative(),
                          this);
    }

    public static EventLoop create() {
        return EventLoop.get(WaylandServerLibrary.INSTANCE()
                                                 .wl_event_loop_create());
    }

    public static EventLoop get(final Pointer pointer) {
        if (pointer == null) {
            return null;
        }
        EventLoop eventLoop = ObjectCache.from(pointer);
        if (eventLoop == null) {
            eventLoop = new EventLoop(pointer);
        }
        return eventLoop;
    }

    private Pointer getHandlerRef(Object handler) {
        final Pointer handlerRefKey = Pointer.createConstant(handler.hashCode());

        Pointer handlerRef = HANDLER_REF_CACHE.get(handlerRefKey);
        if (handlerRef == null) {
            handlerRef = handlerRefKey;
            HANDLER_REF_CACHE.put(handlerRefKey,
                                  handlerRef);
        }
        return handlerRef;
    }

    public EventSource addFileDescriptor(final int fd,
                                         final int mask,
                                         final FileDescriptorEventHandler handler) {
        Pointer handlerRef = getHandlerRef(handler);
        if (!HANDLER_REFS.containsKey(handlerRef)) {
            //handler will be garbage collected once event source is collected.
            HANDLER_REFS.put(handlerRef,
                             handler);
        }

        final EventSource eventSource = EventSource.create(WaylandServerLibrary.INSTANCE()
                                                                               .wl_event_loop_add_fd(getNative(),
                                                                                                     fd,
                                                                                                     mask,
                                                                                                     WL_EVENT_LOOP_FD_FUNC,
                                                                                                     handlerRef));
        EVENT_SOURCE_HANDLER_REFS.put(eventSource,
                                      handlerRef);
        return eventSource;
    }

    public EventSource addTimer(final TimerEventHandler handler) {
        Pointer handlerRef = getHandlerRef(handler);
        if (!HANDLER_REFS.containsKey(handlerRef)) {
            //handler will be garbage collected once event source is collected.
            HANDLER_REFS.put(handlerRef,
                             handler);
        }
        final EventSource eventSource = EventSource.create(WaylandServerLibrary.INSTANCE()
                                                                               .wl_event_loop_add_timer(getNative(),
                                                                                                        WL_EVENT_LOOP_TIMER_FUNC,
                                                                                                        handlerRef));
        EVENT_SOURCE_HANDLER_REFS.put(eventSource,
                                      handlerRef);
        return eventSource;
    }

    public EventSource addSignal(final int signalNumber,
                                 final SignalEventHandler handler) {
        Pointer handlerRef = getHandlerRef(handler);
        if (!HANDLER_REFS.containsKey(handlerRef)) {
            //handler will be garbage collected once event source is collected.
            HANDLER_REFS.put(handlerRef,
                             handler);
        }
        final EventSource eventSource = EventSource.create(WaylandServerLibrary.INSTANCE()
                                                                               .wl_event_loop_add_signal(getNative(),
                                                                                                         signalNumber,
                                                                                                         WL_EVENT_LOOP_SIGNAL_FUNC,
                                                                                                         handlerRef));
        EVENT_SOURCE_HANDLER_REFS.put(eventSource,
                                      handlerRef);
        return eventSource;
    }

    public EventSource addIdle(final IdleHandler handler) {
        Pointer handlerRef = getHandlerRef(handler);
        if (!HANDLER_REFS.containsKey(handlerRef)) {
            //handler will be garbage collected once event source is collected.
            HANDLER_REFS.put(handlerRef,
                             handler);
        }
        final EventSource eventSource = EventSource.create(WaylandServerLibrary.INSTANCE()
                                                                               .wl_event_loop_add_idle(getNative(),
                                                                                                       WL_EVENT_LOOP_IDLE_FUNC,
                                                                                                       handlerRef));
        EVENT_SOURCE_HANDLER_REFS.put(eventSource,
                                      handlerRef);
        return eventSource;
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

    @Override
    public boolean isValid() {
        return this.valid;
    }

    public void destroy() {
        if (isValid()) {
            this.valid = false;
            ObjectCache.remove(getNative());
            WaylandServerLibrary.INSTANCE()
                                .free(getNative());
        }
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

    @Override
    protected void finalize() throws Throwable {
        destroy();
        super.finalize();
    }
}

