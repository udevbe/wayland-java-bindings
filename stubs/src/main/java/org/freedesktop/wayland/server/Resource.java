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
import org.freedesktop.wayland.server.jna.WaylandServerLibrary;
import org.freedesktop.wayland.server.jna.wl_resource_destroy_func_t;
import org.freedesktop.wayland.util.*;

/**
 * Server side implementation of a wayland object for a specific client.
 *
 * @param <I> Type of implementation that will be used to handle client requests.
 */
public abstract class Resource<I> implements WaylandObject<Pointer> {
    //keep refs to callbacks to they don't get garbage collected.
    private final Dispatcher                 dispatcher            = new Dispatcher(this);
    private final wl_resource_destroy_func_t nativeDestroyCallback = new wl_resource_destroy_func_t() {
        @Override
        public void apply(final Pointer resource) {
          Resource.this.valid = false;
          ObjectCache.remove(Resource.this.getNative());
        }
    };
    private final Pointer pointer;
    private final I       implementation;

    private boolean valid;

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
                                                        this.dispatcher,
                                                        Pointer.NULL,
                                                        Pointer.NULL,
                                                        this.nativeDestroyCallback);
    }

    protected Resource(final Pointer pointer) {
        this.pointer = pointer;
        this.implementation = null;
        this.valid = false;
        addDestroyListener(new Listener() {
            @Override
            public void handle() {
                remove();
                ObjectCache.remove(Resource.this.getNative());
            }
        });
        ObjectCache.store(getNative(),
                          this);
    }

    //TODO add static get(Pointer) method for each generated resource

    public int getVersion() {
        return WaylandServerLibrary.INSTANCE()
                                   .wl_resource_get_version(getNative());
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

    public void addDestroyListener(final Listener listener) {
        WaylandServerLibrary.INSTANCE()
                            .wl_resource_add_destroy_listener(getNative(),
                                                              listener.getNative());
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
     * @param opcode
     * @param args
     */
    public void postEvent(final int opcode,
                          final Arguments args) {
        WaylandServerLibrary.INSTANCE()
                            .wl_resource_post_event_array(getNative(),
                                                          opcode,
                                                          args.getNative());
    }

    /**
     * @param opcode
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

    public Pointer getNative() {
        return this.pointer;
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
    public int hashCode() {
        return getNative().hashCode();
    }

    @Override
    protected void finalize() throws Throwable {
        destroy();
        super.finalize();
    }
}