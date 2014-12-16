package examples;

import org.freedesktop.wayland.client.*;
import org.freedesktop.wayland.shared.WlPointerButtonState;
import org.freedesktop.wayland.shared.WlShmFormat;
import org.freedesktop.wayland.util.Fixed;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Window {

  private int previousMotionX;
  private int previousMotionY;

  private int deltaX;
  private int deltaY;

    public class Buffer {

        private final ShmPool       shmPool;
        private       WlBufferProxy bufferProxy;
        private final ByteBuffer    byteBuffer;

        public Buffer() {
            try {
                this.shmPool = new ShmPool(Window.this.width * Window.this.height * 4);

                WlShmPoolProxy pool = Window.this.display.getShmProxy()
                                             .createPool(new WlShmPoolEvents() {
                                                         },
                                                         this.shmPool.getFileDescriptor(),
                                                         Window.this.width * Window.this.height * 4);
                this.bufferProxy = pool.createBuffer(new WlBufferEvents() {
                                                    @Override
                                                    public void release(final WlBufferProxy emitter) {
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
            catch (IOException e) {
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

    private final int width;
    private final int height;

    private WlSurfaceProxy  surfaceProxy;
    private WlCallbackProxy callbackProxy;
    private Buffer          buffer;

    public Window(Display display,
                  int width,
                  int height) {
        this.display = display;
        this.width = width;
        this.height = height;

        this.buffer = new Buffer();

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
    }

    public void destroy() {
        if (this.callbackProxy != null) {
            this.callbackProxy.destroy();
        }
        this.surfaceProxy.destroy();
    }

    private int abs(int i) {
        return i < 0 ? -i : i;
    }

    private void paintPixels(ByteBuffer buffer,
                             int padding,
                             int time) {
        final int halfh = padding + (this.height - padding * 2) / 2;
        final int halfw = padding + (this.width - padding * 2) / 2;
        int ir;
        int or;
        IntBuffer image = buffer.asIntBuffer();
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
            int y2 = (y - halfh) * (y - halfh);

            image.position(image.position() + padding);
            for (int x = padding; x < this.width - padding; x++) {
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

                if (abs(x - y) > 6 && abs(x + y - this.height) > 6) {
                    v |= 0xff000000;
                }

                image.put(v);
            }
            image.position(image.position() + padding);
        }
    }

    public void redraw(final int time) {
        paintPixels(this.buffer.getByteBuffer(),
                    20,
                    time);
        this.display.getSeatProxy().getPointer(new WlPointerEventsV3() {

            boolean buttonPressed = false;
            @Override
            public void enter(final WlPointerProxy emitter,
                              @Nonnull final int serial,
                              @Nonnull final WlSurfaceProxy surface,
                              @Nonnull final Fixed surfaceX,
                              @Nonnull final Fixed surfaceY) {

            }

            @Override
            public void leave(final WlPointerProxy emitter,
                              @Nonnull final int serial,
                              @Nonnull final WlSurfaceProxy surface) {
                buttonPressed = false;
            }

            @Override
            public void motion(final WlPointerProxy emitter,
                               @Nonnull final int time,
                               @Nonnull final Fixed surfaceX,
                               @Nonnull final Fixed surfaceY) {
                if(buttonPressed){

                  final int surfaceXInt = surfaceX.asInt();
                  final int surfaceYInt = surfaceY.asInt();

                  //only calculate delta when we have motion (ie requires 2 points)
                  if(previousMotionX != 0 && previousMotionY != 0) {
                    deltaX = surfaceXInt - previousMotionX;
                    deltaY = surfaceYInt - previousMotionY;
                  }

                  previousMotionX = surfaceXInt;
                  previousMotionY = surfaceYInt;
                }
            }

            @Override
            public void button(final WlPointerProxy emitter,
                               @Nonnull final int serial,
                               @Nonnull final int time,
                               @Nonnull final int button,
                               @Nonnull final int state) {
                buttonPressed = state == WlPointerButtonState.PRESSED.getValue();
                if(!buttonPressed){
                  previousMotionX = 0;
                  previousMotionY = 0;
                }
            }

            @Override
            public void axis(final WlPointerProxy emitter,
                             @Nonnull final int time,
                             @Nonnull final int axis,
                             @Nonnull final Fixed value) {

            }
        });

        this.surfaceProxy.attach(this.buffer.getProxy(),
                                 deltaX,
                                 deltaY);
        deltaX = 0;
        deltaY = 0;

        this.surfaceProxy.damage(20,
                                 20,
                                 this.height - 40,
                                 this.height - 40);

        final WlCallbackEvents wlCallbackEvents = new WlCallbackEvents() {
            @Override
            public void done(final WlCallbackProxy emitter,
                             final int callbackData) {
                Window.this.callbackProxy.destroy();
                redraw(callbackData);

            }
        };
        this.callbackProxy = this.surfaceProxy.frame(wlCallbackEvents);

        this.surfaceProxy.commit();
    }
}
