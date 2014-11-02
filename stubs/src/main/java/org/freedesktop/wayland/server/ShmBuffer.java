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

import org.freedesktop.wayland.HasPointer;
import org.freedesktop.wayland.util.ObjectCache;

import java.nio.ByteBuffer;

public class ShmBuffer implements HasPointer {

    private final long pointer;

    protected ShmBuffer(final long pointer) {
        this.pointer = pointer;
    }

    /**
     * Create a new underlying WlBufferResource with the constructed ShmBuffer as it's implementation.
     * <p>
     * {@code ShmBuffer} should never be stored in a compositor instead it should always be queried from a
     * {@code WlBufferResource}. Listening for the resource's destruction can be done when the buffer
     * resource is attached to a surface.
     *
     * @param client
     * @param id
     * @param width
     * @param height
     * @param stride
     * @param format
     */
    public ShmBuffer(final Client client,
                     final int id,
                     final int width,
                     final int height,
                     final int stride,
                     final int format) {
        this(WlServerJNI.createShmBuffer(client.getPointer(),
                                         id,
                                         width,
                                         height,
                                         stride,
                                         format));
    }

    public static ShmBuffer get(final Resource<?> resource) {
        final long bufferPointer = WlServerJNI.get(resource.getPointer());
        final ShmBuffer buffer;
        if (bufferPointer == 0) {
            buffer = null;
        }
        else {
            buffer = new ShmBuffer(bufferPointer);
        }
        return buffer;
    }

    public void destroy() {
        //don't free the underlying native context, that's taking care of for us in the native layer.
        ObjectCache.remove(getPointer());
    }

    @Override
    public long getPointer() {
        return this.pointer;
    }

    /**
     * Mark that the given SHM buffer is about to be accessed
     * <p>
     * An SHM buffer is a memory-mapped file given by the client.
     * According to POSIX, reading from a memory-mapped region that
     * extends off the end of the file will cause a SIGBUS signal to be
     * generated. Normally this would cause the compositor to terminate.
     * In order to make the compositor robust against clients that change
     * the size of the underlying file or lie about its size, you should
     * protect access to the buffer by calling this function before
     * reading from the memory and call {@link #endAccess()}
     * afterwards. This will install a signal handler for SIGBUS which
     * will prevent the compositor from crashing.
     * <p>
     * After calling this function the signal handler will remain
     * installed for the lifetime of the compositor process. Note that
     * this function will not work properly if the compositor is also
     * installing its own handler for SIGBUS.
     * <p>
     * If a SIGBUS signal is received for an address within the range of
     * the SHM pool of the given buffer then the client will be sent an
     * error event when {@link #endAccess()} is called. If the signal
     * is for an address outside that range then the signal handler will
     * reraise the signal which would will likely cause the compositor to
     * terminate.
     * <p>
     * It is safe to nest calls to these functions as long as the nested
     * calls are all accessing the same buffer. The number of calls to
     * wl_shm_buffer_end_access must match the number of calls to
     * {@link #beginAccess()}. These functions are thread-safe and it
     * is allowed to simultaneously access different buffers or the same
     * buffer from multiple threads.
     */
    public void beginAccess() {
        WlServerJNI.beginAccess(getPointer());
    }

    /**
     * Ends the access to a buffer started by {@link #beginAccess()}.
     * <p>
     * This should be called after {@link #beginAccess()} once the
     * buffer is no longer being accessed. If a SIGBUS signal was
     * generated in-between these two calls then the resource for the
     * given buffer will be sent an error.
     */
    public void endAccess() {
        WlServerJNI.endAccess(getPointer());
    }

    /**
     * /** Get a pointer to the memory for the SHM buffer
     * <p>
     * Returns a pointer which can be used to read the data contained in
     * the given SHM buffer.
     * <p
     * As this buffer is memory-mapped, reading it from may generate
     * SIGBUS signals. This can happen if the client claims that the
     * buffer is larger than it is or if something truncates the
     * underlying file. To prevent this signal from causing the compositor
     * to crash you should call wl_shm_buffer_begin_access and
     * wl_shm_buffer_end_access around code that reads from the memory.
     *
     * @return a direct ByteBuffer.
     */
    public ByteBuffer getData() {
        return WlServerJNI.getData(getPointer(),
                                   getHeight() * getStride());
    }

    /**
     * @return
     * @see #getData()
     */
    public long getDataAsPointer() {
        return WlServerJNI.getDataAsPointer(getPointer());
    }

    public int getStride() {
        return WlServerJNI.getStride(getPointer());
    }

    public int getFormat() {
        return WlServerJNI.getFormat(getPointer());
    }

    public int getWidth() {
        return WlServerJNI.getWidth(getPointer());
    }

    public int getHeight() {
        return WlServerJNI.getHeight(getPointer());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShmBuffer)) {
            return false;
        }

        final ShmBuffer shmBuffer = (ShmBuffer) o;

        return getPointer() == shmBuffer.getPointer();

    }

    @Override
    public int hashCode() {
        return (int) getPointer();
    }
}
