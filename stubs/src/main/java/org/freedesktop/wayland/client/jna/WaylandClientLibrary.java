package org.freedesktop.wayland.client.jna;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import org.freedesktop.wayland.util.jna.WaylandUtilLibrary;

public interface WaylandClientLibrary extends WaylandUtilLibrary {

  public static final String JNA_LIBRARY_NAME = "wayland-client";
  public static final NativeLibrary JNA_NATIVE_LIB = NativeLibrary.getInstance(WaylandClientLibrary.JNA_LIBRARY_NAME);
  public static final WaylandClientLibrary INSTANCE = (WaylandClientLibrary) Native.loadLibrary(
      WaylandClientLibrary.JNA_LIBRARY_NAME,
      WaylandClientLibrary.class);
}
