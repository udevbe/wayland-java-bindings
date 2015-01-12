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

    Pointer wl_event_loop_create();

    void wl_event_loop_destroy(Pointer loop);

    Pointer wl_event_loop_add_fd(Pointer loop,
                                 int fd,
                                 int mask,
                                 wl_event_loop_fd_func_t func,
                                 Pointer data);

    int wl_event_source_fd_update(Pointer source,
                                  int mask);

    Pointer wl_event_loop_add_timer(Pointer loop,
                                    wl_event_loop_timer_func_t func,
                                    Pointer data);

    Pointer wl_event_loop_add_signal(Pointer loop,
                                     int signal_number,
                                     wl_event_loop_signal_func_t func,
                                     Pointer data);

    int wl_event_source_timer_update(Pointer source,
                                     int ms_delay);

    int wl_event_source_remove(Pointer source);

    void wl_event_source_check(Pointer source);

    int wl_event_loop_dispatch(Pointer loop,
                               int timeout);

    void wl_event_loop_dispatch_idle(Pointer loop);

    Pointer wl_event_loop_add_idle(Pointer loop,
                                   wl_event_loop_idle_func_t func,
                                   Pointer data);

    int wl_event_loop_get_fd(Pointer loop);

    void wl_event_loop_add_destroy_listener(Pointer loop,
                                            wl_listener listener);

    wl_listener wl_event_loop_get_destroy_listener(Pointer loop,
                                                   wl_notify_func_t notify$);

    Pointer wl_display_create();

    void wl_display_destroy(Pointer display);

    Pointer wl_display_get_event_loop(Pointer display);

    int wl_display_add_socket(Pointer display,
                              Pointer name);

    String wl_display_add_socket_auto(Pointer display);

    void wl_display_terminate(Pointer display);

    void wl_display_run(Pointer display);

    void wl_display_flush_clients(Pointer display);

    int wl_display_get_serial(Pointer display);

    int wl_display_next_serial(Pointer display);

    void wl_display_add_destroy_listener(Pointer display,
                                         wl_listener listener);

    wl_listener wl_display_get_destroy_listener(Pointer display,
                                                wl_notify_func_t notify$);

    Pointer wl_global_create(Pointer display,
                             wl_interface interface$,
                             int version,
                             Pointer data,
                             wl_global_bind_func_t bind);

    void wl_global_destroy(Pointer global);

    Pointer wl_client_create(Pointer display,
                             int fd);

    void wl_client_destroy(Pointer client);

    void wl_client_flush(Pointer client);

    void wl_client_get_credentials(Pointer client,
                                   IntByReference pid,
                                   IntByReference uid,
                                   IntByReference gid);

    void wl_client_add_destroy_listener(Pointer client,
                                        wl_listener listener);

    wl_listener wl_client_get_destroy_listener(Pointer client,
                                               wl_notify_func_t notify$);

    Pointer wl_client_get_object(Pointer client,
                                 int id);

    void wl_client_post_no_memory(Pointer client);

    int wl_client_add_resource(Pointer client,
                               Pointer resource);

    Pointer wl_client_add_object(Pointer client,
                                 wl_interface interface$,
                                 Pointer implementation,
                                 int id,
                                 Pointer data);

    Pointer wl_client_new_object(Pointer client,
                                 wl_interface interface$,
                                 Pointer implementation,
                                 Pointer data);

    Pointer wl_display_add_global(Pointer display,
                                  wl_interface interface$,
                                  Pointer data,
                                  wl_global_bind_func_t bind);

    void wl_display_remove_global(Pointer display,
                                  Pointer global);

    void wl_resource_post_event_array(Pointer resource,
                                      int opcode,
                                      Pointer args);

    void wl_resource_queue_event_array(Pointer resource,
                                       int opcode,
                                       Pointer args);

    void wl_resource_post_no_memory(Pointer resource);

    Pointer wl_client_get_display(Pointer client);

    Pointer wl_resource_create(Pointer client,
                               wl_interface interface$,
                               int version,
                               int id);

    void wl_resource_set_implementation(Pointer resource,
                                        Pointer implementation,
                                        Pointer data,
                                        wl_resource_destroy_func_t destroy);

    void wl_resource_set_dispatcher(Pointer resource,
                                    wl_dispatcher_func_t dispatcher,
                                    Pointer implementation,
                                    Pointer data,
                                    wl_resource_destroy_func_t destroy);

    void wl_resource_destroy(Pointer resource);

    int wl_resource_get_id(Pointer resource);

    wl_list wl_resource_get_link(Pointer resource);

    Pointer wl_resource_from_link(wl_list resource);

    Pointer wl_resource_find_for_client(wl_list list,
                                        Pointer client);

    Pointer wl_resource_get_client(Pointer resource);

    void wl_resource_set_user_data(Pointer resource,
                                   Pointer data);

    Pointer wl_resource_get_user_data(Pointer resource);

    int wl_resource_get_version(Pointer resource);

    void wl_resource_set_destructor(Pointer resource,
                                    wl_resource_destroy_func_t destroy);

    int wl_resource_instance_of(Pointer resource,
                                wl_interface interface$,
                                Pointer implementation);

    void wl_resource_add_destroy_listener(Pointer resource,
                                          wl_listener listener);

    wl_listener wl_resource_get_destroy_listener(Pointer resource,
                                                 wl_notify_func_t notify$);

    void wl_shm_buffer_begin_access(Pointer buffer);

    void wl_shm_buffer_end_access(Pointer buffer);

    Pointer wl_shm_buffer_get(Pointer resource);

    Pointer wl_shm_buffer_get_data(Pointer buffer);

    int wl_shm_buffer_get_stride(Pointer buffer);

    int wl_shm_buffer_get_width(Pointer buffer);

    int wl_shm_buffer_get_height(Pointer buffer);

    int wl_display_init_shm(Pointer display);

    IntByReference wl_display_add_shm_format(Pointer display,
                                             int format);

    Pointer wl_shm_buffer_create(Pointer client,
                                 int id,
                                 int width,
                                 int height,
                                 int stride,
                                 int format);

    void wl_log_set_handler_server(wl_log_func_t handler);

    int wl_shm_buffer_get_format(Pointer buffer);

    void wl_resource_post_error(Pointer pointer,
                                int code,
                                String msg);
}
