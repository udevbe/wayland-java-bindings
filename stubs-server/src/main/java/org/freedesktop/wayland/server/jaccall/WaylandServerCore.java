package org.freedesktop.wayland.server.jaccall;

import org.freedesktop.jaccall.Lib;
import org.freedesktop.jaccall.Pointer;
import org.freedesktop.jaccall.Ptr;
import org.freedesktop.jaccall.Unsigned;
import org.freedesktop.wayland.util.jaccall.WaylandUtil;
import org.freedesktop.wayland.util.jaccall.wl_argument;
import org.freedesktop.wayland.util.jaccall.wl_array;
import org.freedesktop.wayland.util.jaccall.wl_dispatcher_func_t;
import org.freedesktop.wayland.util.jaccall.wl_interface;
import org.freedesktop.wayland.util.jaccall.wl_list;
import org.freedesktop.wayland.util.jaccall.wl_log_func_t;

import static org.freedesktop.jaccall.Pointer.ref;

@Lib(value = "wayland-server",
     version = 0)
public class WaylandServerCore implements WaylandUtil {

    public static final int WL_EVENT_READABLE = 0x01;
    public static final int WL_EVENT_WRITABLE = 0x02;
    public static final int WL_EVENT_HANGUP   = 0x04;
    public static final int WL_EVENT_ERROR    = 0x08;
    private static WaylandServerCore INSTANCE;

    private WaylandServerCore() {}

    public static WaylandServerCore INSTANCE() {
        if (INSTANCE == null) {
            new WaylandServerCore_Symbols().link();
            INSTANCE = new WaylandServerCore();
        }
        return INSTANCE;
    }

    public native int wl_event_source_timer_update(@Ptr long source,
                                                   int ms_delay);

    public native int wl_event_source_remove(@Ptr long source);

    public native void wl_event_source_check(@Ptr long source);


    public native int wl_event_loop_dispatch(@Ptr long loop,
                                             int timeout);

    public native void wl_event_loop_dispatch_idle(@Ptr long loop);

    @Ptr
    public native long wl_event_loop_add_idle(@Ptr long loop,
                                              @Ptr(wl_event_loop_idle_func_t.class) long func,
                                              @Ptr(void.class) long data);

    public native int wl_event_loop_get_fd(@Ptr long loop);

    public native void wl_event_loop_add_destroy_listener(@Ptr long loop,
                                                          @Ptr(wl_listener.class) long listener);

    @Ptr(wl_listener.class)
    public native long wl_event_loop_get_destroy_listener(@Ptr long loop,
                                                          @Ptr(wl_notify_func_t.class) long notify);

    @Ptr
    public native long wl_display_create();

    public native void wl_display_destroy(@Ptr long display);

    @Ptr
    public native long wl_display_get_event_loop(@Ptr long display);

    public native int wl_display_add_socket(@Ptr long display,
                                            @Ptr(String.class) long name);

    @Ptr(String.class)
    public native long wl_display_add_socket_auto(@Ptr long display);

    public native void wl_display_terminate(@Ptr long display);

    public native void wl_display_run(@Ptr long display);

    public native void wl_display_flush_clients(@Ptr long display);

    @Unsigned
    public native int wl_display_get_serial(@Ptr long display);

    @Unsigned
    public native int wl_display_next_serial(@Ptr long display);

    public native void wl_display_add_destroy_listener(@Ptr long display,
                                                       @Ptr(wl_listener.class) long listener);

    @Ptr(wl_listener.class)
    public native long wl_display_get_destroy_listener(@Ptr long display,
                                                       @Ptr(wl_notify_func_t.class) long notify);

    @Ptr
    public native long wl_global_create(@Ptr long display,
                                        @Ptr(wl_interface.class) long interface_,
                                        int version,
                                        @Ptr(Object.class) long data,
                                        @Ptr(wl_global_bind_func_t.class) long bind);

    public native void wl_global_destroy(@Ptr long global);

    @Ptr
    public native long wl_client_create(@Ptr long display,
                                        int fd);

    public native void wl_client_destroy(@Ptr long client);

    public native void wl_client_flush(@Ptr long client);

    public native void wl_client_get_credentials(@Ptr long client,
                                                 @Ptr(int.class) long pid,
                                                 @Ptr(int.class) long uid,
                                                 @Ptr(int.class) long gid);

    public native void wl_client_add_destroy_listener(@Ptr long client,
                                                      @Ptr(wl_listener.class) long listener);

    @Ptr(wl_listener.class)
    public native long wl_client_get_destroy_listener(@Ptr long client,
                                                      @Ptr(wl_notify_func_t.class) long notify);

    @Ptr
    public native long wl_client_get_object(@Ptr long client,
                                            @Unsigned int id);

    public native void wl_client_post_no_memory(@Ptr long client);

    public void wl_signal_init(final Pointer<wl_signal> signal) {
        wl_list_init(ref(signal.get()
                               .listener_list()).address);
    }

    //wayland-util.h
    @Override
    public native void wl_list_init(@Ptr(wl_list.class) final long list);

//public Pointer<wl_listener>
//wl_signal_get(Pointer<wl_signal> signal, wl_notify_func_t notify)
//{
//	@Ptr(wl_listener.class) long l;
//
//	wl_list_for_each(l, &signal->listener_list, link)
//		if (l->notify == notify)
//			return l;
//
//	return NULL;
//}

///** Emits this signal, notifying all registered listeners.
// *
// * \param signal The signal object that will emit the signal
// * \param data The data that will be emitted with the signal
// *
// * \memberof wl_signal
// */
//static inline void
//wl_signal_emit(struct wl_signal *signal, @Ptr(void.class) long data)
//{
//	@Ptr(wl_listener.class) long l, *next;
//
//	wl_list_for_each_safe(l, next, &signal->listener_list, link)
//		l->notify(l, data);
//}

    public void wl_signal_add(final Pointer<wl_signal> signal,
                              final Pointer<wl_listener> listener) {
        wl_list_insert(signal.get()
                             .listener_list()
                             .prev().address,
                       ref(listener.get()
                                   .link()).address);
    }

    @Override
    public native void wl_list_insert(@Ptr(wl_list.class) final long list,
                                      @Ptr(wl_list.class) final long elm);

    /**
     * Post an event to the client's object referred to by 'resource'.
     * 'opcode' is the event number generated from the protocol XML
     * description (the event name). The variable arguments are the event
     * parameters, in the order they appear in the protocol XML specification.
     * <p/>
     * The variable arguments' types are:
     * - type=uint: 	@Unsigned int
     * - type=int:		int
     * - type=fixed:	wl_fixed_t
     * - type=string:	( char *) to a nil-terminated string
     * - type=array:	(struct wl_array *)
     * - type=fd:		int, that is an open file descriptor
     * - type=new_id:	(struct wl_object *) or (@Ptr long )
     * - type=object:	(struct wl_object *) or (@Ptr long )
     */
    public native void wl_resource_post_event_array(@Ptr long resource,
                                                    @Unsigned int opcode,
                                                    @Ptr(wl_argument.class) long args);

    public native void wl_resource_queue_event_array(@Ptr long resource,
                                                     @Unsigned int opcode,
                                                     @Ptr(wl_argument.class) long args);

    /**
     * msg is a printf format string, variable args are its args.
     */
    public native void wl_resource_post_error(@Ptr long resource,
                                              @Unsigned int code,
                                              @Ptr(String.class) long msg);

    public native void wl_resource_post_no_memory(@Ptr long resource);

    @Ptr
    public native long wl_client_get_display(@Ptr long client);

    @Ptr
    public native long wl_resource_create(@Ptr long client,
                                          @Ptr(wl_interface.class) long interface_,
                                          int version,
                                          @Unsigned int id);

    public native void wl_resource_set_implementation(@Ptr long resource,
                                                      @Ptr(void.class) long implementation,
                                                      @Ptr(void.class) long data,
                                                      @Ptr(wl_resource_destroy_func_t.class) long destroy);

    public native void wl_resource_set_dispatcher(@Ptr long resource,
                                                  @Ptr(wl_dispatcher_func_t.class) long dispatcher,
                                                  @Ptr(Object.class) long implementation,
                                                  @Ptr(Object.class) long data,
                                                  @Ptr(wl_resource_destroy_func_t.class) long destroy);

    public native void wl_resource_destroy(@Ptr long resource);

    @Unsigned
    public native int wl_resource_get_id(@Ptr long resource);

    @Ptr(wl_list.class)
    public native long wl_resource_get_link(@Ptr long resource);

    @Ptr
    public native long wl_resource_from_link(@Ptr(wl_list.class) long resource);

    @Ptr
    public native long wl_resource_find_for_client(@Ptr(wl_list.class) long list,
                                                   @Ptr long client);

    @Ptr
    public native long wl_resource_get_client(@Ptr long resource);

    public native void wl_resource_set_user_data(@Ptr long resource,
                                                 @Ptr(void.class) long data);

    @Ptr(void.class)
    public native long wl_resource_get_user_data(@Ptr long resource);

    public native int wl_resource_get_version(@Ptr long resource);

    public native void wl_resource_set_destructor(@Ptr long resource,
                                                  @Ptr(wl_resource_destroy_func_t.class) long destroy);

    public native int wl_resource_instance_of(@Ptr long resource,
                                              @Ptr(wl_interface.class) long interface_,
                                              @Ptr(void.class) long implementation);

    public native void wl_resource_add_destroy_listener(@Ptr long resource,
                                                        @Ptr(wl_listener.class) long listener);

    @Ptr(wl_listener.class)
    public native long wl_resource_get_destroy_listener(@Ptr long resource,
                                                        @Ptr(wl_notify_func_t.class) long notify);

    public native void wl_shm_buffer_begin_access(@Ptr long buffer);

    public native void wl_shm_buffer_end_access(@Ptr long buffer);

    @Ptr
    public native long wl_shm_buffer_get(@Ptr long resource);

    @Ptr(void.class)
    public native long wl_shm_buffer_get_data(@Ptr long buffer);

    public native int wl_shm_buffer_get_stride(@Ptr long buffer);

    @Unsigned
    public native int wl_shm_buffer_get_format(@Ptr long buffer);

    public native int wl_shm_buffer_get_width(@Ptr long buffer);

    public native int wl_shm_buffer_get_height(@Ptr long buffer);

    public native int wl_display_init_shm(@Ptr long display);

    @Ptr(int.class)
    public native long wl_display_add_shm_format(@Ptr long display,
                                                 @Unsigned int format);

    @Ptr
    public native long wl_shm_buffer_create(@Ptr long client,
                                            @Unsigned int id,
                                            int width,
                                            int height,
                                            int stride,
                                            @Unsigned int format);

    public native void wl_log_set_handler_server(@Ptr(wl_log_func_t.class) long handler);

    @Ptr
    public native long wl_event_loop_add_signal(@Ptr long loop,
                                                int signal_number,
                                                @Ptr(wl_event_loop_signal_func_t.class) long func,
                                                @Ptr(Object.class) long data);

    @Ptr
    public native long wl_event_loop_add_timer(@Ptr long loop,
                                               @Ptr(wl_event_loop_timer_func_t.class) long func,
                                               @Ptr(Object.class) long data);

    public native int wl_event_source_fd_update(@Ptr long source,
                                                @Unsigned int mask);

    @Ptr
    public native long wl_event_loop_add_fd(@Ptr long loop,
                                            int fd,
                                            @Unsigned int mask,
                                            @Ptr(wl_event_loop_fd_func_t.class) long func,
                                            @Ptr(Object.class) long data);

    public native void wl_event_loop_destroy(@Ptr long loop);

    @Ptr
    public native long wl_event_loop_create();

    @Override
    public native void wl_list_remove(@Ptr(wl_list.class) final long elm);

    @Override
    public native int wl_list_length(@Ptr(wl_list.class) final long list);

    @Override
    public native int wl_list_empty(@Ptr(wl_list.class) final long list);

    @Override
    public native void wl_list_insert_list(@Ptr(wl_list.class) final long list,
                                           @Ptr(wl_list.class) final long other);

    @Override
    public native void wl_array_init(@Ptr(wl_array.class) final long array);

    @Override
    public native void wl_array_release(@Ptr(wl_array.class) final long array);

    @Override
    public native void wl_array_add(@Ptr(wl_array.class) final long array,
                                    final long size);

    @Override
    public native int wl_array_copy(@Ptr(wl_array.class) final long array,
                                    @Ptr(wl_array.class) final long source);

    @Override
    public native void free(@Ptr final long pointer);
}
