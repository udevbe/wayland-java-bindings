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
 * Wrapper class for any Java type to get or create a native wayland interface for use with the native wayland
 * library. To get a native wayland interface for a given Java type, use {@link #get(Class)}.
 */
public class InterfaceMeta implements HasPointer {

    public static final  InterfaceMeta                NO_INTERFACE  = new InterfaceMeta();
    private static final Map<Class<?>, InterfaceMeta> INTERFACE_MAP = new HashMap<Class<?>, InterfaceMeta>();

    private final long pointer;

    protected InterfaceMeta(final long pointer) {
        this.pointer = pointer;
        ObjectCache.store(getPointer(),
                          this);
    }

    protected InterfaceMeta() {
        this.pointer = 0;
    }

    /**
     * Scans this type for {@link Interface} annotations and creates a native context if possible.
     *
     * @param type Any Java type.
     * @return The associated {@link InterfaceMeta} or {@link #NO_INTERFACE} if the type does not have a wayland interface
     * associated with it.
     */
    public static InterfaceMeta get(final Class<?> type) {
        InterfaceMeta interfaceMeta = INTERFACE_MAP.get(type);
        if (interfaceMeta == null) {
            final Interface waylandInterface = type.getAnnotation(Interface.class);
            if (waylandInterface == null) {
                interfaceMeta = NO_INTERFACE;
            }
            else {
                interfaceMeta = create(waylandInterface.name(),
                                       waylandInterface.version(),
                                       waylandInterface.methods(),
                                       waylandInterface.events());
            }
            INTERFACE_MAP.put(type,
                              interfaceMeta);
        }
        return interfaceMeta;
    }

    protected static InterfaceMeta create(final String name,
                                          final int version,
                                          final Message[] methods,
                                          final Message[] events) {
        final long methodPointer = WlUtilJNI.allocateMessages(methods.length);
        final long eventPointer  = WlUtilJNI.allocateMessages(events.length);
        for (int i = 0; i < methods.length; i++) {
            MessageMeta.init(methodPointer,
                             i,
                             methods[i]);
            }
        for (int i = 0; i < events.length; i++) {
            MessageMeta.init(eventPointer,
                             i,
                             events[i]);
        }
        return new InterfaceMeta(WlUtilJNI.createInterface(name,
                                                           version,
                                                           methodPointer,
                                                           methods.length,
                                                           eventPointer,
                                                           events.length));
    }

    public long getPointer() {
        return this.pointer;
    }

    public String getName(){
        return WlUtilJNI.getName(getPointer());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final InterfaceMeta that = (InterfaceMeta) o;

        return getPointer() == that.getPointer();
    }

    @Override
    public int hashCode() {
        return (int) getPointer();
    }
}