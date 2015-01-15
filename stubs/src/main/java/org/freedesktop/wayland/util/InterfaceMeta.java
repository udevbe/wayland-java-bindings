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
import com.sun.jna.Pointer;
import org.freedesktop.wayland.HasNative;
import org.freedesktop.wayland.util.jna.wl_interface;
import org.freedesktop.wayland.util.jna.wl_message;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper class for any Java type to get or create a native wayland interface for use with the native wayland
 * library. To get a native wayland interface for a given Java type, use {@link #get(Class)}.
 */
public class InterfaceMeta implements HasNative<wl_interface> {

    public static final  InterfaceMeta                NO_INTERFACE  = new InterfaceMeta(new wl_interface(Pointer.NULL));
    private static final Map<Class<?>, InterfaceMeta> INTERFACE_MAP = new HashMap<Class<?>, InterfaceMeta>();

    private final wl_interface pointer;

    private boolean valid;

    protected InterfaceMeta(final wl_interface pointer) {
        this.pointer = pointer;
        ObjectCache.store(getNative().getPointer(),
                          this);
    }

    public static InterfaceMeta get(wl_interface pointer){
        InterfaceMeta interfaceMeta = ObjectCache.from(pointer.getPointer());
        if(interfaceMeta == null){
            interfaceMeta= new InterfaceMeta(pointer);
        }
        return interfaceMeta;
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
        final wl_message.ByReference[] methodPointer;
        if (methods.length > 0) {
            methodPointer = (wl_message.ByReference[]) new wl_message.ByReference().toArray(methods.length);
            for (int i = 0; i < methods.length; i++) {
                MessageMeta.init(methodPointer[i],
                                 methods[i]);
            }
        }
        else {
            methodPointer = new wl_message.ByReference[]{new wl_message.ByReference(Pointer.NULL)};
        }

        final wl_message.ByReference[] eventPointer;
        if (events.length > 0) {
            eventPointer = (wl_message.ByReference[]) new wl_message.ByReference().toArray(events.length);
            for (int i = 0; i < events.length; i++) {
                MessageMeta.init(eventPointer[i],
                                 events[i]);
            }
        }
        else {
            eventPointer = new wl_message.ByReference[]{new wl_message.ByReference(Pointer.NULL)};
        }

        final wl_interface interfacePointer = new wl_interface();
        //set name
        final Pointer m = new Memory(name.length() + 1);
        m.setString(0,
                    name);
        interfacePointer.writeField("name",
                                    m);
        //set version
        interfacePointer.writeField("version",
                                    version);
        //set methods
        interfacePointer.writeField("methods",
                                    methodPointer[0]);
        interfacePointer.writeField("method_count",
                                    methods.length);
        //set events
        interfacePointer.writeField("events",
                                    eventPointer[0]);
        interfacePointer.writeField("event_count",
                                    events.length);

        return InterfaceMeta.get(interfacePointer);
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }

    public wl_interface getNative() {
        return this.pointer;
    }

    public String getName() {
        return this.pointer.name.getString(0);
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

        return getNative().equals(that.getNative());
    }

    @Override
    public int hashCode() {
        return getNative().hashCode();
    }

    @Override
    protected void finalize() throws Throwable {
        this.valid = false;
        super.finalize();
    }
}