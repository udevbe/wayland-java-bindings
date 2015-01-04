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

import org.freedesktop.wayland.HasNative;
import org.freedesktop.wayland.server.jna.WaylandServerLibrary;
import org.freedesktop.wayland.server.jna.wl_client;
import org.freedesktop.wayland.util.ObjectCache;

public class Client implements HasNative<wl_client> {

    private final wl_client pointer;

    protected Client(final wl_client pointer) {
        this.pointer = pointer;
        ObjectCache.store(getNative().getPointer(),
                          this);
    }

    /**
     * Create a client for the given file descriptor
     * <p/>
     * Given a file descriptor corresponding to one end of a socket, this
     * function will create a {@link Client} and add the new client to
     * the compositors client list.  At that point, the client is
     * initialized and ready to run, as if the client had connected to the
     * servers listening socket.  When the client eventually sends
     * requests to the compositor, the {@link Client} argument to the request
     * handler will be the client returned from this function.
     * <p/>
     * The other end of the socket can be passed to
     * {@link WlDisplayProxy#connectToFd(int)} on the client side or used with the
     * WAYLAND_SOCKET environment variable on the client side.
     * <p/>
     * On failure this function sets errno accordingly and returns NULL.
     *
     * @param display The display object
     * @param fd      The file descriptor for the socket to the client
     * @return The new client object or NULL on failure.
     */
    public static Client create(final Display display,
                                final int fd) {
        return new Client(WaylandServerLibrary.INSTANCE.wl_client_create(display.getNative(),
                                                                         fd));
    }

    /**
     * Flush pending events to the client,
     * <p/>
     * Events sent to clients are queued in a buffer and written to the
     * socket later - typically when the compositor has handled all
     * requests and goes back to block in the event loop.  This function
     * flushes all queued up events for a client immediately.
     */
    public void flush() {
        WaylandServerLibrary.INSTANCE.wl_client_flush(getNative());
    }

    public void addDestroyListener(final Listener listener) {
        WaylandServerLibrary.INSTANCE.wl_client_add_destroy_listener(getNative(),
                                                                     listener.getNative());
    }

    /**
     * Get the display object for the given client
     * <p/>
     *
     * @return The display object the client is associated with.
     */
    public Display getDisplay() {
        return ObjectCache.from(WaylandServerLibrary.INSTANCE.wl_client_get_display(getNative())
                                                             .getPointer());
    }

    public void destroy() {
        ObjectCache.remove(getNative().getPointer());
        WaylandServerLibrary.INSTANCE.wl_client_destroy(getNative());
    }

    public wl_client getNative() {
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

        final Client client = (Client) o;

        return getNative().equals(client.getNative());
    }

    @Override
    public int hashCode() {
        return getNative().hashCode();
    }
}

