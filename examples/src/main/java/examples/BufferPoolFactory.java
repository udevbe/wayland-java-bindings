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

    public BufferPool create(int width, int height, int size, WlShmFormat shmFormat) throws IOException {

        final BufferPool bufferPool = new BufferPool();
        for(int i = 0; i < size; i++){
            final int bufferSize = width * height * 4;
            final ShmPool shmPool = new ShmPool(bufferSize);

            final WlShmPoolProxy
                    wlShmPoolProxy =
                    this.display.getShmProxy()
                            .createPool(bufferPool,
                                        shmPool.getFileDescriptor(),
                                        bufferSize);
            final WlBufferProxy buffer = wlShmPoolProxy.createBuffer(new Buffer(bufferPool,
                                                           shmPool,
                                                           width,
                                                           height),
                                                0,
                                                width,
                                                height,
                                                width * 4,
                                                shmFormat.getValue());
            bufferPool.queueBuffer(buffer);
            wlShmPoolProxy.destroy();
        }
        return bufferPool;
    }
}
