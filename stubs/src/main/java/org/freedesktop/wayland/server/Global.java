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
import org.freedesktop.wayland.server.jna.wl_global_bind_func_t;
import org.freedesktop.wayland.util.InterfaceMeta;
import org.freedesktop.wayland.util.ObjectCache;

public abstract class Global<R extends Resource<?>> implements HasNative<Long> {
    private final wl_global_bind_func_t nativeCallback = new wl_global_bind_func_t() {

        @Override
        public void apply(final long wlClient,
                          final long data,
                          final int version,
                          final int id) {
            final Client client = ObjectCache.from(wlClient);
            onBindClient(client == null ? new Client(wlClient) : client,
                         version,
                         id);
        }
    };

    private final long pointer;

    protected Global(final Display display,
                     final Class<R> resourceClass,
                     final int version) {
        if (version <= 0) {
            throw new IllegalArgumentException("Version must be bigger than 0");
        }

        this.pointer = WaylandServerLibrary.INSTANCE.wl_global_create(display.getNative(),
                                                                      InterfaceMeta.get(resourceClass)
                                                                          .getNative(),
                                                                      version,
                                                                      0,
                                                                      this.nativeCallback);
      ObjectCache.store(getNative(),
                        this);
    }

    public Long getNative() {
        return this.pointer;
    }

    //called from jni
    protected void bindClient(final long clientPointer,
                              final int version,
                              final int id) {

        //TODO add some extra checks?
    }

    public void destroy() {
        ObjectCache.remove(getNative());
      WaylandServerLibrary.INSTANCE.wl_global_destroy(getNative());
    }

    public abstract R onBindClient(Client client,
                                   int version,
                                   int id);

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Global)) {
            return false;
        }

        final Global global = (Global) o;

        return getNative().equals(global.getNative());
    }

    @Override
    public int hashCode() {
        return getNative().hashCode();
    }
}

