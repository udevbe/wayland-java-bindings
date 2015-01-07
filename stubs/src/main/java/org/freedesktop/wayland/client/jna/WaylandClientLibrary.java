package org.freedesktop.wayland.client.jna;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.freedesktop.wayland.util.jna.*;


public interface WaylandClientLibrary extends WaylandUtilLibrary {

    public static final String               JNA_LIBRARY_NAME = "wayland-client";
    public static final NativeLibrary        JNA_NATIVE_LIB   = NativeLibrary.getInstance(WaylandClientLibrary.JNA_LIBRARY_NAME);
    public static final WaylandClientLibrary INSTANCE         = (WaylandClientLibrary) Native.loadLibrary(WaylandClientLibrary.JNA_LIBRARY_NAME,
                                                                                                          WaylandClientLibrary.class);

    void wl_event_queue_destroy(wl_event_queue queue);

    void wl_proxy_marshal_array(wl_proxy p,
                                int opcode,
                                wl_argument args);

    wl_proxy wl_proxy_create(wl_proxy factory,
                             wl_interface interface$);

    wl_proxy wl_proxy_marshal_constructor(wl_proxy proxy,
                                          int opcode,
                                          wl_interface interface$,
                                          Object... varArgs1);

    wl_proxy wl_proxy_marshal_array_constructor(wl_proxy proxy,
                                                int opcode,
                                                wl_argument args,
                                                wl_interface interface$);

    void wl_proxy_destroy(wl_proxy proxy);

    int wl_proxy_add_listener(wl_proxy proxy,
                              PointerByReference implementation,
                              Pointer data);

    Pointer wl_proxy_get_listener(wl_proxy proxy);

    int wl_proxy_add_dispatcher(wl_proxy proxy,
                                wl_dispatcher_func_t dispatcher_func,
                                Pointer dispatcher_data,
                                Pointer data);

    void wl_proxy_set_user_data(wl_proxy proxy,
                                Pointer user_data);

    Pointer wl_proxy_get_user_data(wl_proxy proxy);

    int wl_proxy_get_id(wl_proxy proxy);

    String wl_proxy_get_class(wl_proxy proxy);

    void wl_proxy_set_queue(wl_proxy proxy,
                            wl_event_queue queue);

    wl_display wl_display_connect(String name);

    wl_display wl_display_connect_to_fd(int fd);

    void wl_display_disconnect(wl_display display);

    int wl_display_get_fd(wl_display display);

    int wl_display_dispatch(wl_display display);

    int wl_display_dispatch_queue(wl_display display,
                                  wl_event_queue queue);

    int wl_display_dispatch_queue_pending(wl_display display,
                                          wl_event_queue queue);

    int wl_display_dispatch_pending(wl_display display);

    int wl_display_get_error(wl_display display);

    int wl_display_get_protocol_error(wl_display display,
                                      wl_interface.ByReference interface$[],
                                      IntByReference id);

    int wl_display_flush(wl_display display);

    int wl_display_roundtrip_queue(wl_display display,
                                   wl_event_queue queue);

    int wl_display_roundtrip(wl_display display);

    wl_event_queue wl_display_create_queue(wl_display display);

    int wl_display_prepare_read_queue(wl_display display,
                                      wl_event_queue queue);

    int wl_display_prepare_read(wl_display display);

    void wl_display_cancel_read(wl_display display);

    int wl_display_read_events(wl_display display);

    void wl_log_set_handler_client(wl_log_func_t handler);
}