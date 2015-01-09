package org.freedesktop.wayland.util.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;

public class wl_argument extends Union {
    /**
     * < signed integer
     */
    public int                  i;
    /**
     * < unsigned integer
     */
    public int                  u;
    /**
     * < fixed point<br>
     * C type : wl_fixed_t
     */
    public int                  f;
    /**
     * < string<br>
     * C type : const char*
     */
    public Pointer              s;
    /**
     * < object<br>
     * C type : wl_object*
     */
    public wl_object            o;
    /**
     * < new_id
     */
    public int                  n;
    /**
     * < array<br>
     * C type : wl_array*
     */
    public wl_array.ByReference a;
    /**
     * < file descriptor
     */
    public int                  h;

    public wl_argument() {
        super();
    }

    /**
     * @param s < string<br>
     *          C type : const char*
     */
    public wl_argument(final Pointer s) {
        super(s);
    }

    protected ByReference newByReference() { return new ByReference(); }

    protected ByValue newByValue() { return new ByValue(); }

    protected wl_argument newInstance() { return new wl_argument(); }

    public static class ByReference extends wl_argument implements Structure.ByReference {

    }

    public static class ByValue extends wl_argument implements Structure.ByValue {

    }
}
