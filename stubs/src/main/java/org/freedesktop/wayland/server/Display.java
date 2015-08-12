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
package org.freedesktop.wayland.server;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import org.freedesktop.wayland.HasNative;
import org.freedesktop.wayland.server.jna.WaylandServerLibrary;
import org.freedesktop.wayland.util.ObjectCache;

import java.util.HashSet;
import java.util.Set;

import static org.freedesktop.wayland.HasNative.Precondition.checkValid;

public class Display implements HasNative<Pointer> {

    public static final int OBJECT_ID = 1;

    private final Pointer pointer;
    private final Set<DestroyListener> destroyListeners = new HashSet<DestroyListener>();
    private boolean valid;


    protected Display(final Pointer pointer) {
        this.pointer = pointer;
        this.valid = true;
        addDestroyListener(new Listener() {
            @Override
            public void handle() {
                notifyDestroyListeners();
                Display.this.destroyListeners.clear();
                Display.this.valid = false;
                ObjectCache.remove(Display.this.getNative());
                free();
            }
        });
        ObjectCache.store(getNative(),
                          this);
    }

    protected void addDestroyListener(final Listener listener) {
        checkValid(this);
        WaylandServerLibrary.INSTANCE()
                            .wl_display_add_destroy_listener(getNative(),
                                                             listener.getNative());
    }

    private void notifyDestroyListeners() {
        for (DestroyListener listener : new HashSet<DestroyListener>(this.destroyListeners)) {
            listener.handle();
        }
    }

    public Pointer getNative() {
        return this.pointer;
    }

    /**
     * Create Wayland display object. <p> This creates the wl_display object.
     *
     * @return The Wayland display object. Null if failed to create
     */
    public static Display create() {
        return Display.get(WaylandServerLibrary.INSTANCE()
                                               .wl_display_create());
    }

    public static Display get(final Pointer pointer) {
        if (pointer == null) {
            return null;
        }
        Display display = ObjectCache.from(pointer);
        if (display == null) {
            display = new Display(pointer);
        }
        return display;
    }

    /**
     * Add a socket to Wayland display for the clients to connect. </p> This adds a Unix socket to Wayland display which
     * can be used by clients to connect to Wayland display. <p> If NULL is passed as name, then it would look for
     * WAYLAND_DISPLAY env variable for the socket name. If WAYLAND_DISPLAY is not set, then default wayland-0 is used.
     * <p> The Unix socket will be created in the directory pointed to by environment variable XDG_RUNTIME_DIR. If
     * XDG_RUNTIME_DIR is not set, then this function fails and returns -1. <p> The length of socket path, i.e., the
     * path set in XDG_RUNTIME_DIR and the socket name, must not exceed the maxium length of a Unix socket path. The
     * function also fails if the user do not have write permission in the XDG_RUNTIME_DIR path or if the socket name is
     * already in use.
     *
     * @param name Name of the Unix socket.
     *
     * @return 0 if success. -1 if failed.
     */

    public int addSocket(final String name) {
        checkValid(this);
        final Pointer m = new Memory(name.length() + 1);
        m.setString(0,
                    name);
        return WaylandServerLibrary.INSTANCE()
                                   .wl_display_add_socket(getNative(),
                                                          m);
    }

    public String addSocketAuto() {
        return WaylandServerLibrary.INSTANCE()
                                   .wl_display_add_socket_auto(getNative())
                                   .getString(0);
    }

    public void terminate() {
        checkValid(this);
        WaylandServerLibrary.INSTANCE()
                            .wl_display_terminate(getNative());
    }

    public void run() {
        checkValid(this);
        WaylandServerLibrary.INSTANCE()
                            .wl_display_run(getNative());
    }

    public void flushClients() {
        checkValid(this);
        WaylandServerLibrary.INSTANCE()
                            .wl_display_flush_clients(getNative());
    }

    /**
     * Get the current serial number <p> This function returns the most recent serial number, but does not increment
     * it.
     */
    public int getSerial() {
        checkValid(this);
        return WaylandServerLibrary.INSTANCE()
                                   .wl_display_get_serial(getNative());
    }

    /**
     * Get the next serial number <p> This function increments the display serial number and returns the new value.
     */
    public int nextSerial() {
        checkValid(this);
        return WaylandServerLibrary.INSTANCE()
                                   .wl_display_next_serial(getNative());
    }

    public EventLoop getEventLoop() {
        checkValid(this);
        return EventLoop.get(WaylandServerLibrary.INSTANCE()
                                                 .wl_display_get_event_loop(getNative()));
    }

    public void register(final DestroyListener destroyListener) {
        this.destroyListeners.add(destroyListener);
    }

    public void unregister(final DestroyListener destroyListener) {
        this.destroyListeners.remove(destroyListener);
    }

    public int initShm() {
        checkValid(this);
        return WaylandServerLibrary.INSTANCE()
                                   .wl_display_init_shm(getNative());
    }

    /**
     * Add support for a wl_shm pixel format <p> Add the specified wl_shm format to the list of formats the wl_shm
     * object advertises when a client binds to it.  Adding a format to the list means that clients will know that the
     * compositor supports this format and may use it for creating wl_shm buffers.  The compositor must be able to
     * handle the pixel format when a client requests it. <p> The compositor by default supports WL_SHM_FORMAT_ARGB8888
     * and WL_SHM_FORMAT_XRGB8888. <p>
     *
     * @param format The wl_shm pixel format to advertise
     *
     * @return A pointer to the wl_shm format that was added to the list or NULL if adding it to the list failed.
     */
    public int addShmFormat(final int format) {
        checkValid(this);
        final Pointer formatPointer = WaylandServerLibrary.INSTANCE()
                                                          .wl_display_add_shm_format(getNative(),
                                                                                     format);
        return formatPointer == null ? 0 : formatPointer.getInt(0);
    }

    @Override
    public int hashCode() {
        return getNative().hashCode();
    }

    //TODO wl_display_get_additional_shm_formats

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Display display = (Display) o;

        return getNative().equals(display.getNative());
    }

    @Override
    protected void finalize() throws Throwable {
        destroy();
        super.finalize();
    }

    /**
     * Destroy Wayland display object.
     * <p>
     * This function emits the wl_display destroy signal, releases all the sockets added to this display, free's all the
     * globals associated with this display, free's memory of additional shared memory formats and destroy the display
     * object.
     *
     * @see #addDestroyListener(Listener)
     */
    public void destroy() {
        if (isValid()) {
            WaylandServerLibrary.INSTANCE()
                                .wl_display_destroy(getNative());
        }
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }
}

