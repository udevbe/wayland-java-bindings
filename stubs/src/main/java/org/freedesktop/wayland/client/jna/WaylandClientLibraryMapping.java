package org.freedesktop.wayland.client.jna;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import org.freedesktop.wayland.util.jna.WaylandUtilLibraryMapping;
import org.freedesktop.wayland.util.jna.wl_dispatcher_func_t;
import org.freedesktop.wayland.util.jna.wl_log_func_t;


public interface WaylandClientLibraryMapping extends WaylandUtilLibraryMapping {

    public static final String JNA_LIBRARY_NAME = "wayland-client";

    void wl_event_queue_destroy(Pointer queue);

    void wl_proxy_marshal_array(Pointer p,
                                int opcode,
                                Pointer args);

    Pointer wl_proxy_create(Pointer factory,
                            Pointer interface$);

    Pointer wl_proxy_marshal_array_constructor(Pointer proxy,
                                               int opcode,
                                               Pointer args,
                                               Pointer interface$);

    void wl_proxy_destroy(Pointer proxy);

    int wl_proxy_add_listener(Pointer proxy,
                              Pointer implementation,
                              Pointer data);

    Pointer wl_proxy_get_listener(Pointer proxy);

    int wl_proxy_add_dispatcher(Pointer proxy,
                                wl_dispatcher_func_t dispatcher_func,
                                Pointer dispatcher_data,
                                Pointer data);

    void wl_proxy_set_user_data(Pointer proxy,
                                Pointer user_data);

    Pointer wl_proxy_get_user_data(Pointer proxy);

    int wl_proxy_get_id(Pointer proxy);

    String wl_proxy_get_class(Pointer proxy);

    void wl_proxy_set_queue(Pointer proxy,
                            Pointer queue);

    Pointer wl_display_connect(Pointer name);

    Pointer wl_display_connect_to_fd(int fd);

    void wl_display_disconnect(Pointer display);

    int wl_display_get_fd(Pointer display);

    int wl_display_dispatch(Pointer display);

    int wl_display_dispatch_queue(Pointer display,
                                  Pointer queue);

    int wl_display_dispatch_queue_pending(Pointer display,
                                          Pointer queue);

    int wl_display_dispatch_pending(Pointer display);

    int wl_display_get_error(Pointer display);

    int wl_display_get_protocol_error(Pointer display,
                                      Pointer interface$,
                                      IntByReference id);

    int wl_display_flush(Pointer display);

    int wl_display_roundtrip_queue(Pointer display,
                                   Pointer queue);

    int wl_display_roundtrip(Pointer display);

    Pointer wl_display_create_queue(Pointer display);

    int wl_display_prepare_read_queue(Pointer display,
                                      Pointer queue);

    int wl_display_prepare_read(Pointer display);

    void wl_display_cancel_read(Pointer display);

    int wl_display_read_events(Pointer display);

    void wl_log_set_handler_client(wl_log_func_t handler);
}