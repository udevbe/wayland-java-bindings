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
            ObjectCache.remove(getNative());
        }
    };
    private final Pointer pointer;
    private final I       implementation;

    protected Resource(final Client client,
                       final int version,
                       final int id,
                       final I implementation) {
        this.implementation = implementation;
        this.pointer = WaylandServerLibrary.INSTANCE.wl_resource_create(client.getNative(),
                                                                        InterfaceMeta.get(getClass())
                                                                                     .getNative(),
                                                                        version,
                                                                        id);
        ObjectCache.store(getNative(),
                          this);
        WaylandServerLibrary.INSTANCE.wl_resource_set_dispatcher(this.pointer,
                                                                 this.dispatcher,
                                                                 Pointer.NULL,
                                                                 Pointer.NULL,
                                                                 this.nativeDestroyCallback);
    }

    public Resource(final Pointer pointer) {
        this.pointer = pointer;
        this.implementation = null;
    }

    public int getVersion() {
        return WaylandServerLibrary.INSTANCE.wl_resource_get_version(this.pointer);
    }

    public I getImplementation() {
        return this.implementation;
    }

    public Client getClient() {
        return ObjectCache.from(WaylandServerLibrary.INSTANCE.wl_resource_get_client(this.pointer));
    }

    public int getId() {
        return WaylandServerLibrary.INSTANCE.wl_resource_get_id(this.pointer);
    }

    public void addDestroyListener(final Listener listener) {
        WaylandServerLibrary.INSTANCE.wl_resource_add_destroy_listener(this.pointer,
                                                                       listener.getNative());
    }

    public void destroy() {
        ObjectCache.remove(getNative());
        WaylandServerLibrary.INSTANCE.wl_resource_destroy(this.pointer);
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
        WaylandServerLibrary.INSTANCE.wl_resource_post_event_array(this.pointer,
                                                                   opcode,
                                                                   args.getNative());
    }

    /**
     * @param opcode
     * @see #postEvent(int, org.freedesktop.wayland.util.Arguments)
     */
    public void postEvent(final int opcode) {
        WaylandServerLibrary.INSTANCE.wl_resource_post_event_array(this.pointer,
                                                                   opcode,
                                                                   Pointer.NULL);
    }

    public void postError(final int code,
                          final String msg) {
        WaylandServerLibrary.INSTANCE.wl_resource_post_error(this.pointer,
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
}