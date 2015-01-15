package org.freedesktop.wayland.server.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.freedesktop.wayland.util.jna.wl_list;

import java.util.Arrays;
import java.util.List;

public class wl_listener extends Structure {
    public wl_list.ByValue  link;
    public wl_notify_func_t notify$;

    public wl_listener() {
        super();
    }

    protected List<?> getFieldOrder() {
        return Arrays.asList("link",
                             "notify$");
    }

    public wl_listener(Pointer peer) {
        super(peer);
    }

    public static class ByReference extends wl_listener implements Structure.ByReference {
        public ByReference() {
        }

        public ByReference(final Pointer peer) {
            super(peer);
        }
    }

    public static class ByValue extends wl_listener implements Structure.ByValue {
        public ByValue() {
        }

        public ByValue(final Pointer peer) {
            super(peer);
        }
    }
}
