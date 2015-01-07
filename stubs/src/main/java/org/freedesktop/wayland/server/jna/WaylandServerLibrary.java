package org.freedesktop.wayland.server.jna;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import org.freedesktop.wayland.util.jna.*;

public interface WaylandServerLibrary extends WaylandUtilLibrary {
    public static final String               JNA_LIBRARY_NAME = "wayland-server";
    public static final NativeLibrary        JNA_NATIVE_LIB   = NativeLibrary.getInstance(WaylandServerLibrary.JNA_LIBRARY_NAME);
    public static final WaylandServerLibrary INSTANCE         = (WaylandServerLibrary) Native.loadLibrary(WaylandServerLibrary.JNA_LIBRARY_NAME,
                                                                                                          WaylandServerLibrary.class);

    wl_event_loop wl_event_loop_create();

    void wl_event_loop_destroy(wl_event_loop loop);

    wl_event_source wl_event_loop_add_fd(wl_event_loop loop,
                                         int fd,
                                         int mask,
                                         wl_event_loop_fd_func_t func,
                                         Pointer data);

    int wl_event_source_fd_update(wl_event_source source,
                                  int mask);

    wl_event_source wl_event_loop_add_timer(wl_event_loop loop,
                                            wl_event_loop_timer_func_t func,
                                            Pointer data);

    wl_event_source wl_event_loop_add_signal(wl_event_loop loop,
                                             int signal_number,
                                             wl_event_loop_signal_func_t func,
                                             Pointer data);

    int wl_event_source_timer_update(wl_event_source source,
                                     int ms_delay);

    int wl_event_source_remove(wl_event_source source);

    void wl_event_source_check(wl_event_source source);

    int wl_event_loop_dispatch(wl_event_loop loop,
                               int timeout);

    void wl_event_loop_dispatch_idle(wl_event_loop loop);

    wl_event_source wl_event_loop_add_idle(wl_event_loop loop,
                                           wl_event_loop_idle_func_t func,
                                           Pointer data);

    int wl_event_loop_get_fd(wl_event_loop loop);

    void wl_event_loop_add_destroy_listener(wl_event_loop loop,
                                            wl_listener listener);

    wl_listener wl_event_loop_get_destroy_listener(wl_event_loop loop,
                                                   wl_notify_func_t notify$);

    wl_display wl_display_create();

    void wl_display_destroy(wl_display display);

    wl_event_loop wl_display_get_event_loop(wl_display display);

    int wl_display_add_socket(wl_display display,
                              Pointer name);

    String wl_display_add_socket_auto(wl_display display);

    void wl_display_terminate(wl_display display);

    void wl_display_run(wl_display display);

    void wl_display_flush_clients(wl_display display);

    int wl_display_get_serial(wl_display display);

    int wl_display_next_serial(wl_display display);

    void wl_display_add_destroy_listener(wl_display display,
                                         wl_listener listener);

    wl_listener wl_display_get_destroy_listener(wl_display display,
                                                wl_notify_func_t notify$);

    wl_global wl_global_create(wl_display display,
                               wl_interface interface$,
                               int version,
                               Pointer data,
                               wl_global_bind_func_t bind);

    void wl_global_destroy(wl_global global);

    wl_client wl_client_create(wl_display display,
                               int fd);

    void wl_client_destroy(wl_client client);

    void wl_client_flush(wl_client client);

    void wl_client_get_credentials(wl_client client,
                                   IntByReference pid,
                                   IntByReference uid,
                                   IntByReference gid);

    void wl_client_add_destroy_listener(wl_client client,
                                        wl_listener listener);

    wl_listener wl_client_get_destroy_listener(wl_client client,
                                               wl_notify_func_t notify$);

    wl_resource wl_client_get_object(wl_client client,
                                     int id);

    void wl_client_post_no_memory(wl_client client);

    int wl_client_add_resource(wl_client client,
                               wl_resource resource);

    wl_resource wl_client_add_object(wl_client client,
                                     wl_interface interface$,
                                     Pointer implementation,
                                     int id,
                                     Pointer data);

    wl_resource wl_client_new_object(wl_client client,
                                     wl_interface interface$,
                                     Pointer implementation,
                                     Pointer data);

    wl_global wl_display_add_global(wl_display display,
                                    wl_interface interface$,
                                    Pointer data,
                                    wl_global_bind_func_t bind);

    void wl_display_remove_global(wl_display display,
                                  wl_global global);

    void wl_resource_post_event_array(wl_resource resource,
                                      int opcode,
                                      wl_argument args);

    void wl_resource_queue_event_array(wl_resource resource,
                                       int opcode,
                                       wl_argument args);

    void wl_resource_post_no_memory(wl_resource resource);

    wl_display wl_client_get_display(wl_client client);

    wl_resource wl_resource_create(wl_client client,
                                   wl_interface interface$,
                                   int version,
                                   int id);

    void wl_resource_set_implementation(wl_resource resource,
                                        Pointer implementation,
                                        Pointer data,
                                        wl_resource_destroy_func_t destroy);

    void wl_resource_set_dispatcher(wl_resource resource,
                                    wl_dispatcher_func_t dispatcher,
                                    Pointer implementation,
                                    Pointer data,
                                    wl_resource_destroy_func_t destroy);

    void wl_resource_destroy(wl_resource resource);

    int wl_resource_get_id(wl_resource resource);

    wl_list wl_resource_get_link(wl_resource resource);

    wl_resource wl_resource_from_link(wl_list resource);

    wl_resource wl_resource_find_for_client(wl_list list,
                                            wl_client client);

    wl_client wl_resource_get_client(wl_resource resource);

    void wl_resource_set_user_data(wl_resource resource,
                                   Pointer data);

    Pointer wl_resource_get_user_data(wl_resource resource);

    int wl_resource_get_version(wl_resource resource);

    void wl_resource_set_destructor(wl_resource resource,
                                    wl_resource_destroy_func_t destroy);

    int wl_resource_instance_of(wl_resource resource,
                                wl_interface interface$,
                                Pointer implementation);

    void wl_resource_add_destroy_listener(wl_resource resource,
                                          wl_listener listener);

    wl_listener wl_resource_get_destroy_listener(wl_resource resource,
                                                 wl_notify_func_t notify$);

    void wl_shm_buffer_begin_access(wl_shm_buffer buffer);

    void wl_shm_buffer_end_access(wl_shm_buffer buffer);

    wl_shm_buffer wl_shm_buffer_get(wl_resource resource);

    Pointer wl_shm_buffer_get_data(wl_shm_buffer buffer);

    int wl_shm_buffer_get_stride(wl_shm_buffer buffer);

    int wl_shm_buffer_get_width(wl_shm_buffer buffer);

    int wl_shm_buffer_get_height(wl_shm_buffer buffer);

    int wl_display_init_shm(wl_display display);

    IntByReference wl_display_add_shm_format(wl_display display,
                                             int format);

    wl_shm_buffer wl_shm_buffer_create(wl_client client,
                                       int id,
                                       int width,
                                       int height,
                                       int stride,
                                       int format);

    void wl_log_set_handler_server(wl_log_func_t handler);

    int wl_shm_buffer_get_format(wl_shm_buffer buffer);

    void wl_resource_post_error(wl_resource pointer,
                                int code,
                                String msg);
}
