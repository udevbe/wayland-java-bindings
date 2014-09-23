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
package org.freedesktop.wayland.server;

import org.freedesktop.wayland.arch.Native;

import java.nio.ByteBuffer;

public class WlServerJNI {
    static {
        //load the jni lib
        Native.loadLibrary("wayland-java-server");

        //register classes that expose a callback that is called from JNI.
        registerFDHandler(EventLoop.FileDescriptorEventHandler.class);
        registerTimerHandler(EventLoop.TimerEventHandler.class);
        registerSignalHandler(EventLoop.SignalEventHandler.class);
        registerIdleHandler(EventLoop.IdleHandler.class);
        registerBindClientHandler(Global.class);
        registerListenerHandler(Listener.class);
    }

    //listener
    public static native long createListener(Listener listener);

    public static native void removeListener(long listenerPointer);

    public static native void registerListenerHandler(Class<Listener> listenerClass);

    //display
    public static native long addShmFormat(long displayPointer,
                                           int format);

    public static native int initShm(long displayPointer);

    public static native long getEventLoop(long displayPointer);

    public static native int addSocket(long displayPointer,
                                       String name);

    public static native void terminate(long displayPointer);

    public static native void run(long displayPointer);

    public static native void flushClients(long displayPointer);

    public static native int getSerial(long displayPointer);

    public static native int nextSerial(long displayPointer);

    public static native long createDisplay();

    public static native void destroy(long displayPointer);

    public static native void addDisplayDestroyListener(long displayPointer,
                                                        long listenerPointer);

    //event loop
    private static native void registerFDHandler(Class<EventLoop.FileDescriptorEventHandler> clazz);

    private static native void registerTimerHandler(Class<EventLoop.TimerEventHandler> clazz);

    private static native void registerSignalHandler(Class<EventLoop.SignalEventHandler> clazz);

    private static native void registerIdleHandler(Class<EventLoop.IdleHandler> clazz);

    public static native long addFileDescriptor(long eventLoopPointer,
                                                int fd,
                                                int mask,
                                                EventLoop.FileDescriptorEventHandler handler);

    public static native int updateFileDescriptor(long eventSourcePointer,
                                                  int mask);

    public static native long addTimer(long eventLoopPointer,
                                       EventLoop.TimerEventHandler handler);

    public static native int updateTimer(long eventSourcePointer,
                                         int milliseconds);

    public static native long addSignal(long eventLoopPointer,
                                        int signalNumber,
                                        EventLoop.SignalEventHandler handler);

    public static native long addIdle(long eventLoopPointer,
                                      EventLoop.IdleHandler handler);

    public static native int remove(long eventSourcePointer);

    public static native void check(long eventSourcePointer);

    public static native int dispatch(long eventLoopPointer,
                                      int timeout);

    public static native void dispatchIdle(long eventLoopPointer);

    public static native long createEventLoop();

    public static native int getFileDescriptor(long eventLoopPointer);

    public static native void addEventLoopDestroyListener(long eventLoopPointer,
                                                          long listenerPointer);

    //global
    public static native long createGlobal(long displayPointer,
                                           long interfacePointer,
                                           int version,
                                           Global global);

    public static native void destroyGlobal(final long globalPointer);

    private static native void registerBindClientHandler(Class<Global> globalClass);

    //resource
    public static native long createResource(long clientPointer,
                                             long interfacePointer,
                                             int version,
                                             int id);

    public static native int getVersion(final long resourcePointer);

    public static native int getId(long resourcePointer);

    public static native void addResourceDestroyListener(long resourcePointer,
                                                         long listenerPointer);

    public static native void destroyResource(long resourcePointer);

    public static native void postEvent(long resourcePointer,
                                        int opcode,
                                        long argumentsPointer);

    public static native void postError(long resourcePointer,
                                        int code,
                                        String msg);

    public static native long getClient(long resourcePointer);

    public static native Object getImplementation(long resourcePointer);

    public static native void setDispatcher(long resourcePointer,
                                            Object implementation);

    public static native long getInterface(final long resourcePointer);

    //client
    public static native long createClient(long displayPointer,
                                           int fd);

    public static native void flush(long clientPointer);

    public static native void addClientDestroyListener(long clientPointer,
                                                       long listenerPointer);

    public static native void destroyClient(long clientPointer);

    public static native long getDisplay(long clientPointer);

    //shm buffer
    public static native long createShmBuffer(long clientPointer,
                                              int id,
                                              int width,
                                              int height,
                                              int stride,
                                              int format);

    public static native void beginAccess(long shmBufferPointer);

    public static native void endAccess(long shmBufferPointer);

    public static native ByteBuffer getData(long shmBufferPointer,
                                            long size);

    public static native int getStride(long shmBufferPointer);

    public static native int getFormat(long shmBufferPointer);

    public static native int getWidth(long shmBufferPointer);

    public static native int getHeight(long shmBufferPointer);

    public static native long get(long bufferResourcePointer);

    public static native long getDataAsPointer(long bufferPointer);
}
