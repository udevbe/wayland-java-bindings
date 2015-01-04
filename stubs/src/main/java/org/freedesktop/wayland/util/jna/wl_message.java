package org.freedesktop.wayland.util.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class wl_message extends Structure {
    /**
     * C type : const char*
     */
    public Pointer                                name;
    /**
     * C type : const char*
     */
    public Pointer                                signature;
    /**
     * C type : wl_interface**
     */
    public  wl_interface.ByReference[] types;

    public wl_message() {
        super();
    }

    protected List<?> getFieldOrder() {
        return Arrays.asList("name",
                             "signature",
                             "types");
    }

    /**
     * @param name      C type : const char*<br>
     * @param signature C type : const char*<br>
     * @param types     C type : wl_interface**
     */
    public wl_message(Pointer name,
                      Pointer signature,
                      wl_interface.ByReference types[]) {
        super();
        this.name = name;
        this.signature = signature;
        if ((types.length != this.types.length)) {
            throw new IllegalArgumentException("Wrong array size !");
        }
        this.types = types;
    }

    public wl_message(Pointer peer) {
        super(peer);
    }

    protected ByReference newByReference() { return new ByReference(); }

    protected ByValue newByValue() { return new ByValue(); }

    protected wl_message newInstance() { return new wl_message(); }

    public static class ByReference extends wl_message implements Structure.ByReference {
        public ByReference() {
        }

        public ByReference(final Pointer name,
                           final Pointer signature,
                           final wl_interface.ByReference[] types) {
            super(name,
                  signature,
                  types);
        }

        public ByReference(final Pointer peer) {
            super(peer);
        }
    }

    public static class ByValue extends wl_message implements Structure.ByValue {
        public ByValue() {
        }

        public ByValue(final Pointer name,
                       final Pointer signature,
                       final wl_interface.ByReference[] types) {
            super(name,
                  signature,
                  types);
        }

        public ByValue(final Pointer peer) {
            super(peer);
        }
    }
}
