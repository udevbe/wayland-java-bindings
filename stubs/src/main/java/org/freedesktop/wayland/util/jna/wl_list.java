package org.freedesktop.wayland.util.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class wl_list extends Structure {
    /**
     * C type : wl_list*
     */
    public volatile Pointer prev;
    /**
     * C type : wl_list*
     */
    public volatile Pointer next;

    public wl_list() {
        super();
    }

    protected List<?> getFieldOrder() {
        return Arrays.asList("prev",
                             "next");
    }

    public wl_list(Pointer peer) {
        super(peer);
    }

    public static class ByReference extends wl_list implements Structure.ByReference {

        public ByReference() {
        }

        public ByReference(final Pointer peer) {
            super(peer);
        }
    }

    public static class ByValue extends wl_list implements Structure.ByValue {

        public ByValue() {
        }

        public ByValue(final Pointer peer) {
            super(peer);
        }
    }
}
