package org.freedesktop.wayland.server.jna;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import org.freedesktop.wayland.util.jna.wl_list;
import org.freedesktop.wayland.util.jna.wl_object;

import java.util.Arrays;
import java.util.List;

public class wl_resource extends PointerType {

  public wl_resource() {
  }

  public wl_resource(final Pointer p) {
    super(p);
  }
}
