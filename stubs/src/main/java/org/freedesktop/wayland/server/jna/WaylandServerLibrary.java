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
package org.freedesktop.wayland.server.jna;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import org.freedesktop.wayland.util.jna.wl_dispatcher_func_t;
import org.freedesktop.wayland.util.jna.wl_interface;
import org.freedesktop.wayland.util.jna.wl_list;
import org.freedesktop.wayland.util.jna.wl_log_func_t;

public class WaylandServerLibrary implements WaylandServerLibraryMapping {

    private static WaylandServerLibraryMapping INSTANCE;

    public static WaylandServerLibraryMapping INSTANCE() {
        if (INSTANCE == null) {
            Native.register(WaylandServerLibraryMapping.JNA_LIBRARY_NAME);
            INSTANCE = new WaylandServerLibrary();
        }
        return INSTANCE;
    }

    public native Pointer wl_event_loop_create();

    public native void wl_event_loop_destroy(final Pointer loop);

    public native Pointer wl_event_loop_add_fd(final Pointer loop,
                                               final int fd,
                                               final int mask,
                                               final wl_event_loop_fd_func_t func,
                                               final Pointer data);

    public native int wl_event_source_fd_update(final Pointer source,
                                                final int mask);

    public native Pointer wl_event_loop_add_timer(final Pointer loop,
                                                  final wl_event_loop_timer_func_t func,
                                                  final Pointer data);

    public native Pointer wl_event_loop_add_signal(final Pointer loop,
                                                   final int signal_number,
                                                   final wl_event_loop_signal_func_t func,
                                                   final Pointer data);

    public native int wl_event_source_timer_update(final Pointer source,
                                                   final int ms_delay);

    public native int wl_event_source_remove(final Pointer source);

    public native void wl_event_source_check(final Pointer source);

    public native int wl_event_loop_dispatch(final Pointer loop,
                                             final int timeout);

    public native void wl_event_loop_dispatch_idle(final Pointer loop);

    public native Pointer wl_event_loop_add_idle(final Pointer loop,
                                                 final wl_event_loop_idle_func_t func,
                                                 final Pointer data);

    public native int wl_event_loop_get_fd(final Pointer loop);

    public native void wl_event_loop_add_destroy_listener(final Pointer loop,
                                                          final wl_listener listener);

    public native wl_listener wl_event_loop_get_destroy_listener(final Pointer loop,
                                                                 final wl_notify_func_t notify$);

    public native Pointer wl_display_create();

    public native void wl_display_destroy(final Pointer display);

    public native Pointer wl_display_get_event_loop(final Pointer display);

    public native int wl_display_add_socket(final Pointer display,
                                            final Pointer name);

    public native String wl_display_add_socket_auto(final Pointer display);

    public native void wl_display_terminate(final Pointer display);

    public native void wl_display_run(final Pointer display);

    public native void wl_display_flush_clients(final Pointer display);

    public native int wl_display_get_serial(final Pointer display);

    public native int wl_display_next_serial(final Pointer display);

    public native void wl_display_add_destroy_listener(final Pointer display,
                                                       final wl_listener listener);

    public native wl_listener wl_display_get_destroy_listener(final Pointer display,
                                                              final wl_notify_func_t notify$);

    public native Pointer wl_global_create(final Pointer display,
                                           final wl_interface interface$,
                                           final int version,
                                           final Pointer data,
                                           final wl_global_bind_func_t bind);

    public native void wl_global_destroy(final Pointer global);

    public native Pointer wl_client_create(final Pointer display,
                                           final int fd);

    public native void wl_client_destroy(final Pointer client);

    public native void wl_client_flush(final Pointer client);

    public native void wl_client_get_credentials(final Pointer client,
                                                 final IntByReference pid,
                                                 final IntByReference uid,
                                                 final IntByReference gid);

    public native void wl_client_add_destroy_listener(final Pointer client,
                                                      final wl_listener listener);

    public native wl_listener wl_client_get_destroy_listener(final Pointer client,
                                                             final wl_notify_func_t notify$);

    public native Pointer wl_client_get_object(final Pointer client,
                                               final int id);

    public native void wl_client_post_no_memory(final Pointer client);

    public native int wl_client_add_resource(final Pointer client,
                                             final Pointer resource);

    public native Pointer wl_client_add_object(final Pointer client,
                                               final wl_interface interface$,
                                               final Pointer implementation,
                                               final int id,
                                               final Pointer data);

    public native Pointer wl_client_new_object(final Pointer client,
                                               final wl_interface interface$,
                                               final Pointer implementation,
                                               final Pointer data);

    public native Pointer wl_display_add_global(final Pointer display,
                                                final wl_interface interface$,
                                                final Pointer data,
                                                final wl_global_bind_func_t bind);

    public native void wl_display_remove_global(final Pointer display,
                                                final Pointer global);

    public native void wl_resource_post_event_array(final Pointer resource,
                                                    final int opcode,
                                                    final Pointer args);

    public native void wl_resource_queue_event_array(final Pointer resource,
                                                     final int opcode,
                                                     final Pointer args);

    public native void wl_resource_post_no_memory(final Pointer resource);

    public native Pointer wl_client_get_display(final Pointer client);

    public native Pointer wl_resource_create(final Pointer client,
                                             final wl_interface interface$,
                                             final int version,
                                             final int id);

    public native void wl_resource_set_implementation(final Pointer resource,
                                                      final Pointer implementation,
                                                      final Pointer data,
                                                      final wl_resource_destroy_func_t destroy);

    public native void wl_resource_set_dispatcher(final Pointer resource,
                                                  final wl_dispatcher_func_t dispatcher,
                                                  final Pointer implementation,
                                                  final Pointer data,
                                                  final wl_resource_destroy_func_t destroy);

    public native void wl_resource_destroy(final Pointer resource);

    public native int wl_resource_get_id(final Pointer resource);

    public native wl_list wl_resource_get_link(final Pointer resource);

    public native Pointer wl_resource_from_link(final wl_list resource);

    public native Pointer wl_resource_find_for_client(final wl_list list,
                                                      final Pointer client);

    public native Pointer wl_resource_get_client(final Pointer resource);

    public native void wl_resource_set_user_data(final Pointer resource,
                                                 final Pointer data);

    public native Pointer wl_resource_get_user_data(final Pointer resource);

    public native int wl_resource_get_version(final Pointer resource);

    public native void wl_resource_set_destructor(final Pointer resource,
                                                  final wl_resource_destroy_func_t destroy);

    public native int wl_resource_instance_of(final Pointer resource,
                                              final wl_interface interface$,
                                              final Pointer implementation);

    public native void wl_resource_add_destroy_listener(final Pointer resource,
                                                        final wl_listener listener);

    public native wl_listener wl_resource_get_destroy_listener(final Pointer resource,
                                                               final wl_notify_func_t notify$);

    public native void wl_shm_buffer_begin_access(final Pointer buffer);

    public native void wl_shm_buffer_end_access(final Pointer buffer);

    public native Pointer wl_shm_buffer_get(final Pointer resource);

    public native Pointer wl_shm_buffer_get_data(final Pointer buffer);

    public native int wl_shm_buffer_get_stride(final Pointer buffer);

    public native int wl_shm_buffer_get_width(final Pointer buffer);

    public native int wl_shm_buffer_get_height(final Pointer buffer);

    public native int wl_display_init_shm(final Pointer display);

    public native IntByReference wl_display_add_shm_format(final Pointer display,
                                                           final int format);

    public native Pointer wl_shm_buffer_create(final Pointer client,
                                               final int id,
                                               final int width,
                                               final int height,
                                               final int stride,
                                               final int format);

    public native void wl_log_set_handler_server(final wl_log_func_t handler);

    public native int wl_shm_buffer_get_format(final Pointer buffer);

    public native void wl_resource_post_error(final Pointer pointer,
                                              final int code,
                                              final String msg);

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
