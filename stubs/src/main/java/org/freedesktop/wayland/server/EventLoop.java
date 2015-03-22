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
import org.freedesktop.wayland.server.jna.*;
import org.freedesktop.wayland.util.ObjectCache;

import java.util.HashMap;
import java.util.Map;

public class EventLoop implements HasNative<Pointer> {

    private static final Map<Pointer, Object> HANDLER_REFS = new HashMap<Pointer, Object>();

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

    public static final int EVENT_READABLE = 0x01;
    public static final int EVENT_WRITABLE = 0x02;
    public static final int EVENT_HANGUP   = 0x04;
    public static final int EVENT_ERROR    = 0x08;


    private final Pointer pointer;

    private boolean valid;

    protected EventLoop(final Pointer pointer) {
        this.pointer = pointer;
        this.valid = true;
        ObjectCache.store(getNative(),
                          this);
    }

    public static EventLoop create() {
        return EventLoop.get(WaylandServerLibrary.INSTANCE()
                                                 .wl_event_loop_create());
    }

    public static EventLoop get(final Pointer pointer) {
        EventLoop eventLoop = ObjectCache.from(pointer);
        if (eventLoop == null) {
            eventLoop = new EventLoop(pointer);
        }
        return eventLoop;
    }

    public EventSource addFileDescriptor(final int fd,
                                         final int mask,
                                         final FileDescriptorEventHandler handler) {
//        static struct wl_event_source *
//        add_source(struct wl_event_loop *loop,
//                   struct wl_event_source *source, uint32_t mask, void *data)
//        {
//            struct epoll_event ep;
//
//            if (source->fd < 0) {
//                free(source);
//                return NULL;
//            }
//
//            source->loop = loop;
//            source->data = data;
//            wl_list_init(&source->link);
//
//            memset(&ep, 0, sizeof ep);
//            if (mask & WL_EVENT_READABLE)
//                ep.events |= EPOLLIN;
//            if (mask & WL_EVENT_WRITABLE)
//                ep.events |= EPOLLOUT;
//            ep.data.ptr = source;
//
//            if (epoll_ctl(loop->epoll_fd, EPOLL_CTL_ADD, source->fd, &ep) < 0) {
//            close(source->fd);
//            free(source);
//            return NULL;
//        }
//
//            return source;
//        }
//
//        WL_EXPORT struct wl_event_source *
//                         wl_event_loop_add_fd(struct wl_event_loop *loop,
//        int fd, uint32_t mask,
//                wl_event_loop_fd_func_t func,
//        void *data)
//        {
//            struct wl_event_source_fd *source;
//
//            source = malloc(sizeof *source);
//            if (source == NULL)
//                return NULL;
//
//            source->base.interface = &fd_source_interface;
//            source->base.fd = fcntl(fd, F_DUPFD_CLOEXEC, 0);
//            source->func = func;
//            source->fd = fd;
//
//            return add_source(loop, &source->base, mask, data);
//        }
        final Pointer handlerRef = Pointer.createConstant(handler.hashCode());
        //FIXME memleak: handler will never be garbage collected
        HANDLER_REFS.put(handlerRef,
                         handler);
        final Pointer wlEventSource = WaylandServerLibrary.INSTANCE()
                                                          .wl_event_loop_add_fd(getNative(),
                                                                                fd,
                                                                                mask,
                                                                                WL_EVENT_LOOP_FD_FUNC,
                                                                                handlerRef);

        return EventSource.create(wlEventSource);
    }

    public EventSource addTimer(final TimerEventHandler handler) {
//        struct wl_event_source_timer *source;
//
//        source = malloc(sizeof *source);
//        if (source == NULL)
//            return NULL;
//
//        source->base.interface = &timer_source_interface;
//        source->base.fd = timerfd_create(CLOCK_MONOTONIC,
//                                         TFD_CLOEXEC | TFD_NONBLOCK);
//        source->func = func;
//
//        return add_source(loop, &source->base, WL_EVENT_READABLE, data);
        //FIXME use better callback mechanism to avoid memory leaks

        final wl_event_loop_timer_func_t nativeCallback = new wl_event_loop_timer_func_t() {
            @Override
            public int apply(final Pointer data) {
                return handler.handle();
            }
        };
        //this call is equivalent to
        final EventSource eventSource = EventSource.create(WaylandServerLibrary.INSTANCE()
                                                                               .wl_event_loop_add_timer(getNative(),
                                                                                                        nativeCallback,
                                                                                                        Pointer.NULL));
        this.HANDLER_REFS.put(eventSource,
                                     nativeCallback);
        return eventSource;
    }

    public EventSource addSignal(final int signalNumber,
                                 final SignalEventHandler handler) {
        //        struct wl_event_source_signal *source;
//        sigset_t mask;
//
//        source = malloc(sizeof *source);
//        if (source == NULL)
//            return NULL;
//
//        source->base.interface = &signal_source_interface;
//        source->signal_number = signal_number;
//
//        sigemptyset(&mask);
//        sigaddset(&mask, signal_number);
//        source->base.fd = signalfd(-1, &mask, SFD_CLOEXEC);
//        sigprocmask(SIG_BLOCK, &mask, NULL);
//
//        source->func = func;
//
//        return add_source(loop, &source->base, WL_EVENT_READABLE, data);
        final wl_event_loop_signal_func_t nativeCallback = new wl_event_loop_signal_func_t() {
            @Override
            public int apply(final int signal_number,
                             final Pointer data) {
                return handler.handle(signalNumber);
            }
        };
        final Pointer wlEventSource = WaylandServerLibrary.INSTANCE()
                                                          .wl_event_loop_add_signal(getNative(),
                                                                                    signalNumber,
                                                                                    nativeCallback,
                                                                                    Pointer.NULL);
        final EventSource eventSource = EventSource.create(wlEventSource);
        this.HANDLER_REFS.put(eventSource,
                                     nativeCallback);
        return eventSource;
    }

    public EventSource addIdle(final IdleHandler handler) {
//        struct wl_event_source_idle *source;
//
//        source = malloc(sizeof *source);
//        if (source == NULL)
//            return NULL;
//
//        source->base.interface = &idle_source_interface;
//        source->base.loop = loop;
//        source->base.fd = -1;
//
//        source->func = func;
//        source->base.data = data;
//
//        wl_list_insert(loop->idle_list.prev, &source->base.link);
//
//        return &source->base;
        final wl_event_loop_idle_func_t nativeCallback = new wl_event_loop_idle_func_t() {
            @Override
            public void apply(final Pointer data) {
                handler.handle();
            }
        };
        final Pointer wlEventSource = WaylandServerLibrary.INSTANCE()
                                                          .wl_event_loop_add_idle(getNative(),
                                                                                  nativeCallback,
                                                                                  Pointer.NULL);
        final EventSource eventSource = EventSource.create(wlEventSource);
        this.HANDLER_REFS.put(eventSource,
                                     nativeCallback);
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

