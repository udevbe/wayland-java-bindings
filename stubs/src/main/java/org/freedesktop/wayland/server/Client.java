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

public class Client implements HasNative<Pointer> {

    private final Pointer pointer;
    private       boolean valid;

    //private final Set<DestroyListener> destroyListeners = new HashSet<DestroyListener>();

    protected Client(final Pointer pointer) {
        this.pointer = pointer;
        this.valid = true;
//        addDestroyListener(new Listener() {
//            @Override
//            public void handle() {
        //notifyDestroyListeners();
        //Client.this.destroyListeners.clear();
        //Client.this.valid = false;
        //ObjectCache.remove(Client.this.pointer);
        //free();
//            }
//        });
//        ObjectCache.store(pointer,
//                          this);
    }

//    private void notifyDestroyListeners(){
//        for (DestroyListener listener : new HashSet<DestroyListener>(this.destroyListeners)) {
//            listener.handle();
//        }
//    }

    /**
     * Create a client for the given file descriptor
     * <p>
     * Given a file descriptor corresponding to one end of a socket, this
     * function will create a {@link Client} and add the new client to
     * the compositors client list.  At that point, the client is
     * initialized and ready to run, as if the client had connected to the
     * servers listening socket.  When the client eventually sends
     * requests to the compositor, the {@link Client} argument to the request
     * handler will be the client returned from this function.
     * <p>
     * The other end of the socket can be passed to
     * {@link WlDisplayProxy#connectToFd(int)} on the client side or used with the
     * WAYLAND_SOCKET environment variable on the client side.
     * <p>
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
        if (pointer == null) {
            return null;
        }
        Client client = ObjectCache.from(pointer);
        if (client == null) {
            client = new Client(pointer);
        }
        return client;
    }

    /**
     * Flush pending events to the client,
     * <p>
     * Events sent to clients are queued in a buffer and written to the
     * socket later - typically when the compositor has handled all
     * requests and goes back to block in the event loop.  This function
     * flushes all queued up events for a client immediately.
     */
    public void flush() {
        //checkValid(this);
        WaylandServerLibrary.INSTANCE()
                            .wl_client_flush(getNative());
    }

//    protected void addDestroyListener(final Listener listener) {
//        checkValid(this);
//        WaylandServerLibrary.INSTANCE()
//                            .wl_client_add_destroy_listener(getNative(),
//                                                            listener.getNative());
//    }

//    public void register(final DestroyListener destroyListener){
//        this.destroyListeners.add(destroyListener);
//    }

//    public void unregister(final DestroyListener destroyListener){
//        this.destroyListeners.remove(destroyListener);
//    }

    public Pointer getNative() {
        return this.pointer;
    }

    //TODO wl_client_get_object
    //TODO wl_client_post_no_memory
    //TODO wl_client_get_credentials

    @Override
    public boolean isValid() {
        return this.valid;
    }

    /**
     * Get the display object for the given client
     * <p>
     *
     * @return The display object the client is associated with.
     */
    public Display getDisplay() {
        //checkValid(this);
        return Display.get(WaylandServerLibrary.INSTANCE()
                                               .wl_client_get_display(getNative()));
    }

    /**
     * Look up an object in the client name space. This looks up an object in the client object name space by its
     * object ID.
     *
     * @param id The object id
     *
     * @return The object or null if there is not object for the given ID
     */
    public Resource<?> getObject(int id) {
        return ObjectCache.from(WaylandServerLibrary.INSTANCE()
                                                    .wl_client_get_object(getNative(),
                                                                          id));
    }

    /**
     * Return Unix credentials for the client
     * <p>
     * This function returns the process ID, the user ID and the group ID
     * for the given client.  The credentials come from getsockopt() with
     * SO_PEERCRED, on the client socket fd.
     * <p>
     * Be aware that for clients that a compositor forks and execs and
     * then connects using socketpair(), this function will return the
     * credentials for the compositor.  The credentials for the socketpair
     * are set at creation time in the compositor.
     */
    public ClientCredentials getCredentials() {
        final Pointer pid = new Memory(Integer.SIZE);
        final Pointer uid = new Memory(Integer.SIZE);
        final Pointer gid = new Memory(Integer.SIZE);

        WaylandServerLibrary.INSTANCE()
                            .wl_client_get_credentials(getNative(),
                                                       pid,
                                                       uid,
                                                       gid);

        return new ClientCredentials(pid.getInt(0),
                                     uid.getInt(0),
                                     gid.getInt(0));
    }

    public void destroy() {
        // if (isValid()) {
        WaylandServerLibrary.INSTANCE()
                            .wl_client_destroy(getNative());
        // }
    }

    @Override
    public int hashCode() {
        return getNative().hashCode();
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
    protected void finalize() throws Throwable {
        //destroy();
        super.finalize();
    }
}

