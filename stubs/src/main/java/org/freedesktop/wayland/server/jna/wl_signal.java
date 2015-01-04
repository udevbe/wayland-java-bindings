package org.freedesktop.wayland.server.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.freedesktop.wayland.util.jna.wl_list;

import java.util.Arrays;
import java.util.List;

public class wl_signal extends Structure {
    public wl_list listener_list;

    public wl_signal() {
        super();
    }

    protected List<?> getFieldOrder() {
        return Arrays.asList("listener_list");
    }

    public wl_signal(wl_list listener_list) {
        super();
        this.listener_list = listener_list;
    }

    public wl_signal(Pointer peer) {
        super(peer);
    }

    protected ByReference newByReference() { return new ByReference(); }

    protected ByValue newByValue() { return new ByValue(); }

    protected wl_signal newInstance() { return new wl_signal(); }

    public static class ByReference extends wl_signal implements Structure.ByReference {
        public ByReference() {
        }

        public ByReference(final wl_list listener_list) {
            super(listener_list);
        }

        public ByReference(final Pointer peer) {
            super(peer);
        }
    }

    public static class ByValue extends wl_signal implements Structure.ByValue {
        public ByValue() {
        }

        public ByValue(final wl_list listener_list) {
            super(listener_list);
        }

        public ByValue(final Pointer peer) {
            super(peer);
        }
    }
}
