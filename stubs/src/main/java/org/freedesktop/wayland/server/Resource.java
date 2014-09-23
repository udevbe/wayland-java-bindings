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

import org.freedesktop.wayland.util.Arguments;
import org.freedesktop.wayland.util.InterfaceMeta;
import org.freedesktop.wayland.util.ObjectCache;
import org.freedesktop.wayland.util.WaylandObject;

/**
 * Server side implementation of a wayland object for a specific client.
 *
 * @param <I> Type of implementation that will be used to handle client requests.
 */
public abstract class Resource<I> implements WaylandObject {

    private final long pointer;

    protected Resource(final Client client,
                       final int version,
                       final int id,
                       final I implementation) {
        this.pointer = WlServerJNI.createResource(client.getPointer(),
                                                  InterfaceMeta.get(getClass())
                                                               .getPointer(),
                                                  version,
                                                  id);
        addDestroyListener(new Listener() {
            @Override
            public void handle() {
                ObjectCache.remove(getPointer());
                destroy();
            }
        });
        ObjectCache.store(getPointer(),
                          this);
        WlServerJNI.setDispatcher(getPointer(),
                                  implementation);
    }

    protected Resource(final long pointer){
        this.pointer = pointer;
    }

    public int getVersion() {
        return WlServerJNI.getVersion(getPointer());
    }

    public I getImplementation() {
        return (I) WlServerJNI.getImplementation(getPointer());
    }

    public Client getClient() {
        final long clientPointer = WlServerJNI.getClient(getPointer());
        return ObjectCache.from(clientPointer);
    }

    public int getId() {
        return WlServerJNI.getId(getPointer());
    }

    public void addDestroyListener(final Listener listener) {
        WlServerJNI.addResourceDestroyListener(getPointer(),
                                               listener.getPointer());
    }

    public void destroy() {
        WlServerJNI.destroyResource(getPointer());
        ObjectCache.remove(getPointer());
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
        WlServerJNI.postEvent(getPointer(),
                              opcode,
                              args.getPointer());
    }

    /**
     * @param opcode
     * @see #postEvent(int, org.freedesktop.wayland.util.Arguments)
     */
    public void postEvent(final int opcode) {
        WlServerJNI.postEvent(getPointer(),
                              opcode,
                              0);
    }

    public void postError(final int code,
                          final String msg) {
        WlServerJNI.postError(getPointer(),
                              code,
                              msg);
    }

    public long getPointer() {
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

        return getPointer() == resource.getPointer();
    }

    @Override
    public int hashCode() {
        return (int) getPointer();
    }
}