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
import org.freedesktop.wayland.server.jna.WaylandServerLibrary;
import org.freedesktop.wayland.server.jna.wl_resource_destroy_func_t;
import org.freedesktop.wayland.util.Arguments;
import org.freedesktop.wayland.util.Dispatcher;
import org.freedesktop.wayland.util.InterfaceMeta;
import org.freedesktop.wayland.util.ObjectCache;
import org.freedesktop.wayland.util.WaylandObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Server side implementation of a wayland object for a specific client.
 *
 * @param <I> Type of implementation that will be used to handle client requests.
 */
public abstract class Resource<I> implements WaylandObject {

    private final Pointer pointer;
    private final I       implementation;
    private final Set<DestroyListener> destroyListeners = new HashSet<DestroyListener>();
    private boolean valid;
    private static final wl_resource_destroy_func_t RESOURCE_DESTROY_FUNC = new wl_resource_destroy_func_t() {
        @Override
        public void apply(final Pointer resourcePointer) {
            Resource<?> resource = ObjectCache.from(resourcePointer);
            resource.notifyDestroyListeners();
            resource.destroyListeners.clear();
            resource.valid = false;
            ObjectCache.remove(resourcePointer);
        }
    };

    protected Resource(final Client client,
                       final int version,
                       final int id,
                       final I implementation) {
        this.implementation = implementation;
        this.pointer = WaylandServerLibrary.INSTANCE()
                                           .wl_resource_create(client.getNative(),
                                                               InterfaceMeta.get(getClass())
                                                                            .getNative(),
                                                               version,
                                                               id);
        this.valid = true;
        ObjectCache.store(getNative(),
                          this);
        WaylandServerLibrary.INSTANCE()
                            .wl_resource_set_dispatcher(getNative(),
                                                        Dispatcher.INSTANCE,
                                                        Pointer.NULL,
                                                        Pointer.NULL,
                                                        RESOURCE_DESTROY_FUNC);
    }

    public Pointer getNative() {
        return this.pointer;
    }

    //TODO add static get(Pointer) method for each generated resource
    //TODO wl_resource_post_no_memory
    //TODO wl_resource_queue_event_array
    //TODO wl_resource_queue_event

    protected Resource(final Pointer pointer) {
        this.pointer = pointer;
        this.implementation = null;
        this.valid = true;
        addDestroyListener(new Listener() {
            @Override
            public void handle() {
                notifyDestroyListeners();
                Resource.this.destroyListeners.clear();
                Resource.this.valid = false;
                ObjectCache.remove(pointer);
                free();
            }
        });
        ObjectCache.store(pointer,
                          this);
    }

    protected void addDestroyListener(final Listener listener) {
        WaylandServerLibrary.INSTANCE()
                            .wl_resource_add_destroy_listener(getNative(),
                                                              listener.getNative());
    }

    private void notifyDestroyListeners() {
        for (DestroyListener listener : new HashSet<DestroyListener>(this.destroyListeners)) {
            listener.handle();
        }
    }

    public I getImplementation() {
        return this.implementation;
    }

    public Client getClient() {
        return Client.get(WaylandServerLibrary.INSTANCE()
                                              .wl_resource_get_client(getNative()));
    }

    public int getId() {
        return WaylandServerLibrary.INSTANCE()
                                   .wl_resource_get_id(getNative());
    }

    public int getVersion() {
        return WaylandServerLibrary.INSTANCE()
                                   .wl_resource_get_version(getNative());
    }

    public void register(final DestroyListener destroyListener) {
        this.destroyListeners.add(destroyListener);
    }

    public void unregister(final DestroyListener destroyListener) {
        this.destroyListeners.remove(destroyListener);
    }

    /**
     * Post an event to the client's object referred to by 'resource'.
     * 'opcode' is the event number generated from the protocol XML
     * description (the event name). The variable arguments are the event
     * parameters, in the order they appear in the protocol XML specification.
     * <p>
     * The variable arguments' types are:
     * <ul>
     * <li>type=uint: uint32_t</li>
     * <li>type=int: int32_t</li>
     * <li>type=fixed: wl_fixed_t</li>
     * <li>type=string: (const char *) to a nil-terminated string</li>
     * <li>type=array: (struct wl_array *)</li>
     * <li>type=fd: int, that is an open file descriptor</li>
     * <li>type=new_id: (struct wl_object *) or (struct wl_resource *)</li>
     * <li>type=object: (struct wl_object *) or (struct wl_resource *)</li>
     * </ul>
     *
     * @param opcode the protocol opcode
     * @param args   the protocol arguments
     */
    public void postEvent(final int opcode,
                          final Arguments args) {
        WaylandServerLibrary.INSTANCE()
                            .wl_resource_post_event_array(getNative(),
                                                          opcode,
                                                          args.getNative());
    }

    /**
     * @param opcode the protocol opcode
     *
     * @see #postEvent(int, org.freedesktop.wayland.util.Arguments)
     */
    public void postEvent(final int opcode) {
        WaylandServerLibrary.INSTANCE()
                            .wl_resource_post_event_array(getNative(),
                                                          opcode,
                                                          Pointer.NULL);
    }

    public void postError(final int code,
                          final String msg) {
        WaylandServerLibrary.INSTANCE()
                            .wl_resource_post_error(getNative(),
                                                    code,
                                                    msg);
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
        if (!(o instanceof Resource)) {
            return false;
        }

        final Resource resource = (Resource) o;

        return getNative().equals(resource.getNative());
    }

    @Override
    protected void finalize() throws Throwable {
        destroy();
        super.finalize();
    }

    public void destroy() {
        if (isValid()) {
            WaylandServerLibrary.INSTANCE()
                                .wl_resource_destroy(getNative());
        }
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }
}