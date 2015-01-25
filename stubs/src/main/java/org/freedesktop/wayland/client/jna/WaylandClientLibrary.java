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
package org.freedesktop.wayland.client.jna;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import org.freedesktop.wayland.util.jna.wl_dispatcher_func_t;
import org.freedesktop.wayland.util.jna.wl_log_func_t;

public class WaylandClientLibrary implements WaylandClientLibraryMapping {

    private static WaylandClientLibraryMapping INSTANCE;

    public static WaylandClientLibraryMapping INSTANCE() {
        if (INSTANCE == null) {
            Native.register(WaylandClientLibraryMapping.JNA_LIBRARY_NAME);
            INSTANCE = new WaylandClientLibrary();
        }
        return INSTANCE;
    }

    public native void wl_event_queue_destroy(final Pointer queue);

    public native void wl_proxy_marshal_array(final Pointer p,
                                              final int opcode,
                                              final Pointer args);

    public native Pointer wl_proxy_create(final Pointer factory,
                                          final Pointer interface$);

    public native Pointer wl_proxy_marshal_array_constructor(final Pointer proxy,
                                                             final int opcode,
                                                             final Pointer args,
                                                             final Pointer interface$);

    public native void wl_proxy_destroy(final Pointer proxy);

    public native int wl_proxy_add_listener(final Pointer proxy,
                                            final Pointer implementation,
                                            final Pointer data);

    public native Pointer wl_proxy_get_listener(final Pointer proxy);

    public native int wl_proxy_add_dispatcher(final Pointer proxy,
                                              final wl_dispatcher_func_t dispatcher_func,
                                              final Pointer dispatcher_data,
                                              final Pointer data);

    public native void wl_proxy_set_user_data(final Pointer proxy,
                                              final Pointer user_data);

    public native Pointer wl_proxy_get_user_data(final Pointer proxy);

    public native int wl_proxy_get_id(final Pointer proxy);

    public native String wl_proxy_get_class(final Pointer proxy);

    public native void wl_proxy_set_queue(final Pointer proxy,
                                          final Pointer queue);

    public native Pointer wl_display_connect(final Pointer name);

    public native Pointer wl_display_connect_to_fd(final int fd);

    public native void wl_display_disconnect(final Pointer display);

    public native int wl_display_get_fd(final Pointer display);

    public native int wl_display_dispatch(final Pointer display);

    public native int wl_display_dispatch_queue(final Pointer display,
                                                final Pointer queue);

    public native int wl_display_dispatch_queue_pending(final Pointer display,
                                                        final Pointer queue);

    public native int wl_display_dispatch_pending(final Pointer display);

    public native int wl_display_get_error(final Pointer display);

    public native int wl_display_get_protocol_error(final Pointer display,
                                                    final Pointer interface$,
                                                    final IntByReference id);

    public native int wl_display_flush(final Pointer display);

    public native int wl_display_roundtrip_queue(final Pointer display,
                                                 final Pointer queue);

    public native int wl_display_roundtrip(final Pointer display);

    public native Pointer wl_display_create_queue(final Pointer display);

    public native int wl_display_prepare_read_queue(final Pointer display,
                                                    final Pointer queue);

    public native int wl_display_prepare_read(final Pointer display);

    public native void wl_display_cancel_read(final Pointer display);

    public native int wl_display_read_events(final Pointer display);

    public native void wl_log_set_handler_client(final wl_log_func_t handler);

    public native void wl_list_init(final Pointer list);

    public native void wl_list_insert(final Pointer list,
                                      final Pointer elm);

    public native void wl_list_remove(final Pointer elm);

    public native int wl_list_length(final Pointer list);

    public native int wl_list_empty(final Pointer list);

    public native void wl_list_insert_list(final Pointer list,
                                           final Pointer other);

    public native void wl_array_init(final Pointer array);

    public native void wl_array_release(final Pointer array);

    public native Pointer wl_array_add(final Pointer array,
                                       final long size);

    public native int wl_array_copy(final Pointer array,
                                    final Pointer source);

    public native void free(final Pointer pointer);
}
