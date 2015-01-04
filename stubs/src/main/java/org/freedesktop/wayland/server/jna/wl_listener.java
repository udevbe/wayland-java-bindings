package org.freedesktop.wayland.server.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.freedesktop.wayland.util.jna.wl_list;

import java.util.Arrays;
import java.util.List;

public class wl_listener extends Structure {
    public wl_list          link;
    public wl_notify_func_t notify$;

    public wl_listener() {
        super();
    }

    protected List<?> getFieldOrder() {
        return Arrays.asList("link",
                             "notify$");
    }

    public wl_listener(wl_list link,
                       wl_notify_func_t notify$) {
        super();
        this.link = link;
        this.notify$ = notify$;
    }

    public wl_listener(Pointer peer) {
        super(peer);
    }

    protected ByReference newByReference() { return new ByReference(); }

    protected ByValue newByValue() { return new ByValue(); }

    protected wl_listener newInstance() { return new wl_listener(); }

    public static class ByReference extends wl_listener implements Structure.ByReference {
        public ByReference() {
        }

        public ByReference(final wl_list link,
                           final wl_notify_func_t notify$) {
            super(link,
                  notify$);
        }

        public ByReference(final Pointer peer) {
            super(peer);
        }
    }

    public static class ByValue extends wl_listener implements Structure.ByValue {
        public ByValue() {
        }

        public ByValue(final wl_list link,
                       final wl_notify_func_t notify$) {
            super(link,
                  notify$);
        }

        public ByValue(final Pointer peer) {
            super(peer);
        }
    }
}
