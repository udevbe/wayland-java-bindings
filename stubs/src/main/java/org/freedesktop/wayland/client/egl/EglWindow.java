package org.freedesktop.wayland.client.egl;

import com.sun.jna.ptr.IntByReference;
import org.freedesktop.wayland.HasNative;
import org.freedesktop.wayland.client.Proxy;
import org.freedesktop.wayland.client.egl.jna.WaylandEglLibrary;
import org.freedesktop.wayland.util.ObjectCache;

public class EglWindow implements HasNative<Long> {

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

    private final long pointer;

    public static EglWindow create(final Proxy<?> wlSurfaceProxy,
                                   final int width,
                                   final int height) {
        return new EglWindow(WaylandEglLibrary.INSTANCE.wl_egl_window_create(wlSurfaceProxy.getNative(),
                                                                             width,
                                                                             height));
    }

    protected EglWindow(final long pointer) {
        this.pointer = pointer;
        ObjectCache.store(getNative(),
                          this);
    }

    public void destroy() {
        WaylandEglLibrary.INSTANCE.wl_egl_window_destroy(getNative());
        ObjectCache.remove(getNative());
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
    public Long getNative() {
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
}