/*
 * Copyright © 2014 Erik De Rijcke
 *
 * Permission to use, copy, modify, distribute, and sell this software and its
 * documentation for any purpose is hereby granted without fee, provided that
 * the above copyright notice appear in all copies and that both that copyright
 * notice and this permission notice appear in supporting documentation, and
 * that the name of the copyright holders not be used in advertising or
 * publicity pertaining to distribution of the software without specific,
 * written prior permission.  The copyright holders make no representations
 * about the suitability of this software for any purpose.  It is provided "as
 * is" without express or implied warranty.
 *
 * THE COPYRIGHT HOLDERS DISCLAIM ALL WARRANTIES WITH REGARD TO THIS SOFTWARE,
 * INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS, IN NO
 * EVENT SHALL THE COPYRIGHT HOLDERS BE LIABLE FOR ANY SPECIAL, INDIRECT OR
 * CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE,
 * DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 * TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE
 * OF THIS SOFTWARE.
 */
package org.freedesktop.wayland.util;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.freedesktop.wayland.HasNative;
import org.freedesktop.wayland.client.Proxy;
import org.freedesktop.wayland.server.Resource;
import org.freedesktop.wayland.util.jna.wl_array;

import java.nio.ByteBuffer;

public class Arguments implements HasNative<Long> {

    private final Memory pointer;

    Arguments(final Memory pointer) {
        this.pointer = pointer;
    }

    public static Arguments create(final int size) {
        if (size < 1) {
            throw new IllegalArgumentException("Arguments size must be greater than 0");
        }
        final Memory memory = new Memory(size * Pointer.SIZE);
        memory.clear();
        return new Arguments(memory);
    }

    public int getI(final int index) {
        return getInt(index);
    }

    private int getInt(final int index) {
        return this.pointer.getInt(index * Pointer.SIZE);
    }

    public int getU(final int index) {
        return getInt(index);
    }

    public Fixed getFixed(final int index) {
        return new Fixed(getInt(index));
    }

    public String getS(final int index) {
        return getPointer(index).getString(0);
    }

    private Pointer getPointer(final int index) {
        return this.pointer.getPointer(index * Pointer.SIZE);
    }

    public long getO(final int index) {
        return Pointer.nativeValue(getPointer(index));
    }

    public int getN(final int index) {
        return getInt(index);
    }

    public wl_array getA(final int index) {
        return new wl_array(getPointer(index));
    }

    public int getH(final int index) {
        return getInt(index);
    }

    /**
     * int32_t i;  signed integer
     * uint32_t u; unsigned integer
     * uint32_t n; new_id
     * int32_t h; file descriptor
     *
     * @param index
     * @param iunh
     * @return
     */
    public Arguments set(final int index,
                         final int iunh) {
        setInt(index,
               iunh);
        return this;
    }

    private void setInt(final int index,
                        final int integer) {
        this.pointer.setInt(index * Pointer.SIZE,
                            integer);
    }


    /**
     * struct wl_object *o; object
     *
     * @param index
     * @param o
     * @return
     */
    public Arguments set(final int index,
                         final Resource<?> o) {
        setPointer(index,
                   o.getNative());
        return this;
    }

    private void setPointer(final int index,
                            final long pointer) {
        this.pointer.setPointer(index * Pointer.SIZE,
                                new Pointer(pointer));
    }

    /**
     * struct wl_object *o; object
     *
     * @param index
     * @param o
     * @return
     */
    public Arguments set(final int index,
                         final Proxy<?> o) {
        setPointer(index,
                   o.getNative());
        return this;
    }

    /**
     * wl_fixed_t f; fixed point
     *
     * @param index
     * @param f
     * @return
     */
    public Arguments set(final int index,
                         final Fixed f) {
        setInt(index,
               f.getRaw());
        return this;
    }

    /**
     * const char *s; string
     *
     * @param index
     * @param s
     * @return
     */
    public Arguments set(final int index,
                         final String s) {
        final Pointer m = new Memory(s.length() + 1);
        m.setString(0,
                    s);
        setPointer(index,
                   Pointer.nativeValue(m));
        return this;
    }

    /**
     * struct wl_array *a; array
     *
     * @param index
     * @param array
     * @return
     */
    public Arguments set(final int index,
                         final ByteBuffer array) {
        final wl_array.ByReference wlArray = new wl_array.ByReference();
        wlArray.alloc = array.capacity();
        wlArray.size = array.capacity();
        wlArray.data = Native.getDirectBufferPointer(array);
        setPointer(index,
                   Pointer.nativeValue(wlArray.getPointer()));
        return this;
    }

    @Override
    public Long getNative() {
        return Pointer.nativeValue(this.pointer);
    }
}
