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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public final class Dispatcher {
    private static final Map<Class<?>, Map<Message, Method>> METHOD_CACHE = new HashMap<Class<?>, Map<Message, Method>>();
    private static final Map<Class<?>,Constructor<?>> CONSTRUCTOR_CACHE = new HashMap<Class<?>, Constructor<?>>();

    //called from jni
    public static void dispatch(final Object implementation,
                                final long   objectPointer,
                                final int    opcode,
                                final long   messagePointer,
                                final long   argumentsPointer) {
        final WaylandObject waylandObject = ObjectCache.from(objectPointer);
        if (waylandObject == null) {
            //special case, the resource was created by a native library.
            //TODO Try to reconstruct a proxy or resource java object based on pointer.
            throw new UnsupportedOperationException("Got a dispatch call from an unknown wayland object!");
        }
        Method method = null;
        Object[] jargs = null;
        Message message = null;
        try {
            message = ObjectCache.<MessageMeta>from(messagePointer)
                    .getMessage();
            method = get(waylandObject.getClass(),
                    implementation.getClass(),
                    message);

            final String signature = message.signature();
            //TODO do something with the version signature? Somehow see which version the implementation exposes and
            // check if it matches?
            final String messageSignature;
            if(signature.length() > 0 && Character.isDigit(signature.charAt(0))) {
                messageSignature = signature.substring(1);
            }else{
                messageSignature = signature;
            }

            jargs = new Object[message.types().length + 1];
            jargs[0] = waylandObject;
            final Arguments arguments = new Arguments(argumentsPointer,
                    false);

            boolean optional = false;
            int argIndex = 0;
            for (char signatureChar: messageSignature.toCharArray()) {
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
            method.invoke(implementation,
                    jargs);
        } catch (final Exception e) {
            System.err.println(String.format("Got an exception, This is most likely a bug.\n"
                            + "Method=%s, " +
                            "implementation=%s, " +
                            "arguments=%s, " +
                            "message=%s",
                    method,
                    implementation,
                    Arrays.toString(jargs), message));
            e.printStackTrace();
        }
    }

    private static Method get(final Class<? extends WaylandObject> waylandObjectType,
                              final Class<?> implementationType,
                              final Message message) throws NoSuchMethodException {
        Map<Message, Method> methodMap = METHOD_CACHE.get(implementationType);
        if (methodMap == null) {
            methodMap = new HashMap<Message, Method>();
            METHOD_CACHE.put(implementationType,
                             methodMap);
        }
        Method method = methodMap.get(message);
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
            methodMap.put(message,
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
                final long objectPointer = arguments.getO(index);
                final Object waylandObject;
                if(objectPointer == 0){
                    waylandObject = null;
                }else{
                    final Object cachedObject = ObjectCache.from(objectPointer);
                    if(cachedObject == null){
                        waylandObject = reconstruct(objectPointer,
                                                    targetType);
                    }else{
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
            default: {
                throw new IllegalArgumentException("Can not convert wl_argument type: " + type);
            }
        }
    }

    private static Object reconstruct(final long objectPointer,
                                             final Class<?> targetType) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?> constructor = CONSTRUCTOR_CACHE.get(targetType);
        if(constructor == null){
            constructor = targetType.getDeclaredConstructor(long.class);
            constructor.setAccessible(true);
            CONSTRUCTOR_CACHE.put(targetType,
                                  constructor);
        }
        return constructor.newInstance(objectPointer);
    }
}