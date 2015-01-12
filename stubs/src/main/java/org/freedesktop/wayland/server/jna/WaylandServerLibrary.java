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

    long wl_event_loop_create();

    void wl_event_loop_destroy(long loop);

    long wl_event_loop_add_fd(long loop,
                              int fd,
                              int mask,
                              wl_event_loop_fd_func_t func,
                              long data);

    int wl_event_source_fd_update(long source,
                                  int mask);

    long wl_event_loop_add_timer(long loop,
                                 wl_event_loop_timer_func_t func,
                                 long data);

    long wl_event_loop_add_signal(long loop,
                                  int signal_number,
                                  wl_event_loop_signal_func_t func,
                                  long data);

    int wl_event_source_timer_update(long source,
                                     int ms_delay);

    int wl_event_source_remove(long source);

    void wl_event_source_check(long source);

    int wl_event_loop_dispatch(long loop,
                               int timeout);

    void wl_event_loop_dispatch_idle(long loop);

    long wl_event_loop_add_idle(long loop,
                                wl_event_loop_idle_func_t func,
                                long data);

    int wl_event_loop_get_fd(long loop);

    void wl_event_loop_add_destroy_listener(long loop,
                                            wl_listener listener);

    wl_listener wl_event_loop_get_destroy_listener(long loop,
                                                   wl_notify_func_t notify$);

    long wl_display_create();

    void wl_display_destroy(long display);

    long wl_display_get_event_loop(long display);

    int wl_display_add_socket(long display,
                              Pointer name);

    String wl_display_add_socket_auto(long display);

    void wl_display_terminate(long display);

    void wl_display_run(long display);

    void wl_display_flush_clients(long display);

    int wl_display_get_serial(long display);

    int wl_display_next_serial(long display);

    void wl_display_add_destroy_listener(long display,
                                         wl_listener listener);

    wl_listener wl_display_get_destroy_listener(long display,
                                                wl_notify_func_t notify$);

    long wl_global_create(long display,
                          wl_interface interface$,
                          int version,
                          long data,
                          wl_global_bind_func_t bind);

    void wl_global_destroy(long global);

    long wl_client_create(long display,
                          int fd);

    void wl_client_destroy(long client);

    void wl_client_flush(long client);

    void wl_client_get_credentials(long client,
                                   IntByReference pid,
                                   IntByReference uid,
                                   IntByReference gid);

    void wl_client_add_destroy_listener(long client,
                                        wl_listener listener);

    wl_listener wl_client_get_destroy_listener(long client,
                                               wl_notify_func_t notify$);

    long wl_client_get_object(long client,
                              int id);

    void wl_client_post_no_memory(long client);

    int wl_client_add_resource(long client,
                               long resource);

    long wl_client_add_object(long client,
                              wl_interface interface$,
                              long implementation,
                              int id,
                              long data);

    long wl_client_new_object(long client,
                              wl_interface interface$,
                              long implementation,
                              long data);

    long wl_display_add_global(long display,
                               wl_interface interface$,
                               long data,
                               wl_global_bind_func_t bind);

    void wl_display_remove_global(long display,
                                  long global);

    void wl_resource_post_event_array(long resource,
                                      int opcode,
                                      long args);

    void wl_resource_queue_event_array(long resource,
                                       int opcode,
                                       long args);

    void wl_resource_post_no_memory(long resource);

    long wl_client_get_display(long client);

    long wl_resource_create(long client,
                            wl_interface interface$,
                            int version,
                            int id);

    void wl_resource_set_implementation(long resource,
                                        long implementation,
                                        long data,
                                        wl_resource_destroy_func_t destroy);

    void wl_resource_set_dispatcher(long resource,
                                    wl_dispatcher_func_t dispatcher,
                                    long implementation,
                                    long data,
                                    wl_resource_destroy_func_t destroy);

    void wl_resource_destroy(long resource);

    int wl_resource_get_id(long resource);

    wl_list wl_resource_get_link(long resource);

    long wl_resource_from_link(wl_list resource);

    long wl_resource_find_for_client(wl_list list,
                                     long client);

    long wl_resource_get_client(long resource);

    void wl_resource_set_user_data(long resource,
                                   Pointer data);

    Pointer wl_resource_get_user_data(long resource);

    int wl_resource_get_version(long resource);

    void wl_resource_set_destructor(long resource,
                                    wl_resource_destroy_func_t destroy);

    int wl_resource_instance_of(long resource,
                                wl_interface interface$,
                                long implementation);

    void wl_resource_add_destroy_listener(long resource,
                                          wl_listener listener);

    wl_listener wl_resource_get_destroy_listener(long resource,
                                                 wl_notify_func_t notify$);

    void wl_shm_buffer_begin_access(long buffer);

    void wl_shm_buffer_end_access(long buffer);

    long wl_shm_buffer_get(long resource);

    Pointer wl_shm_buffer_get_data(long buffer);

    int wl_shm_buffer_get_stride(long buffer);

    int wl_shm_buffer_get_width(long buffer);

    int wl_shm_buffer_get_height(long buffer);

    int wl_display_init_shm(long display);

    IntByReference wl_display_add_shm_format(long display,
                                             int format);

    long wl_shm_buffer_create(long client,
                              int id,
                              int width,
                              int height,
                              int stride,
                              int format);

    void wl_log_set_handler_server(wl_log_func_t handler);

    int wl_shm_buffer_get_format(long buffer);

    void wl_resource_post_error(long pointer,
                                int code,
                                String msg);
}
