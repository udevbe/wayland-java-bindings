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
import org.freedesktop.wayland.HasNative;
import org.freedesktop.wayland.server.jaccall.wl_global_bind_func_t;

import static com.github.zubnix.jaccall.Pointer.wrap;

public abstract class Global<R extends Resource<?>> implements HasNative<Pointer<?>> {
    //keep reference to avoid being garbage collected
    private final wl_global_bind_func_t nativeCallback = new wl_global_bind_func_t() {
        @Override
        public void $(@Ptr final long client,
                      @Ptr(void.class) final long data,
                      @Unsigned final int version,
                      @Unsigned final int id) {
            onBindClient(Client.get(wrap(client)),
                         version,
                         id);
        }
    };

    private final Pointer pointer;

    private boolean valid;

    protected Global(final Display display,
                     final Class<R> resourceClass,
                     final int version) {
        if (version <= 0) {
            throw new IllegalArgumentException("Version must be bigger than 0");
        }

        this.pointer = WaylandServerLibrary.INSTANCE()
                                           .wl_global_create(display.getNative(),
                                                             InterfaceMeta.get(resourceClass)
                                                                          .getNative(),
                                                             version,
                                                             Pointer.NULL,
                                                             this.nativeCallback);
        this.valid = true;
        ObjectCache.store(getNative(),
                          this);
    }

    public Pointer getNative() {
        return this.pointer;
    }

    //called from jni
    protected void bindClient(final long clientPointer,
                              final int version,
                              final int id) {

        //TODO add some extra checks?
    }

    public abstract R onBindClient(Client client,
                                   int version,
                                   int id);

    @Override
    public int hashCode() {
        return getNative().hashCode();
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

        return getNative().equals(global.getNative());
    }

    @Override
    protected void finalize() throws Throwable {
        destroy();
        super.finalize();
    }

    public void destroy() {
        if (isValid()) {
            this.valid = false;
            ObjectCache.remove(getNative());
            WaylandServerLibrary.INSTANCE()
                                .wl_global_destroy(getNative());
        }
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }
}

