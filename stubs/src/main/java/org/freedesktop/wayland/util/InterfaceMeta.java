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

import com.github.zubnix.jaccall.Pointer;
import org.freedesktop.wayland.util.jaccall.wl_interface;
import org.freedesktop.wayland.util.jaccall.wl_message;

import java.util.HashMap;
import java.util.Map;

import static com.github.zubnix.jaccall.Pointer.malloc;
import static com.github.zubnix.jaccall.Size.sizeof;

/**
 * Wrapper class for any Java type to get or create a native wayland interface for use with the native wayland
 * library. To get a native wayland interface for a given Java type, use {@link #get(Class)}.
 */
public class InterfaceMeta {

    public static final  InterfaceMeta                NO_INTERFACE  = new InterfaceMeta(Pointer.wrap(wl_interface.class,
                                                                                                     0L));
    private static final Map<Class<?>, InterfaceMeta> INTERFACE_MAP = new HashMap<Class<?>, InterfaceMeta>();

    public final Pointer<wl_interface> pointer;

    protected InterfaceMeta(final Pointer<wl_interface> pointer) {
        this.pointer = pointer;
        ObjectCache.store(getNative().address,
                          this);
    }

    public Pointer<wl_interface> getNative() {
        return this.pointer;
    }

    /**
     * Scans this type for {@link Interface} annotations and creates a native context if possible.
     *
     * @param type Any Java type.
     *
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

        final int method_count = methods.length;
        final Pointer<wl_message> methodPointer = malloc(method_count * wl_message.SIZE,
                                                         wl_message.class);
        for (int i = 0; i < method_count; i++) {
            MessageMeta.init(methodPointer.offset(i),
                             methods[i]);
        }

        final int event_count = events.length;
        final Pointer<wl_message> eventPointer = malloc(event_count * wl_message.SIZE,
                                                        wl_message.class);
        for (int i = 0; i < event_count; i++) {
            MessageMeta.init(eventPointer.offset(i),
                             events[i]);
        }

        final Pointer<wl_interface> wlInterfacePointer = malloc(wl_interface.SIZE,
                                                                wl_interface.class);
        final Pointer<String> namePointer = malloc(sizeof(name),
                                                   String.class);
        final wl_interface wlInterface = wlInterfacePointer.dref();
        wlInterface.name(namePointer);
        wlInterface.version(version);
        wlInterface.method_count(method_count);
        wlInterface.methods(methodPointer);
        wlInterface.event_count(event_count);

        return InterfaceMeta.get(wlInterfacePointer);
    }

    public static InterfaceMeta get(final Pointer<wl_interface> pointer) {
        InterfaceMeta interfaceMeta = ObjectCache.from(pointer.address);
        if (interfaceMeta == null) {
            interfaceMeta = new InterfaceMeta(pointer);
        }
        return interfaceMeta;
    }

    public String getName() {
        return this.pointer.dref()
                           .name()
                           .dref();
    }

    @Override
    public int hashCode() {
        return getNative().hashCode();
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
}