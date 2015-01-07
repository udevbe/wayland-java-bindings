package org.freedesktop.wayland.util.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class wl_interface extends Structure {
    /**
     * C type : const char*
     */
    public Pointer name;
    public int     version;
    public int     method_count;
    /**
     * C type : wl_message*
     */
    public Pointer methods;
    public int     event_count;
    /**
     * C type : wl_message*
     */
    public Pointer events;

    public wl_interface() {
        super();
    }

    protected List<?> getFieldOrder() {
        return Arrays.asList("name",
                             "version",
                             "method_count",
                             "methods",
                             "event_count",
                             "events");
    }

    /**
     * @param name    C type : const char*<br>
     * @param methods C type : wl_message*<br>
     * @param events  C type : wl_message*
     */
    public wl_interface(final Pointer name,
                        final int version,
                        final int method_count,
                        final Pointer methods,
                        final int event_count,
                        final Pointer events) {
        super();
        this.name = name;
        this.version = version;
        this.method_count = method_count;
        this.methods = methods;
        this.event_count = event_count;
        this.events = events;
    }

    public wl_interface(final Pointer peer) {
        super(peer);
    }

    protected ByReference newByReference() { return new ByReference(); }

    protected ByValue newByValue() { return new ByValue(); }

    protected wl_interface newInstance() { return new wl_interface(); }

    public static class ByReference extends wl_interface implements Structure.ByReference {
        public ByReference() {
        }

        public ByReference(final Pointer name,
                           final int version,
                           final int method_count,
                           final Pointer methods,
                           final int event_count,
                           final Pointer events) {
            super(name,
                  version,
                  method_count,
                  methods,
                  event_count,
                  events);
        }

        public ByReference(final Pointer peer) {
            super(peer);
        }
    }

    public static class ByValue extends wl_interface implements Structure.ByValue {
        public ByValue() {
        }

        public ByValue(final Pointer name,
                       final int version,
                       final int method_count,
                       final Pointer methods,
                       final int event_count,
                       final Pointer events) {
            super(name,
                  version,
                  method_count,
                  methods,
                  event_count,
                  events);
        }

        public ByValue(final Pointer peer) {
            super(peer);
        }
    }
}
