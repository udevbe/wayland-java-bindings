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

import org.freedesktop.jaccall.Pointer;
import org.freedesktop.wayland.util.jaccall.wl_argument;
import org.freedesktop.wayland.util.jaccall.wl_array;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import static org.freedesktop.jaccall.Pointer.malloc;
import static org.freedesktop.jaccall.Pointer.nref;
import static org.freedesktop.jaccall.Pointer.ref;
import static org.freedesktop.jaccall.Pointer.wrap;

public class Arguments {

    public final Pointer<wl_argument> pointer;
    private final List<Object> argumentRefs = new LinkedList<>();

    Arguments(final Pointer<wl_argument> pointer) {
        this.pointer = pointer;
    }

    public static Arguments create(final int size) {
        return new Arguments(malloc(size * wl_argument.SIZE,
                                    wl_argument.class));
    }

    public int getI(final int index) {
        return getInt(index);
    }

    private int getInt(final int index) {
        return this.pointer.get(index)
                           .getI();
    }

    public int getU(final int index) {
        return this.pointer.get(index)
                           .getU();
    }

    public Fixed getFixed(final int index) {
        return new Fixed(this.pointer.get(index)
                                     .getF());
    }

    public String getS(final int index) {
        return this.pointer.get(index)
                           .getS()
                           .get();
    }

    public Pointer getO(final int index) {
        return this.pointer.get(index)
                           .getO();
    }

    public int getN(final int index) {
        return getInt(index);
    }

    public wl_array getA(final int index) {
        return this.pointer.get(index)
                           .getA()
                           .get();
    }

    public int getH(final int index) {
        return this.pointer.get(index)
                           .getH();
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
        this.pointer.get(index)
                    .setI(iunh);
        return this;
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
                         final WaylandObject o) {
        this.argumentRefs.add(o);
        this.pointer.get(index)
                    .setO(wrap(o.getPointer()));
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
        this.argumentRefs.add(f);
        this.pointer.get(index)
                    .setF(f.getRaw());
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
        final Pointer<String> stringPointer = nref(s);
        this.argumentRefs.add(stringPointer);
        this.pointer.get(index)
                    .setS(stringPointer);
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
        final wl_array wlArray = new wl_array();

        wlArray.setData(wrap(array));
        wlArray.setAlloc(array.capacity());
        wlArray.setSize(array.capacity());

        this.argumentRefs.add(wlArray);
        this.pointer.get(index)
                    .setA(ref(wlArray));
        return this;
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
}
