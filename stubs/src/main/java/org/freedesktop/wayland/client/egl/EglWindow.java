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
package org.freedesktop.wayland.client.egl;

import org.freedesktop.jaccall.Pointer;
import org.freedesktop.wayland.client.Proxy;
import org.freedesktop.wayland.client.egl.jaccall.WaylandEglCore;
import org.freedesktop.wayland.util.ObjectCache;

import static org.freedesktop.jaccall.Pointer.nref;

public class EglWindow {

    public final long pointer;

    protected EglWindow(final long pointer) {
        this.pointer = pointer;
        ObjectCache.store(this.pointer,
                          this);
    }

    public static EglWindow create(final Proxy<?> wlSurfaceProxy,
                                   final int width,
                                   final int height) {
        return EglWindow.get(WaylandEglCore.INSTANCE()
                                           .wl_egl_window_create(wlSurfaceProxy.pointer,
                                                                 width,
                                                                 height));
    }

    public static EglWindow get(final long pointer) {
        EglWindow eglWindow = ObjectCache.from(pointer);
        if (eglWindow == null) {
            eglWindow = new EglWindow(pointer);
        }
        return eglWindow;
    }

    public void resize(final int width,
                       final int height,
                       final int dx,
                       final int dy) {
        WaylandEglCore.INSTANCE()
                      .wl_egl_window_resize(this.pointer,
                                            width,
                                            height,
                                            dx,
                                            dy);
    }

    public Size getAttachedSize() {
        final Pointer<Integer> x = nref(0);
        final Pointer<Integer> y = nref(0);

        WaylandEglCore.INSTANCE()
                      .wl_egl_window_get_attached_size(this.pointer,
                                                       x.address,
                                                       y.address);
        return new Size(x.dref(),
                        y.dref());
    }

    public static final class Size {

        private final int width;
        private final int height;

        Size(final int width,
             final int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        @Override
        public int hashCode() {
            int result = this.width;
            result = 31 * result + this.height;
            return result;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final Size size = (Size) o;

            return this.height == size.height && this.width == size.width;
        }


    }

    public void destroy() {
        WaylandEglCore.INSTANCE()
                      .wl_egl_window_destroy(this.pointer);
        ObjectCache.remove(this.pointer);
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EglWindow)) {
            return false;
        }

        final EglWindow eglWindow = (EglWindow) o;

        return this.pointer == eglWindow.pointer;
    }

    @Override
    public int hashCode() {
        return new Long(this.pointer).hashCode();
    }
}