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

import org.freedesktop.jaccall.Pointer;
import org.freedesktop.jaccall.Ptr;
import org.freedesktop.wayland.server.jaccall.Pointerwl_resource_destroy_func_t;
import org.freedesktop.wayland.server.jaccall.WaylandServerCore;
import org.freedesktop.wayland.server.jaccall.wl_resource_destroy_func_t;
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

    private static final Pointer<wl_resource_destroy_func_t> RESOURCE_DESTROY_FUNC = Pointerwl_resource_destroy_func_t.nref(new wl_resource_destroy_func_t() {
        @Override
        public void $(final @Ptr long resourcePointer) {
            final Resource<?> resource = ObjectCache.from(resourcePointer);
            resource.notifyDestroyListeners();
            resource.destroyListeners.clear();
            ObjectCache.remove(resourcePointer);

            resource.jObjectPointer.close();
        }
    });

    public final  Long pointer;
    private final I    implementation;
    private final Set<DestroyListener> destroyListeners = new HashSet<>();

    private final Pointer<Object> jObjectPointer;

    protected Resource(final Client client,
                       final int version,
                       final int id,
                       final I implementation) {
        this.implementation = implementation;
        final long resourcePointer = WaylandServerCore.INSTANCE()
                                                      .wl_resource_create(client.pointer,
                                                                          InterfaceMeta.get(getClass())
                                                                                       .getNative().address,
                                                                          version,
                                                                          id);
        this.pointer = resourcePointer;
        ObjectCache.store(this.pointer,
                          this);

        this.jObjectPointer = Pointer.from(this);

        WaylandServerCore.INSTANCE()
                         .wl_resource_set_dispatcher(resourcePointer,
                                                     Dispatcher.INSTANCE.address,
                                                     this.jObjectPointer.address,
                                                     0L,
                                                     RESOURCE_DESTROY_FUNC.address);
    }

    //TODO add static get(Pointer) method for each generated resource
    //TODO wl_resource_post_no_memory
    //TODO wl_resource_queue_event_array
    //TODO wl_resource_queue_event

    protected Resource(final Long pointer) {
        this.jObjectPointer = Pointer.from(this);
        this.pointer = pointer;
        this.implementation = null;
        addDestroyListener(new Listener() {
            @Override
            public void handle() {
                notifyDestroyListeners();
                Resource.this.destroyListeners.clear();
                ObjectCache.remove(Resource.this.pointer);
                Resource.this.jObjectPointer.close();

                free();
            }
        });
        ObjectCache.store(pointer,
                          this);
    }

    protected void addDestroyListener(final Listener listener) {
        WaylandServerCore.INSTANCE()
                         .wl_resource_add_destroy_listener(this.pointer,
                                                           listener.pointer.address);
    }

    private void notifyDestroyListeners() {
        for (final DestroyListener listener : new HashSet<>(this.destroyListeners)) {
            listener.handle();
        }
    }

    public I getImplementation() {
        return this.implementation;
    }

    public Client getClient() {
        return Client.get(WaylandServerCore.INSTANCE()
                                           .wl_resource_get_client(this.pointer));
    }

    public int getId() {
        return WaylandServerCore.INSTANCE()
                                .wl_resource_get_id(this.pointer);
    }

    public int getVersion() {
        return WaylandServerCore.INSTANCE()
                                .wl_resource_get_version(this.pointer);
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
     * <p/>
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
        WaylandServerCore.INSTANCE()
                         .wl_resource_post_event_array(this.pointer,
                                                       opcode,
                                                       args.pointer.address);
        args.pointer.close();
    }

    /**
     * @param opcode the protocol opcode
     *
     * @see #postEvent(int, org.freedesktop.wayland.util.Arguments)
     */
    public void postEvent(final int opcode) {
        WaylandServerCore.INSTANCE()
                         .wl_resource_post_event_array(this.pointer,
                                                       opcode,
                                                       0L);
    }

    public void postError(final int code,
                          final String msg) {
        WaylandServerCore.INSTANCE()
                         .wl_resource_post_error(this.pointer,
                                                 code,
                                                 Pointer.nref(msg).address);
    }

    @Override
    public int hashCode() {
        return this.pointer.hashCode();
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

        return this.pointer.equals(resource.pointer);
    }

    public void destroy() {
        WaylandServerCore.INSTANCE()
                         .wl_resource_destroy(this.pointer);
    }
}