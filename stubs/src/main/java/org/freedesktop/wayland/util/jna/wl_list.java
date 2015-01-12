package org.freedesktop.wayland.util.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class wl_list extends Structure {
    /**
     * C type : wl_list*
     */
    public transient wl_list.ByReference prev;
    /**
     * C type : wl_list*
     */
    public transient wl_list.ByReference next;

    public wl_list() {
        super();
    }

    protected List<?> getFieldOrder() {
        return Arrays.asList("prev",
                             "next");
    }

    /**
     * @param prev C type : wl_list*<br>
     * @param next C type : wl_list*
     */
    public wl_list(wl_list.ByReference prev,
                   wl_list.ByReference next) {
        super();
        this.prev = prev;
        this.next = next;
    }

    public wl_list(Pointer peer) {
        super(peer);
    }

    protected ByReference newByReference() { return new ByReference(); }

    protected ByValue newByValue() { return new ByValue(); }

    protected wl_list newInstance() { return new wl_list(); }

    public static class ByReference extends wl_list implements Structure.ByReference {

    }

    public static class ByValue extends wl_list implements Structure.ByValue {

    }
}
