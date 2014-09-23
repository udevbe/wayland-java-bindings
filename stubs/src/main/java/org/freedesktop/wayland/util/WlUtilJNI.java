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

import java.nio.ByteBuffer;

public class WlUtilJNI {

    static {
        register(Dispatcher.class);
    }

    private static native void register(Class<Dispatcher> dispatcherClass);

    //arguments
    public static native long createArguments(int size);

    public static native void destroyArgument(long argumentPointer);

    public static native int getIArgument(long argumentPointer,
                                          final int index);

    public static native int getUArgument(long argumentPointer,
                                          final int index);

    public static native int getFArgument(long argumentPointer,
                                          final int index);

    public static native String getSArgument(long argumentPointer,
                                             final int index);

    public static native long getOArgument(long argumentPointer,
                                           final int index);

    public static native int getNArgument(long argumentPointer,
                                          final int index);

    public static native byte[] getAArgument(long argumentPointer,
                                             final int index);

    public static native int getHArgument(long argumentPointer,
                                          final int index);

    public static native void setIUNHArgument(long argumentPointer,
                                              final int index,
                                              int iunh);

    public static native void setOArgument(long argumentPointer,
                                           int index,
                                           long objectPointer);

    public static native void setFArgument(long argumentPointer,
                                           int index,
                                           int raw);

    public static native void setSArgument(long argumentPointer,
                                           int index,
                                           String s);

    public static native void setAArgument(long argumentPointer,
                                           int index,
                                           ByteBuffer array);


    //message
    public static native long initMessage(long messagePointer,
                                          int index,
                                          String signature,
                                          String name,
                                          long[] typeInterfacePointers);

    //interface
    public static native long createInterface(final String name,
                                              final int version,
                                              final long methodMessagePointer,
                                              final int methodCount,
                                              final long eventMessagePointer,
                                              final int eventCount);

    public static native String getName(final long interfacePointer);

    public static native long allocateMessages(final int nroMessages);

    //misc
    public static native void free(final long pointer);
}