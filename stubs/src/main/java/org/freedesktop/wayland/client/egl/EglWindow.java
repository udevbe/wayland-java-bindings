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

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import org.freedesktop.wayland.HasNative;
import org.freedesktop.wayland.client.Proxy;
import org.freedesktop.wayland.client.egl.jna.WaylandEglLibrary;
import org.freedesktop.wayland.util.ObjectCache;

public class EglWindow implements HasNative<Pointer> {

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

        @Override
        public int hashCode() {
            int result = this.width;
            result = 31 * result + this.height;
            return result;
        }
    }

    private final Pointer pointer;
    private       boolean valid;

    public static EglWindow create(final Proxy<?> wlSurfaceProxy,
                                   final int width,
                                   final int height) {
        return EglWindow.get(WaylandEglLibrary.INSTANCE.wl_egl_window_create(wlSurfaceProxy.getNative(),
                                                                             width,
                                                                             height));
    }

    public static EglWindow get(final Pointer pointer) {
        EglWindow eglWindow = ObjectCache.from(pointer);
        if (eglWindow == null) {
            eglWindow = new EglWindow(pointer);
        }
        return eglWindow;
    }

    protected EglWindow(final Pointer pointer) {
        this.pointer = pointer;
        this.valid = true;
        ObjectCache.store(getNative(),
                          this);
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }

    public void destroy() {
        if (isValid()) {
            this.valid = false;
            WaylandEglLibrary.INSTANCE.wl_egl_window_destroy(getNative());
            ObjectCache.remove(getNative());
        }
    }

    public void resize(final int width,
                       final int height,
                       final int dx,
                       final int dy) {
        WaylandEglLibrary.INSTANCE.wl_egl_window_resize(getNative(),
                                                        width,
                                                        height,
                                                        dx,
                                                        dy);
    }

    public Size getAttachedSize() {
        final IntByReference x = new IntByReference();
        final IntByReference y = new IntByReference();
        WaylandEglLibrary.INSTANCE.wl_egl_window_get_attached_size(getNative(),
                                                                   x,
                                                                   y);
        return new Size(x.getValue(),
                        y.getValue());
    }

    @Override
    public Pointer getNative() {
        return this.pointer;
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

        return getNative().equals(eglWindow.getNative());
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