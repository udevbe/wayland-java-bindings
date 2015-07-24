//Copyright 2015 Erik De Rijcke
//
//Licensed under the Apache License,Version2.0(the"License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing,software
//distributed under the License is distributed on an"AS IS"BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
package org.freedesktop.wayland.util;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.freedesktop.wayland.HasNative;
import org.freedesktop.wayland.client.Proxy;
import org.freedesktop.wayland.server.Resource;
import org.freedesktop.wayland.util.jna.wl_array;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class Arguments implements HasNative<Pointer> {

    private static final Map<Integer, Arguments> ARGUMENTS_CACHE = new HashMap<Integer, Arguments>();

    private final Pointer pointer;
    private       boolean valid;

    Arguments(final Pointer pointer) {
        this.pointer = pointer;
        this.valid = true;
    }

    public static Arguments create(final int size) {
        Arguments arguments = ARGUMENTS_CACHE.get(size);
        if (arguments == null) {
            arguments = new Arguments(new Memory(size * Pointer.SIZE));
            ARGUMENTS_CACHE.put(size,
                                arguments);
        }
        return arguments;
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

    public Pointer getO(final int index) {
        return getPointer(index);
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
     *
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
     *
     * @return
     */
    public Arguments set(final int index,
                         final Resource<?> o) {
        setPointer(index,
                   o.getNative());
        return this;
    }

    private void setPointer(final int index,
                            final Pointer pointer) {
        this.pointer.setPointer(index * Pointer.SIZE,
                                pointer);
    }

    /**
     * struct wl_object *o; object
     *
     * @param index
     * @param o
     *
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
     *
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
     *
     * @return
     */
    public Arguments set(final int index,
                         final String s) {
        final Pointer m = new Memory(s.length() + 1);
        m.setString(0,
                    s);
        setPointer(index,
                   m);
        return this;
    }

    /**
     * struct wl_array *a; array
     *
     * @param index
     * @param array
     *
     * @return
     */
    public Arguments set(final int index,
                         final ByteBuffer array) {
        final wl_array.ByReference wlArray = new wl_array.ByReference();
        wlArray.alloc = array.capacity();
        wlArray.size = array.capacity();
        wlArray.data = Native.getDirectBufferPointer(array);
        setPointer(index,
                   wlArray.getPointer());
        return this;
    }

    @Override
    public Pointer getNative() {
        return this.pointer;
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }

    @Override
    public int hashCode() {
        return this.pointer.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Arguments arguments = (Arguments) o;

        return this.pointer.equals(arguments.pointer);
    }

    @Override
    protected void finalize() throws Throwable {
        this.valid = false;
        super.finalize();
    }
}
