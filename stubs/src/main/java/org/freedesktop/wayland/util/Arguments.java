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

import org.freedesktop.wayland.client.Proxy;
import org.freedesktop.wayland.server.Resource;

import java.nio.ByteBuffer;

public class Arguments implements HasPointer {

    private final long    pointer;
    private final boolean autoFree;

    Arguments(final long pointer,
              final boolean autoFree) {
        this.pointer = pointer;
        this.autoFree = autoFree;
        //do not cache object, let the java GC finalize the object, which in turn will free the native context.
    }

    public static Arguments create(final int size) {
        return new Arguments(WlUtilJNI.createArguments(size),
                             true);
    }

    public int getI(final int index) {
        return WlUtilJNI.getIArgument(getPointer(),
                                      index);
    }

    public int getU(final int index) {
        return WlUtilJNI.getUArgument(getPointer(),
                                      index);
    }

    public Fixed getFixed(final int index) {
        return new Fixed(WlUtilJNI.getFArgument(getPointer(),
                                                index));
    }

    public String getS(final int index) {
        return WlUtilJNI.getSArgument(getPointer(),
                                      index);
    }

    public long getO(final int index) {
        return WlUtilJNI.getOArgument(getPointer(),
                                      index);
    }

    public int getN(final int index) {
        return WlUtilJNI.getNArgument(getPointer(),
                                      index);
    }

    public byte[] getA(final int index) {
        return WlUtilJNI.getAArgument(getPointer(),
                                      index);
    }

    public int getH(final int index) {
        return WlUtilJNI.getHArgument(getPointer(),
                                      index);
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
        WlUtilJNI.setIUNHArgument(getPointer(),
                                  index,
                                  iunh);
        return this;
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
        WlUtilJNI.setOArgument(getPointer(),
                               index,
                               o.getPointer());
        return this;
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
        WlUtilJNI.setOArgument(getPointer(),
                               index,
                               o.getPointer());
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
        WlUtilJNI.setFArgument(getPointer(),
                               index,
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
        WlUtilJNI.setSArgument(getPointer(),
                               index,
                               s);
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
        if (!array.isDirect()) {
            throw new IllegalArgumentException("array is not a direct bytebuffer.");
        }
        WlUtilJNI.setAArgument(getPointer(),
                               index,
                               array);
        return this;
    }

    @Override
    public long getPointer() {
        return this.pointer;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Arguments argument = (Arguments) o;

        return getPointer() == argument.getPointer();
    }

    @Override
    public int hashCode() {
        return (int) this.pointer;
    }

    @Override
    protected void finalize() throws Throwable {
        if (this.autoFree) {
            WlUtilJNI.destroyArgument(getPointer());
        }
        super.finalize();
    }
}
