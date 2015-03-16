package examples;

import org.freedesktop.wayland.client.WlBufferProxy;
import org.freedesktop.wayland.client.WlShmPoolProxy;
import org.freedesktop.wayland.shared.WlShmFormat;

import java.io.IOException;
import java.nio.ByteBuffer;

public class BufferPoolFactory {

    private final Display display;

    public BufferPoolFactory(Display display) {
        this.display = display;
    }

    public WlShmPoolProxy create(int width, int height, int size) throws IOException {
        final int bufferSize = width * height * 4;
        final ShmPool shmPool = new ShmPool(bufferSize *size);
        final BufferPool bufferPool = new BufferPool();
        final WlShmPoolProxy
                wlShmPoolProxy =
                this.display.getShmProxy()
                        .createPool(bufferPool,
                                    shmPool.getFileDescriptor(),
                                    bufferSize * size);
        for(int i = 0; i < size; i++){
            final int offset = i * bufferSize;
            final ByteBuffer poolByteBuffer = shmPool.asByteBuffer();
            poolByteBuffer.position(offset);
            final ByteBuffer byteBuffer = poolByteBuffer.slice();

            final WlBufferProxy buffer = wlShmPoolProxy.createBuffer(new Buffer(bufferPool,
                                                           byteBuffer,
                                                           width,
                                                           height),
                                                offset,
                                                width,
                                                height,
                                                width * 4,
                                                WlShmFormat.XRGB8888.getValue());
            bufferPool.queueBuffer(buffer);
        }
        return wlShmPoolProxy;
    }
}
