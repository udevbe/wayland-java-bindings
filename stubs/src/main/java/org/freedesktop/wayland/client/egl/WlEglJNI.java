package org.freedesktop.wayland.client.egl;

public class WlEglJNI {

  //Egl
  public static native long createEglWindow(long surfaceProxyPointer,
                                            int width,
                                            int height);

  public static native void destroyEglWindow(long eglWindowPointer);//TODO

  public static native void resize(long eglWindowPointer,
                                   int width,
                                   int height,
                                   int dx,
                                   int dy);//TODO

  public static native void getAttachedSize(long eglWindowPointer,
                                            int[] size);//TODO
}
