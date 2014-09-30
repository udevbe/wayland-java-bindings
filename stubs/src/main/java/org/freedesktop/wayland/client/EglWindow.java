package org.freedesktop.wayland.client;

import org.freedesktop.wayland.util.HasPointer;
import org.freedesktop.wayland.util.ObjectCache;

public class EglWindow implements HasPointer{

  public static final class Size{

    private final int width;
    private final int height;

    Size(final int width,
         final int height) {
      this.width = width;
      this.height = height;
    }

    public int getWidth() {
      return width;
    }

    public int getHeight() {
      return height;
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

      return height == size.height && width == size.width;
    }

    @Override
    public int hashCode() {
      int result = width;
      result = 31 * result + height;
      return result;
    }
  }

  private long pointer;

  public static EglWindow create(Proxy<?> wlSurfaceProxy,
                                 int width,
                                 int height){
    return new EglWindow(WlClientJNI.createEglWindow(wlSurfaceProxy.getPointer(),
                                                     width,
                                                     height));
  }

  protected EglWindow(final long pointer) {
    this.pointer = pointer;
    ObjectCache.store(getPointer(),
                      this);
  }

  public void destroy(){
    WlClientJNI.destroyEglWindow(getPointer());
    ObjectCache.remove(getPointer());
  }

  public void resize(int width,
                     int height,
                     int dx,
                     int dy){
    WlClientJNI.resize(getPointer(),
                       width,
                       height,
                       dx,
                       dy);
  }

  public Size getAttachedSize(){
    final int[] size = new int[2];
    WlClientJNI.getAttachedSize(getPointer(),
                                size);
    return new Size(size[0],
                    size[1]);
  }

  @Override
  public long getPointer() {
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

    return pointer == eglWindow.pointer;
  }

  @Override
  public int hashCode() {
    return (int) pointer;
  }
}