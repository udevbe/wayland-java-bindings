package examples;

import org.freedesktop.wayland.client.WlBufferEvents;
import org.freedesktop.wayland.client.WlBufferProxy;

import java.io.IOException;
import java.nio.ByteBuffer;


public class Buffer implements WlBufferEvents {

    private final BufferPool bufferPool;
    private final ShmPool shmPool;
    private final int width;
    private final int height;

    public Buffer(final BufferPool bufferPool,
                  final ShmPool shmPool,
                  final int width,
                  final int height) {
        this.bufferPool = bufferPool;
        this.shmPool = shmPool;
        this.width = width;
        this.height = height;
    }

    @Override
    public void release(final WlBufferProxy emitter) {
        if(bufferPool.isDestroyed()){
            emitter.destroy();
            try {
                shmPool.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            bufferPool.queueBuffer(emitter);
        }
    }

    public ByteBuffer getByteBuffer() {
        return shmPool.asByteBuffer();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
