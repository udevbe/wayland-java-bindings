package org.freedesktop.wayland.client.jna;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import org.freedesktop.wayland.util.jna.*;


public interface WaylandClientLibrary extends WaylandUtilLibrary {

    public static final String               JNA_LIBRARY_NAME = "wayland-client";
    public static final NativeLibrary        JNA_NATIVE_LIB   = NativeLibrary.getInstance(WaylandClientLibrary.JNA_LIBRARY_NAME);
    public static final WaylandClientLibrary INSTANCE         = (WaylandClientLibrary) Native.loadLibrary(WaylandClientLibrary.JNA_LIBRARY_NAME,
                                                                                                          WaylandClientLibrary.class);

    void wl_event_queue_destroy(long queue);

    void wl_proxy_marshal_array(long p,
                                int opcode,
                                Pointer args);

  long wl_proxy_create(long factory,
                             wl_interface interface$);

  long wl_proxy_marshal_array_constructor(long proxy,
                                                int opcode,
                                                Pointer args,
                                                wl_interface interface$);

    void wl_proxy_destroy(long proxy);

    int wl_proxy_add_listener(long proxy,
                              long implementation,
                              long data);

  long wl_proxy_get_listener(long proxy);

    int wl_proxy_add_dispatcher(long proxy,
                                wl_dispatcher_func_t dispatcher_func,
                                long dispatcher_data,
                                long data);

    void wl_proxy_set_user_data(long proxy,
                                long user_data);

  long wl_proxy_get_user_data(long proxy);

    int wl_proxy_get_id(long proxy);

    String wl_proxy_get_class(long proxy);

    void wl_proxy_set_queue(long proxy,
                           long queue);

  long wl_display_connect(Pointer name);

  long wl_display_connect_to_fd(int fd);

    void wl_display_disconnect(long display);

    int wl_display_get_fd(long display);

    int wl_display_dispatch(long display);

    int wl_display_dispatch_queue(long display,
                                 long queue);

    int wl_display_dispatch_queue_pending(long display,
                                         long queue);

    int wl_display_dispatch_pending(long display);

    int wl_display_get_error(long display);

    int wl_display_get_protocol_error(long display,
                                      wl_interface.ByReference interface$[],
                                      IntByReference id);

    int wl_display_flush(long display);

    int wl_display_roundtrip_queue(long display,
                                  long queue);

    int wl_display_roundtrip(long display);

   long wl_display_create_queue(long display);

    int wl_display_prepare_read_queue(long display,
                                     long queue);

    int wl_display_prepare_read(long display);

    void wl_display_cancel_read(long display);

    int wl_display_read_events(long display);

    void wl_log_set_handler_client(wl_log_func_t handler);
}