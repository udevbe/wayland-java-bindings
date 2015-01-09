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

import org.freedesktop.wayland.HasPointer;
import org.freedesktop.wayland.util.InterfaceMeta;
import org.freedesktop.wayland.util.ObjectCache;

public abstract class Global<R extends Resource<?>> implements HasPointer {

  private long pointer;

    protected Global(final Display display,
                     final Class<R> resourceClass,
                     final int version) {
        if (version <= 0) {
            throw new IllegalArgumentException("Version must be bigger than 0");
        }
        this.pointer = WlServerJNI.createGlobal(display.getPointer(),
                                              InterfaceMeta.get(resourceClass)
                                                  .getPointer(),
                                              version,
                                              this);
        ObjectCache.store(getPointer(),
                        this);
    }

    public long getPointer() {
        return this.pointer;
    }

    //called from jni
    protected void bindClient(final long clientPointer,
                              final int version,
                              final int id) {
        final Client client = ObjectCache.from(clientPointer);
        R resource = onBindClient(client == null ? new Client(clientPointer) : client,
                                  version,
                                  id);
        //TODO add some extra checks?
    }

    public void destroyGlobal() {
        ObjectCache.remove(getPointer());
        WlServerJNI.destroyGlobal(getPointer());
        this.pointer = 0;
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

        return getPointer() == global.getPointer();
    }

    @Override
    public int hashCode() {
        return (int) getPointer();
    }
}

