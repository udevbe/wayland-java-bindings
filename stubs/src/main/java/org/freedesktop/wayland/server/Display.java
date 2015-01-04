/*
 * Copyright © 2014 Erik De Rijcke
 *
 * Permission to use, copy, modify, distribute, and sell this software and its
 * documentation for any purpose is hereby granted without fee, provided that
 * the above copyright notice appear in all copies and that both that copyright
 * notice and this permission notice appear in supporting documentation, and
 * that the name of the copyright holders not be used in advertising or
 * publicity pertaining to distribution of the software without specific,
 * written prior permission.  The copyright holders make no representations
 * about the suitability of this software for any purpose.  It is provided "as
 * is" without express or implied warranty.
 *
 * THE COPYRIGHT HOLDERS DISCLAIM ALL WARRANTIES WITH REGARD TO THIS SOFTWARE,
 * INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS, IN NO
 * EVENT SHALL THE COPYRIGHT HOLDERS BE LIABLE FOR ANY SPECIAL, INDIRECT OR
 * CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE,
 * DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 * TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE
 * OF THIS SOFTWARE.
 */
package org.freedesktop.wayland.server;

import com.sun.jna.Pointer;
import org.freedesktop.wayland.HasNative;
import org.freedesktop.wayland.server.jna.WaylandServerLibrary;
import org.freedesktop.wayland.server.jna.wl_display;
import org.freedesktop.wayland.server.jna.wl_event_loop;
import org.freedesktop.wayland.util.ObjectCache;

public class Display implements HasNative<wl_display> {

    private final wl_display pointer;

    public Display(final wl_display pointer) {
        this.pointer = pointer;
        ObjectCache.store(getNative().getPointer(),
                          this);
    }

    /**
     * Create Wayland display object.
     * <p/>
     * This creates the wl_display object.
     *
     * @return The Wayland display object. Null if failed to create
     */
    public static Display create() {
        return new Display(WaylandServerLibrary.INSTANCE.wl_display_create());
    }

    public int addSocket(final String name) {
        return WaylandServerLibrary.INSTANCE.wl_display_add_socket(getNative(),
                                                                   name);
    }

    public void terminate() {
        WaylandServerLibrary.INSTANCE.wl_display_terminate(getNative());
    }

    public void run() {
        WaylandServerLibrary.INSTANCE.wl_display_run(getNative());
    }

    public void flushClients() {
        WaylandServerLibrary.INSTANCE.wl_display_flush_clients(getNative());
    }

    /**
     * Get the current serial number
     * <p/>
     * This function returns the most recent serial number, but does not
     * increment it.
     */
    public int getSerial() {
        return WaylandServerLibrary.INSTANCE.wl_display_get_serial(getNative());
    }

    /**
     * Get the next serial number
     * <p/>
     * This function increments the display serial number and returns the
     * new value.
     */
    public int nextSerial() {
        return WaylandServerLibrary.INSTANCE.wl_display_next_serial(getNative());
    }

    public EventLoop getEventLoop() {
        final wl_event_loop wlEventLoop = WaylandServerLibrary.INSTANCE.wl_display_get_event_loop(getNative());
        final Pointer eventLoopPointer = wlEventLoop.getPointer();
        final EventLoop eventLoop = ObjectCache.from(eventLoopPointer);
        return eventLoop == null ? new EventLoop(wlEventLoop) : eventLoop;
    }

    public void destroy() {
        ObjectCache.remove(getNative().getPointer());
        WaylandServerLibrary.INSTANCE.wl_display_destroy(getNative());

    }

    public void addDestroyListener(final Listener listener) {
        WaylandServerLibrary.INSTANCE.wl_display_add_destroy_listener(getNative(),
                                                                      listener.getNative());
    }

    public int initShm() {
        return WaylandServerLibrary.INSTANCE.wl_display_init_shm(getNative());
    }

    /**
     * Add support for a wl_shm pixel format
     * <p/>
     * Add the specified wl_shm format to the list of formats the wl_shm
     * object advertises when a client binds to it.  Adding a format to
     * the list means that clients will know that the compositor supports
     * this format and may use it for creating wl_shm buffers.  The
     * compositor must be able to handle the pixel format when a client
     * requests it.
     * <p/>
     * The compositor by default supports WL_SHM_FORMAT_ARGB8888 and
     * WL_SHM_FORMAT_XRGB8888.
     * <p/>
     *
     * @param format The wl_shm pixel format to advertise
     * @return A pointer to the wl_shm format that was added to the list or NULL if adding it to the list failed.
     */
    public int addShmFormat(final int format) {
        return WaylandServerLibrary.INSTANCE.wl_display_add_shm_format(getNative(),
                                                                       format)
                                            .getValue();
    }

    public wl_display getNative() {
        return this.pointer;
    }

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
    public int hashCode() {
        return getNative().hashCode();
    }
}

