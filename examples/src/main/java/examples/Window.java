package examples;

import org.freedesktop.wayland.client.*;
import org.freedesktop.wayland.shared.WlPointerButtonState;
import org.freedesktop.wayland.shared.WlShmFormat;
import org.freedesktop.wayland.util.Fixed;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;

public class Window {

    private final WlShellSurfaceProxy shellSurface;

    public class Buffer {

        private final ShmPool       shmPool;
        private final WlBufferProxy bufferProxy;
        private final ByteBuffer    byteBuffer;

        public Buffer() {
            try {
                this.shmPool = new ShmPool(Window.this.width * Window.this.height * 4);

                final WlShmPoolProxy pool = Window.this.display.getShmProxy()
                                                               .createPool(new WlShmPoolEvents() {
                                                                           },
                                                                           this.shmPool.getFileDescriptor(),
                                                                           Window.this.width * Window.this.height * 4);
                this.bufferProxy = pool.createBuffer(new WlBufferEvents() {
                                                         @Override
                                                         public void release(final WlBufferProxy emitter) {
                                                             Window.this.bufferPool.add(Buffer.this);
                                                         }
                                                     },
                                                     0,
                                                     Window.this.width,
                                                     Window.this.height,
                                                     Window.this.width * 4,
                                                     WlShmFormat.XRGB8888.getValue());
                pool.destroy();
                this.byteBuffer = this.shmPool.asByteBuffer();
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        public ByteBuffer getByteBuffer() {
            return this.byteBuffer;
        }

        public WlBufferProxy getProxy() {
            return this.bufferProxy;
        }
    }

    private final Display display;

    private int width;
    private int height;

    private final WlSurfaceProxy  surfaceProxy;
    private       WlCallbackProxy callbackProxy;
    private final LinkedList<Buffer> bufferPool = new LinkedList<Buffer>();

    public Window(final Display display,
                  final int width,
                  final int height) {
        this.display = display;
        this.width = width;
        this.height = height;

        this.bufferPool.add(new Buffer());
        this.bufferPool.add(new Buffer());
        this.bufferPool.add(new Buffer());

        this.surfaceProxy = display.getCompositorProxy()
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
        this.surfaceProxy.damage(0,
                                 0,
                                 width,
                                 height);

        final WlRegionProxy inputRegion = display.getCompositorProxy()
                                                 .createRegion(new WlRegionEvents() {
                                                 });
        inputRegion.add(0,
                        0,
                        width,
                        height);
        this.surfaceProxy.setInputRegion(inputRegion);

        this.shellSurface = this.display.getShellProxy()
                                        .getShellSurface(new WlShellSurfaceEvents() {
                                                             @Override
                                                             public void ping(final WlShellSurfaceProxy emitter,
                                                                              final int serial) {
                                                                 emitter.pong(serial);
                                                             }

                                                             @Override
                                                             public void configure(final WlShellSurfaceProxy emitter,
                                                                                   final int edges,
                                                                                   final int width,
                                                                                   final int height) {
                                                                 Window.this.width = width;
                                                                 Window.this.height = height;
                                                             }

                                                             @Override
                                                             public void popupDone(final WlShellSurfaceProxy emitter) {

                                                             }
                                                         },
                                                         this.surfaceProxy);

        this.display.getSeatProxy()
                    .getPointer(new WlPointerEventsV3() {

                        boolean buttonPressed = false;

                        @Override
                        public void enter(final WlPointerProxy emitter,
                                          final int serial,
                                          @Nonnull final WlSurfaceProxy surface,
                                          @Nonnull final Fixed surfaceX,
                                          @Nonnull final Fixed surfaceY) {

                        }

                        @Override
                        public void leave(final WlPointerProxy emitter,
                                          final int serial,
                                          @Nonnull final WlSurfaceProxy surface) {
                        }

                        @Override
                        public void motion(final WlPointerProxy emitter,
                                           final int time,
                                           @Nonnull final Fixed surfaceX,
                                           @Nonnull final Fixed surfaceY) {
                        }

                        @Override
                        public void button(final WlPointerProxy emitter,
                                           final int serial,
                                           final int time,
                                           final int button,
                                           final int state) {
                            this.buttonPressed = state == WlPointerButtonState.PRESSED.getValue();
                            if (this.buttonPressed) {
                                Window.this.shellSurface.move(display.getSeatProxy(),
                                                              serial);
                            }
                        }

                        @Override
                        public void axis(final WlPointerProxy emitter,
                                         final int time,
                                         final int axis,
                                         @Nonnull final Fixed value) {

                        }
                    });
    }

    public void destroy() {
        this.surfaceProxy.destroy();
    }

    private int abs(final int i) {
        return i < 0 ? -i : i;
    }

    private void paintPixels(final ByteBuffer buffer,
                             final int padding,
                             final int time) {
        final int halfh = padding + (this.height - padding * 2) / 2;
        final int halfw = padding + (this.width - padding * 2) / 2;
        int ir;
        int or;
        final IntBuffer image = buffer.asIntBuffer();
        image.clear();
        for (int i = 0; i < this.width * this.height; ++i) {
            image.put(0xffffffff);
        }
        image.clear();

        /* squared radii thresholds */
        or = (halfw < halfh ? halfw : halfh) - 8;
        ir = or - 32;
        or = or * or;
        ir = ir * ir;

        image.position(padding * this.width);
        for (int y = padding; y < this.height - padding; y++) {
            final int y2 = (y - halfh) * (y - halfh);

            image.position(image.position() + padding);
            for (int x = padding; x < this.width - padding; x++) {
                int v;

                final int r2 = (x - halfw) * (x - halfw) + y2;

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

                if (abs(x - y) > 6 && abs(x + y - this.height) > 6) {
                    v |= 0xff000000;
                }

                image.put(v);
            }
            image.position(image.position() + padding);
        }
    }

    public void redraw(final int time) {
        final Buffer buffer = this.bufferPool.pop();
        paintPixels(buffer.getByteBuffer(),
                    20,
                    time);

        this.surfaceProxy.attach(buffer.getProxy(),
                                 0,
                                 0);
        this.surfaceProxy.damage(20,
                                 20,
                                 this.height - 40,
                                 this.height - 40);
        //cleanup the previous frame callback
        if (this.callbackProxy != null) {
            Window.this.callbackProxy.destroy();
        }
        //allocate a new frame callback
        this.callbackProxy = this.surfaceProxy.frame(new WlCallbackEvents() {
            @Override
            public void done(final WlCallbackProxy emitter,
                             final int time) {
                redraw(time);
            }
        });

        this.surfaceProxy.commit();
    }
}
