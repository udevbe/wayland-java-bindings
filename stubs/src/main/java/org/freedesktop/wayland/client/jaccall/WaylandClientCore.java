package org.freedesktop.wayland.client.jaccall;


import org.freedesktop.jaccall.Lib;
import org.freedesktop.jaccall.Pointer;
import org.freedesktop.jaccall.Ptr;
import org.freedesktop.jaccall.Unsigned;
import org.freedesktop.wayland.util.jaccall.wl_argument;
import org.freedesktop.wayland.util.jaccall.wl_dispatcher_func_t;
import org.freedesktop.wayland.util.jaccall.wl_interface;
import org.freedesktop.wayland.util.jaccall.wl_log_func_t;

@Lib("wayland-client")
public class WaylandClientCore {

    private static WaylandClientCore INSTANCE;

    public static WaylandClientCore INSTANCE() {
        if (INSTANCE == null) {
            new WaylandClientCore_Symbols().link();
            INSTANCE = new WaylandClientCore();
        }
        return INSTANCE;
    }

    public native void wl_event_queue_destroy(@Ptr long queue);

    public native void wl_proxy_marshal_array(@Ptr long p,
                                              @Unsigned int opcode,
                                              @Ptr(wl_argument.class) long args);

    @Ptr
    public native long wl_proxy_create(@Ptr long factory,
                                       @Ptr(wl_interface.class) long interface_);

    @Ptr
    public native long wl_proxy_marshal_array_constructor(@Ptr long proxy,
                                                          @Unsigned int opcode,
                                                          @Ptr(wl_argument.class) long args,
                                                          @Ptr(wl_interface.class) long interface_);

    @Ptr
    public native long wl_proxy_marshal_array_constructor_versioned(@Ptr long proxy,
                                                                    @Unsigned int opcode,
                                                                    @Ptr(wl_argument.class) long args,
                                                                    @Ptr(wl_interface.class) long interface_,
                                                                    @Unsigned int version);

    public native void wl_proxy_destroy(@Ptr long proxy);

    public native int wl_proxy_add_dispatcher(@Ptr long proxy,
                                              @Ptr(wl_dispatcher_func_t.class) long dispatcher_func,
                                              @Ptr(void.class) long dispatcher_data,
                                              @Ptr(void.class) long data);

    public native void wl_proxy_set_user_data(@Ptr long proxy,
                                              @Ptr(void.class) long user_data);

    @Ptr(void.class)
    public native long wl_proxy_get_user_data(@Ptr long proxy);

    @Unsigned
    public native int wl_proxy_get_version(@Ptr long proxy);

    @Unsigned
    public native int wl_proxy_get_id(@Ptr long proxy);

    @Ptr(String.class)
    public native long wl_proxy_get_class(@Ptr long proxy);

    public native void wl_proxy_set_queue(@Ptr long proxy,
                                          @Ptr long queue);

    @Ptr
    public native long wl_display_connect(@Ptr(String.class) long name);

    @Ptr
    public native long wl_display_connect_to_fd(int fd);

    public native void wl_display_disconnect(@Ptr long display);

    public native int wl_display_get_fd(@Ptr long display);

    public native int wl_display_dispatch(@Ptr long display);

    public native int wl_display_dispatch_queue(@Ptr long display,
                                                @Ptr long queue);

    public native int wl_display_dispatch_queue_pending(@Ptr long display,
                                                        @Ptr long queue);

    public native int wl_display_dispatch_pending(@Ptr long display);

    public native int wl_display_get_error(@Ptr long display);

    @Unsigned
    public native int wl_display_get_protocol_error(@Ptr long display,
                                                    @Ptr(Pointer.class) long interface_,
                                                    @Unsigned int id);

    public native int wl_display_flush(@Ptr long display);

    public native int wl_display_roundtrip_queue(@Ptr long display,
                                                 @Ptr long queue);

    public native int wl_display_roundtrip(@Ptr long display);

    @Ptr
    public native long wl_display_create_queue(@Ptr long display);

    public native int wl_display_prepare_read_queue(@Ptr long display,
                                                    @Ptr long queue);

    public native int wl_display_prepare_read(@Ptr long display);

    public native void wl_display_cancel_read(@Ptr long display);

    public native int wl_display_read_events(@Ptr long display);

    public native void wl_log_set_handler_client(@Ptr(wl_log_func_t.class) long handler);
}
