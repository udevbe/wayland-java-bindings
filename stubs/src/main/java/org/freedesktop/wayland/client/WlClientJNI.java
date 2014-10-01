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
package org.freedesktop.wayland.client;

import org.freedesktop.wayland.arch.Native;

public class WlClientJNI {
    static {
        //load the jni lib
        Native.loadLibrary("wayland-java-client");
    }

    //display
    //called by generated display proxy
    public static native long connect(String displayName);

    //called by generated display proxy
    public static native long connect(int displayFd);

    public static native void disconnect(long displayPointer);

    public static native int getFD(long displayPointer);

    public static native int dispatch(long displayPointer);

    public static native int dispatchPending(long displayPointer);

    public static native int dispatchQueue(long displayPointer,
                                           long queuePointer);

    public static native int dispatchQueuePending(long displayPointer,
                                                  long queuePointer);

    public static native int flush(long displayPointer);

    public static native int roundtrip(long displayPointer);

    public static native long createQueue(long displayPointer);

    public static native int getError(long displayPointer);

    public static native int prepareReadQueue(long displayPointer,
                                              long queuePointer);

    public static native int prepareRead(long displayPointer);

    public static native void cancelRead(long displayPointer);

    public static native int readEvents(long displayPointer);

    //event queue
    public static native void destroyEventQueue(long eventQueuePointer);

    //proxy
    public static native void marshal(long proxyPointer,
                                      int opcode,
                                      long argumentsPointer);

    public static native void destroy(long proxyPointer);

    public static native Object getListener(long proxyPointer);

    public static native int getProxyId(long proxyPointer);

    public static native void setQueue(long proxyPointer,
                                       long queuePointer);

    public static native long marshalConstructor(long proxyPointer,
                                                 int opcode,
                                                 long interfacePointer,
                                                 long argumentsPointer);

    public static native void addDispatcher(long proxyPointer,
                                            Object implementation);

}