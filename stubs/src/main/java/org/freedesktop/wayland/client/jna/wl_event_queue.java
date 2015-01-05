package org.freedesktop.wayland.client.jna;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class wl_event_queue extends PointerType {
  public wl_event_queue(Pointer address) {
    super(address);
  }
  public wl_event_queue() {
    super();
  }
}
