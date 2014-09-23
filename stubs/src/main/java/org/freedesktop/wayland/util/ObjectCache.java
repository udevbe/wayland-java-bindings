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

import java.util.HashMap;
import java.util.Map;

/**
 * A cache for POJOs with a native context.
 * It maps POJOs to their native context (a native pointer) so it can consistently return the same POJO given a pointer
 * value.
 */
public class ObjectCache {
    private static final Map<Long, Object> MAPPED_OBJECTS = new HashMap<Long, Object>();

    /**
     * Retrieve a POJO that is mapped to a native pointer. This method should be used to easily retrieve a POJO with a
     * native context. This method will only return a POJO if it was previously cached with a call to {@link #store(long, Object)}
     *
     * @param pointer The pointer of the associated object.
     * @param <T>     The type of the POJO to cast.
     * @return The cached object.
     */
    public static <T> T from(final long pointer) {
        return (T) MAPPED_OBJECTS.get(pointer);
    }

    /**
     * Maps a native pointer to a POJO. This method should be used to easily store a POJO with a native context.
     *
     * @param pointer The pointer of the associated object.
     * @param object  The object to cache.
     */
    public static void store(final long pointer,
                             final Object object) {
        MAPPED_OBJECTS.put(pointer,object);
    }

    /**
     * Remove a mapped POJO. This method should be used when the native context of the POJO is no longer valid.
     *
     * @param pointer The pointer of the associated object.
     * @return True if a pointer was cached, false if not. The value can be used to detected double frees.
     */
    public static boolean remove(final long pointer) {
        return MAPPED_OBJECTS.remove(pointer) != null;
    }
}
