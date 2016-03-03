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

import com.github.zubnix.jaccall.Pointer;
import com.github.zubnix.jaccall.Ptr;
import com.github.zubnix.jaccall.Unsigned;
import org.freedesktop.wayland.server.jaccall.WaylandServerCore;
import org.freedesktop.wayland.server.jaccall.wl_global_bind_func_t;
import org.freedesktop.wayland.util.InterfaceMeta;
import org.freedesktop.wayland.util.ObjectCache;

import static org.freedesktop.wayland.server.jaccall.Pointerwl_global_bind_func_t.nref;

public abstract class Global<R extends Resource<?>> {

    private static final Pointer<wl_global_bind_func_t> FUNC_T_POINTER = nref(new wl_global_bind_func_t() {
        @Override
        public void $(@Ptr final long client,
                      @Ptr(Object.class) final long data,
                      @Unsigned final int version,
                      @Unsigned final int id) {
            final Global<?> global = (Global<?>) Pointer.wrap(Object.class,
                                                              data)
                                                        .dref();
            global.onBindClient(Client.get(client),
                                version,
                                id);
        }
    });

    private final Long            pointer;
    private final Pointer<Object> jObjectPointer;

    protected Global(final Display display,
                     final Class<R> resourceClass,
                     final int version) {
        if (version <= 0) {
            throw new IllegalArgumentException("Version must be bigger than 0");
        }

        this.jObjectPointer = Pointer.from(this);

        this.pointer = WaylandServerCore.INSTANCE()
                                        .wl_global_create(display.pointer,
                                                          InterfaceMeta.get(resourceClass)
                                                                       .getNative().address,
                                                          version,
                                                          this.jObjectPointer.address,
                                                          FUNC_T_POINTER.address);
        ObjectCache.store(this.pointer,
                          this);
    }

    public abstract R onBindClient(Client client,
                                   int version,
                                   int id);

    @Override
    public int hashCode() {
        return this.pointer.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Global)) {
            return false;
        }

        final Global global = (Global) o;

        return this.pointer.equals(global.pointer);
    }

    public void destroy() {
        WaylandServerCore.INSTANCE()
                         .wl_global_destroy(this.pointer);
        ObjectCache.remove(this.pointer);
        this.jObjectPointer.close();
    }
}

