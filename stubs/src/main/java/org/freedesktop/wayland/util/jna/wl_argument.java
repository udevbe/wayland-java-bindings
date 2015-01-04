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
     * @param o < object<br>
     *          C type : wl_object*
     */
    public wl_argument(final wl_object o) {
        super();
        this.o = o;
        setType(wl_object.class);
    }

    /**
     * @param s < string<br>
     *          C type : const char*
     */
    public wl_argument(final Pointer s) {
        super();
        this.s = s;
        setType(Pointer.class);
    }

    /**
     * @param a < array<br>
     *          C type : wl_array*
     */
    public wl_argument(final wl_array.ByReference a) {
        super();
        this.a = a;
        setType(wl_array.ByReference.class);
    }

    /**
     * @param i_or_u_or_f_or_n_or_h < signed integer, or < unsigned integer, or < fixed point<br>
     *                              C type : wl_fixed_t, or < new_id, or < file descriptor
     */
    public wl_argument(final int i_or_u_or_f_or_n_or_h) {
        super();
        this.h = this.n = this.f = this.u = this.i = i_or_u_or_f_or_n_or_h;
        setType(Integer.TYPE);
    }

    protected ByReference newByReference() { return new ByReference(); }

    protected ByValue newByValue() { return new ByValue(); }

    protected wl_argument newInstance() { return new wl_argument(); }

    public static class ByReference extends wl_argument implements Structure.ByReference {

    }

    public static class ByValue extends wl_argument implements Structure.ByValue {

    }
}
