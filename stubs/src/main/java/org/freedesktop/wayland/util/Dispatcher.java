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

import com.github.zubnix.jaccall.JNI;
import com.github.zubnix.jaccall.JObject;
import com.github.zubnix.jaccall.Pointer;
import com.github.zubnix.jaccall.Ptr;
import org.freedesktop.wayland.util.jaccall.wl_argument;
import org.freedesktop.wayland.util.jaccall.wl_array;
import org.freedesktop.wayland.util.jaccall.wl_dispatcher_func_t;
import org.freedesktop.wayland.util.jaccall.wl_message;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.github.zubnix.jaccall.Pointer.wrap;
import static org.freedesktop.wayland.util.jaccall.Pointerwl_dispatcher_func_t.nref;

public final class Dispatcher implements wl_dispatcher_func_t {

    private static final Map<Class<?>, Map<Integer, Method>> METHOD_CACHE      = new HashMap<Class<?>, Map<Integer, Method>>();
    private static final Map<Class<?>, Constructor<?>>       CONSTRUCTOR_CACHE = new HashMap<Class<?>, Constructor<?>>();
    public static final  Pointer<wl_dispatcher_func_t>       INSTANCE          = nref(new Dispatcher());

    Dispatcher() {
    }

    public int $(@Ptr(JObject.class) final long implementation,
                 @Ptr final long wlObject,
                 final int opcode,
                 @Ptr(wl_message.class) final long wlMessage,
                 @Ptr(wl_argument.class) final long wlArguments) {

        Method        method        = null;
        Object[]      jargs         = null;
        Message       message       = null;
        WaylandObject waylandObject = null;

        try {
            message = ObjectCache.<MessageMeta>from(wlMessage)
                                 .getMessage();
            waylandObject = (WaylandObject) wrap(JObject.class,
                                                 implementation).dref()
                                                                .pojo();
            method = get(waylandObject.getClass(),
                         waylandObject.getImplementation()
                                      .getClass(),
                         message);

            final String signature = message.signature();
            //TODO do something with the version signature? Somehow see which version the implementation exposes and
            // check if it matches?
            final String messageSignature;
            if (signature.length() > 0 && Character.isDigit(signature.charAt(0))) {
                messageSignature = signature.substring(1);
            }
            else {
                messageSignature = signature;
            }

            final int nroArgs = message.types().length;
            jargs = new Object[nroArgs + 1];
            jargs[0] = waylandObject;

            if (nroArgs > 0) {
                final Arguments arguments = new Arguments(wrap(wl_argument.class,
                                                               wlArguments));
                boolean optional = false;
                int argIndex = 0;
                for (final char signatureChar : messageSignature.toCharArray()) {
                    if (signatureChar == '?') {
                        optional = true;
                        continue;
                    }
                    final Object jarg = fromArgument(arguments,
                                                     argIndex,
                                                     signatureChar,
                                                     message.types()[argIndex]);
                    if (!optional && jarg == null) {
                        throw new IllegalArgumentException(String.format("Got non optional argument that is null!. "
                                                                         + "Message: %s(%s), violated arg index: %d",
                                                                         message.name(),
                                                                         message.signature(),
                                                                         argIndex));
                    }
                    argIndex++;
                    jargs[argIndex] = jarg;
                    optional = false;
                }
            }
            method.invoke(waylandObject.getImplementation(),
                          jargs);
        }
        catch (final Exception e) {
            System.err.println(String.format("Got an exception, This is most likely a bug.\n"
                                             + "Method=%s\n" +
                                             "implementation=%s\n" +
                                             "arguments=%s\n" +
                                             "message=%s",
                                             method,
                                             waylandObject.getImplementation(),
                                             Arrays.toString(jargs),
                                             message));
            e.printStackTrace();
        }

        return 0;
    }

    private static Method get(final Class<? extends WaylandObject> waylandObjectType,
                              final Class<?> implementationType,
                              final Message message) throws NoSuchMethodException {

        Map<Integer, Method> methodMap = METHOD_CACHE.get(implementationType);
        if (methodMap == null) {
            methodMap = new HashMap<Integer, Method>();
            METHOD_CACHE.put(implementationType,
                             methodMap);
        }

        final int methodHash = Objects.hash(waylandObjectType,
                                            message);
        Method method = methodMap.get(methodHash);
        if (method == null) {
            final Class<?>[] types = message.types();
            final Class<?>[] argTypes = new Class<?>[types.length + 1];
            //copy to new array and shift by 1
            System.arraycopy(types,
                             0,
                             argTypes,
                             1,
                             types.length);
            argTypes[0] = waylandObjectType;
            method = implementationType.getMethod(message.functionName(),
                                                  argTypes);
            method.setAccessible(true);
            methodMap.put(methodHash,
                          method);
        }
        return method;
    }

    private static Object fromArgument(final Arguments arguments,
                                       final int index,
                                       final char type,
                                       final Class<?> targetType) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        switch (type) {
            case 'u': {
                return arguments.getU(index);
            }
            case 'i': {
                return arguments.getI(index);
            }
            case 'f': {
                return arguments.getFixed(index);
            }
            case 'h': {
                return arguments.getH(index);
            }
            case 'o': {
                final Pointer objectPointer = arguments.getO(index);
                final Object waylandObject;
                if (objectPointer.address == 0L) {
                    waylandObject = null;
                }
                else {
                    final Object cachedObject = ObjectCache.from(objectPointer.address);
                    if (cachedObject == null) {
                        waylandObject = reconstruct(objectPointer,
                                                    targetType);
                    }
                    else {
                        waylandObject = cachedObject;
                    }
                }
                return waylandObject;
            }
            case 'n': {
                return arguments.getN(index);
            }
            case 's': {
                return arguments.getS(index);
            }
            case 'a': {
                final wl_array wlArray = arguments.getA(index);
                return JNI.wrap(wlArray.data().address,
                                wlArray.alloc());
            }
            default: {
                throw new IllegalArgumentException("Can not convert wl_argument type: " + type);
            }
        }
    }

    private static Object reconstruct(final Pointer objectPointer,
                                      final Class<?> targetType) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?> constructor = CONSTRUCTOR_CACHE.get(targetType);
        if (constructor == null) {
            //FIXME use static get(Pointer) method instead of proxy or resource
            constructor = targetType.getDeclaredConstructor(Pointer.class);
            constructor.setAccessible(true);
            CONSTRUCTOR_CACHE.put(targetType,
                                  constructor);
        }
        return constructor.newInstance(objectPointer);
    }
}