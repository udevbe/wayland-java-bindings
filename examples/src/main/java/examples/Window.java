package examples;

import org.freedesktop.wayland.client.*;
import org.freedesktop.wayland.shared.WlShmFormat;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Window {

    public class Buffer {

        private final ShmPool       shmPool;
        private       WlBufferProxy bufferProxy;
        private final ByteBuffer byteBuffer;

        public Buffer() {
            try {
                shmPool = new ShmPool(width * height * 4);

                WlShmPoolProxy pool = display.getShmProxy()
                                             .createPool(new WlShmPoolEvents() {
                                                         },
                                                         shmPool.getFileDescriptor(),
                                                         width * height * 4);
                bufferProxy = pool.createBuffer(new WlBufferEvents() {
                                                    @Override
                                                    public void release(final WlBufferProxy emitter) {
                                                    }
                                                },
                                                0,
                                                width,
                                                height,
                                                width * 4,
                                                WlShmFormat.XRGB8888.getValue());
                pool.destroy();
                byteBuffer = shmPool.asByteBuffer();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public ByteBuffer getByteBuffer() {
            return byteBuffer;
        }

        public WlBufferProxy getProxy() {
            return bufferProxy;
        }
    }

    private final Display display;

    private final int width;
    private final int height;

    private WlSurfaceProxy      surfaceProxy;
    private WlShellSurfaceProxy shellSurfaceProxy;
    private WlCallbackProxy     callbackProxy;
    private Buffer              buffer;

    public Window(Display display,
                  int width,
                  int height) {
        this.display = display;
        this.width = width;
        this.height = height;

        buffer = new Buffer();

        surfaceProxy = display.getCompositorProxy()
                              .createSurface(new WlSurfaceEvents() {
                                  @Override
                                  public void enter(final WlSurfaceProxy emitter,
                                                    @Nonnull
                                                    final WlOutputProxy output) {

                                  }

                                  @Override
                                  public void leave(final WlSurfaceProxy emitter,
                                                    @Nonnull
                                                    final WlOutputProxy output) {

                                  }
                              });
        surfaceProxy.damage(0,
                            0,
                            width,
                            height);

        shellSurfaceProxy = display.getShellProxy()
                                   .getShellSurface(new WlShellSurfaceEvents() {
                                                        @Override
                                                        public void ping(final WlShellSurfaceProxy emitter,
                                                                         @Nonnull
                                                                         final int serial) {
                                                            emitter.pong(serial);
                                                        }

                                                        @Override
                                                        public void configure(final WlShellSurfaceProxy emitter,
                                                                              @Nonnull
                                                                              final int edges,
                                                                              @Nonnull
                                                                              final int width,
                                                                              @Nonnull
                                                                              final int height) {

                                                        }

                                                        @Override
                                                        public void popupDone(final WlShellSurfaceProxy emitter) {

                                                        }
                                                    },
                                                    surfaceProxy);

        shellSurfaceProxy.setTitle("simple-shm");
        shellSurfaceProxy.setToplevel();
    }

    public void destroy() {
        if (callbackProxy != null) {
            callbackProxy.destroy();
        }

        shellSurfaceProxy.destroy();
        surfaceProxy.destroy();
    }

    private int abs(int i) {
        return i < 0 ? -i : i;
    }

    private void paintPixels(ByteBuffer buffer,
                             int padding,
                             int time) {
        final int halfh = padding + (height - padding * 2) / 2;
        final int halfw = padding + (width - padding * 2) / 2;
        int ir;
        int or;
        IntBuffer image = buffer.asIntBuffer();
        image.clear();
        for (int i = 0; i < width * height; ++i) {
            image.put(0xffffffff);
        }
        image.clear();

        /* squared radii thresholds */
        or = (halfw < halfh ? halfw : halfh) - 8;
        ir = or - 32;
        or = or * or;
        ir = ir * ir;

        image.position(padding * width);
        for (int y = padding; y < height - padding; y++) {
            int y2 = (y - halfh) * (y - halfh);

            image.position(image.position() + padding);
            for (int x = padding; x < width - padding; x++) {
                int v;

                int r2 = (x - halfw) * (x - halfw) + y2;

                if (r2 < ir) {
                    v = (r2 / 32 + time / 64) * 0x0080401;
                }
                else if (r2 < or) {
                    v = (y + time / 32) * 0x0080401;
                }
                else {
                    v = (x + time / 16) * 0x0080401;
                }
                v &= 0x00ffffff;

                if (abs(x - y) > 6 && abs(x + y - height) > 6) {
                    v |= 0xff000000;
                }

                image.put(v);
            }
            image.position(image.position() + padding);
        }
    }

    public void redraw(final int time) {
        paintPixels(buffer.getByteBuffer(),
                    20,
                    time);
        surfaceProxy.attach(buffer.getProxy(),
                            0,
                            0);
        surfaceProxy.damage(20,
                            20,
                            height - 40,
                            height - 40);

        final WlCallbackEvents wlCallbackEvents = new WlCallbackEvents() {
            @Override
            public void done(final WlCallbackProxy emitter,
                             final int callbackData) {
                callbackProxy.destroy();
                redraw(callbackData);

            }
        };
        callbackProxy = surfaceProxy.frame(wlCallbackEvents);

        surfaceProxy.commit();
    }
}
