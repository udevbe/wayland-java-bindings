package examples;

import org.freedesktop.wayland.client.WlBufferEvents;
import org.freedesktop.wayland.client.WlBufferProxy;
import org.freedesktop.wayland.client.WlShmPoolProxy;

import java.nio.ByteBuffer;


public class Buffer implements WlBufferEvents {

    private final WlShmPoolProxy wlShmPoolProxy;
    private final ByteBuffer byteBuffer;
    private final int width;
    private final int height;

    public Buffer(final WlShmPoolProxy wlShmPoolProxy, final ByteBuffer byteBuffer, final int width, final int height) {
        this.wlShmPoolProxy = wlShmPoolProxy;
        this.byteBuffer = byteBuffer;
        this.width = width;
        this.height = height;
    }

    @Override
    public void release(final WlBufferProxy emitter) {
        final BufferPool bufferPool = (BufferPool) this.wlShmPoolProxy.getImplementation();
        bufferPool.queueBuffer(emitter);
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
