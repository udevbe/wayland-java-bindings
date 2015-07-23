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

import com.sun.jna.Pointer;

import java.util.HashMap;
import java.util.Map;

/**
 * A cache for POJOs with a native context.
 * It maps POJOs to their native context (a native pointer) so it can consistently return the same POJO given a pointer
 * value.
 */
public class ObjectCache {
    private static final Map<Pointer, Object> MAPPED_OBJECTS = new HashMap<Pointer, Object>();

    /**
     * Retrieve a POJO that is mapped to a native pointer. This method should be used to easily retrieve a POJO with a
     * native context. This method will only return a POJO if it was previously cached with a call to {@link #store(Pointer, Object)}
     *
     * @param pointer The pointer of the associated object.
     * @param <T>     The type of the POJO to cast.
     *
     * @return The cached object.
     */
    public static <T> T from(final Pointer pointer) {

        return (T) MAPPED_OBJECTS.get(pointer);
    }

    /**
     * Maps a native pointer to a POJO. This method should be used to easily store a POJO with a native context.
     *
     * @param pointer The pointer of the associated object.
     * @param object  The object to cache.
     */
    public static void store(final Pointer pointer,
                             final Object object) {
        //important notice:
        //A bug/missing feature in libwayland is that it does not allow us to fully track all library allocated objects (yet?).
        //As such there is no reliable way to be entirely sure when an object is really destroyed.
        //As a result, there is an exception in the following scenario: if we
        //get a 'pre-destroy' notify of object foo (=removes it from the object cache), and a subsequent call returns
        //foo (=add it to the cache), then  an illegal state exception is thrown, even though it's not really an illegal state!
        //
        //To work around this issue, this check is disabled, and library objects that don't have a real destructor are simply
        //not removed from the cache.

        final Object oldValue = MAPPED_OBJECTS.put(pointer,
                                                   object);
        if (oldValue != null) {
            //put it back!
            MAPPED_OBJECTS.put(pointer,
                               oldValue);
            throw new IllegalStateException(String.format("Can not re-map existing pointer.\n" +
                                                          "Pointer=%s\n" +
                                                          "old value=%s" +
                                                          "\nnew value=%s",
                                                          pointer,
                                                          oldValue,
                                                          object));
        }
    }

    /**
     * Remove a mapped POJO. This method should be used when the native context of the POJO is no longer valid.
     *
     * @param pointer The pointer of the associated object.
     */
    public static <T> T remove(final Pointer pointer) {
        return (T) MAPPED_OBJECTS.remove(pointer);
    }
}
