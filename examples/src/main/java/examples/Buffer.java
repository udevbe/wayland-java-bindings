package examples;

import org.freedesktop.wayland.client.WlBufferEvents;
import org.freedesktop.wayland.client.WlBufferProxy;

import java.nio.ByteBuffer;


public class Buffer implements WlBufferEvents {

    private final BufferPool bufferPool;
    private final ByteBuffer byteBuffer;
    private final int width;
    private final int height;

    public Buffer(final BufferPool bufferPool, final ByteBuffer byteBuffer, final int width, final int height) {
        this.bufferPool = bufferPool;
        this.byteBuffer = byteBuffer;
        this.width = width;
        this.height = height;
    }

    @Override
    public void release(final WlBufferProxy emitter) {
        if(bufferPool.isDestroyed()){
            emitter.destroy();
        }else {
            bufferPool.queueBuffer(emitter);
        }
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
