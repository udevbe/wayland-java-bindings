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

import com.sun.jna.Pointer;
import org.freedesktop.wayland.HasNative;
import org.freedesktop.wayland.server.jna.WaylandServerLibrary;
import org.freedesktop.wayland.util.ObjectCache;

public class Client implements HasNative<Pointer> {

    private final Pointer pointer;

    private boolean valid;

    protected Client(final Pointer pointer) {
        this.pointer = pointer;
        addDestroyListener(new Listener() {
            @Override
            public void handle() {
                remove();
                Client.this.valid = false;
                ObjectCache.remove(Client.this.getNative());
            }
        });
        this.valid = true;
        ObjectCache.store(getNative(),
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
     *
     * @return The new client object or NULL on failure.
     */
    public static Client create(final Display display,
                                final int fd) {
        return Client.get(WaylandServerLibrary.INSTANCE()
                                              .wl_client_create(display.getNative(),
                                                                fd));
    }

    public static Client get(final Pointer pointer) {
        Client client = ObjectCache.from(pointer);
        if (client == null) {
            client = new Client(pointer);
        }
        return client;
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
        WaylandServerLibrary.INSTANCE()
                            .wl_client_flush(getNative());
    }

    public void addDestroyListener(final Listener listener) {
        WaylandServerLibrary.INSTANCE()
                            .wl_client_add_destroy_listener(getNative(),
                                                            listener.getNative());
    }

    /**
     * Get the display object for the given client
     * <p/>
     *
     * @return The display object the client is associated with.
     */
    public Display getDisplay() {
        return Display.get(WaylandServerLibrary.INSTANCE()
                                               .wl_client_get_display(getNative()));
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }

    public void destroy() {
        if (isValid()) {
            WaylandServerLibrary.INSTANCE()
                                .wl_client_destroy(getNative());
        }
    }

    public Pointer getNative() {
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

    @Override
    protected void finalize() throws Throwable {
        destroy();
        super.finalize();
    }
}

