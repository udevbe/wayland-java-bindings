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
package org.freedesktop.wayland.client;

import com.sun.jna.Pointer;
import org.freedesktop.wayland.client.jna.WaylandClientLibrary;
import org.freedesktop.wayland.util.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a protocol object on the client side.
 * <p/>
 * A {@code Proxy} acts as a client side proxy to an object existing in the
 * compositor. The proxy is responsible for converting requests made by the
 * clients with {@link #marshal(int)} into Wayland's wire format. Events
 * coming from the compositor are also handled by the proxy, which will in
 * turn call the implementation.
 * <p/>
 * With the exception of function {@link #setQueue(EventQueue)}, functions
 * accessing a {@code Proxy} are not normally used by client code. Clients
 * should normally use the higher level interface generated by the scanner to
 * interact with compositor objects.
 * <p/>
 *
 * @param <I> Implementation type that will act as the listener for received events.
 */
public abstract class Proxy<I> implements WaylandObject {

    private static final Map<Class<? extends Proxy<?>>, Constructor<? extends Proxy<?>>> PROXY_CONSTRUCTORS = new HashMap<Class<? extends Proxy<?>>, Constructor<? extends Proxy<?>>>();

    private final Pointer pointer;
    private final int     version;
    private final I       implementation;

    private       boolean    valid;

    /**
     * @param pointer
     * @param implementation The listener to be added to proxy
     * @param version
     */
    protected Proxy(final Pointer pointer,
                    final I implementation,
                    final int version) {
        this.pointer = pointer;
        this.valid = true;
        this.implementation = implementation;
        this.version = version;
        ObjectCache.store(getNative(),
                          this);
        //Special casing implementation. For some proxies the underlying native library provides its own implementation.
        //We pass in a null implementation in those cases. (Eg Display proxy).
        if (implementation != null) {
            WaylandClientLibrary.INSTANCE()
                                .wl_proxy_add_dispatcher(getNative(),
                                                         Dispatcher.INSTANCE,
                                                         Pointer.NULL,
                                                         Pointer.NULL);
        }
    }

    protected Proxy(final Pointer pointer) {
        this(pointer,
             null,
             99);
    }

    //TODO add get(Pointer) method for each generated proxy

    public int getVersion() {
        return this.version;
    }

    //called from generated proxies

    /**
     * Prepare a request to be sent to the compositor
     * <p/>
     * This function is similar to {@link #marshalConstructor(int, Object, int, Class, Arguments)}, except
     * it doesn't create proxies for new-id arguments.
     * <p/>
     * This should not normally be used by non-generated code.
     *
     * @param opcode Opcode of the request to be sent
     * @param args   Extra arguments for the given request
     */
    protected void marshal(final int opcode,
                           final Arguments args) {
        marshal(opcode,
                args.getNative());
    }

    //called from generated proxies

    /**
     * @param opcode Opcode of the request to be sent
     *
     * @see {@link #marshal(int, Arguments)}
     */
    protected void marshal(final int opcode) {
        WaylandClientLibrary.INSTANCE()
                            .wl_proxy_marshal_array(getNative(),
                                                    opcode,
                                                    Pointer.NULL);
    }

    //called from generated proxies
    protected void marshal(final int opcode,
                           final Pointer argsPointer) {
        WaylandClientLibrary.INSTANCE()
                            .wl_proxy_marshal_array(getNative(),
                                                    opcode,
                                                    argsPointer);
    }

    //called from generated proxies

    /**
     * Prepare a request to be sent to the compositor
     * <p/>
     * Translates the request given by opcode and the extra arguments into the
     * wire format and write it to the connection buffer.
     * <p/>
     * For new-id arguments, this function will allocate a new {@code Proxy}
     * and send the ID to the server.  The new {@code Proxy} will be returned
     * on success or NULL on errror with errno set accordingly.
     * <p/>
     * This is intended to be used by language bindings and not in
     * non-generated code.
     *
     * @param opcode         Opcode of the request to be sent
     * @param implementation The listener to use for the new proxy
     * @param version        The runtime version of the new proxy
     * @param newProxyCls    The type to use for the new proxy
     * @param args           Extra arguments for the given request
     * @param <J>            implementation Type
     * @param <T>            proxy Type
     *
     * @return a new proxy
     */
    protected <J, T extends Proxy<J>> T marshalConstructor(final int opcode,
                                                           final J implementation,
                                                           final int version,
                                                           final Class<T> newProxyCls,
                                                           final Arguments args) {
        return marshalConstructor(opcode,
                                  implementation,
                                  version,
                                  newProxyCls,
                                  args.getNative());
    }

    //called from generated proxies
    protected <J, T extends Proxy<J>> T marshalConstructor(final int opcode,
                                                           final J implementation,
                                                           final int version,
                                                           final Class<T> newProxyCls,
                                                           final Pointer argsPointer) {
        try {
            final Pointer
                    wlProxy =
                    WaylandClientLibrary.INSTANCE()
                                        .wl_proxy_marshal_array_constructor(getNative(),
                                                                            opcode,
                                                                            argsPointer,
                                                                            InterfaceMeta.get(newProxyCls)
                                                                                         .getNative()
                                                                                         .getPointer());
            return marshalProxy(wlProxy,
                                implementation,
                                version,
                                newProxyCls);
        }
        catch (final NoSuchMethodException e) {
            throw new RuntimeException("Uh oh, this is a bug!",
                                       e);
        }
        catch (final IllegalAccessException e) {
            throw new RuntimeException("Uh oh, this is a bug!",
                                       e);
        }
        catch (final InvocationTargetException e) {
            throw new RuntimeException("Uh oh, this is a bug!",
                                       e);
        }
        catch (final InstantiationException e) {
            throw new RuntimeException("Uh oh, this is a bug!",
                                       e);
        }
    }

    protected <J, T extends Proxy<J>> T marshalProxy(final Pointer pointer,
                                                     final J implementation,
                                                     final int version,
                                                     final Class<T> newProxyCls) throws NoSuchMethodException,
                                                                                        IllegalAccessException,
                                                                                        InvocationTargetException,
                                                                                        InstantiationException {
        Constructor<? extends Proxy<?>> proxyConstructor = PROXY_CONSTRUCTORS.get(newProxyCls);
        if (proxyConstructor == null) {
            proxyConstructor = findMatchingConstructor(newProxyCls,
                                                       Pointer.class,
                                                       implementation.getClass(),
                                                       int.class);
            PROXY_CONSTRUCTORS.put(newProxyCls,
                                   proxyConstructor);
        }
        return (T) proxyConstructor.newInstance(pointer,
                                                implementation,
                                                version);
    }

    protected <J, T extends Proxy<J>> Constructor<T> findMatchingConstructor(final Class<T> newProxyCls,
                                                                             final Class<?> pointerClass,
                                                                             final Class<?> implementationClass,
                                                                             final Class<?> intClass) throws NoSuchMethodException {
        for (final Constructor<?> constructor : newProxyCls.getConstructors()) {
            final Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length != 3) {
                continue;
            }
            if (parameterTypes[0].isAssignableFrom(pointerClass) &&
                parameterTypes[1].isAssignableFrom(implementationClass) &&
                parameterTypes[2].isAssignableFrom(intClass)) {
                return (Constructor<T>) constructor;
            }
        }
        throw new NoSuchMethodException();
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }

    /**
     * Destroy a proxy object
     */
    public void destroy() {
        if (isValid()) {
            this.valid = false;
            WaylandClientLibrary.INSTANCE()
                                .wl_proxy_destroy(getNative());
            ObjectCache.remove(getNative());
        }
    }

    /**
     * Get a proxy's listener
     * <p/>
     * Gets the proxy's listener; which is the implementation set when this proxy was constructed.
     * <p/>
     * This function is useful in client with multiple listeners on the same
     * interface to allow the identification of which code to execute.
     *
     * @return The proxy's listener or NULL if no listener is set
     */
    public I getImplementation() {
        return this.implementation;
    }

    /**
     * Get the id of a proxy object
     *
     * @return The id the object associated with the proxy
     */
    public int getId() {
        return WaylandClientLibrary.INSTANCE()
                                   .wl_proxy_get_id(getNative());
    }

    /**
     * Assign a proxy to an event queue
     * <p/>
     * Assign proxy to event queue. Events coming from {@code proxy} will be
     * queued in {@code queue} instead of the display's main queue.
     *
     * @param queue The event queue that will handle this proxy
     *
     * @see Display#dispatchQueue(EventQueue)
     */
    public void setQueue(final EventQueue queue) {
        WaylandClientLibrary.INSTANCE()
                            .wl_proxy_set_queue(getNative(),
                                                queue.getNative());
    }

    public Pointer getNative() {
        return this.pointer;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Proxy)) {
            return false;
        }

        final Proxy proxy = (Proxy) o;

        return getNative().equals(proxy.getNative());
    }

    @Override
    public int hashCode() {
        return getNative().hashCode();
    }

    @Override
    protected void finalize() throws Throwable {
        destroy();
        super.finalize();
    }
}