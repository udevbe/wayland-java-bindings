//Copyright 2015 Erik De Rijcke
//
//Licensed under the Apache License,Version2.0(the"License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing,software
//distributed under the License is distributed on an"AS IS"BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
package examples;

import org.freedesktop.wayland.client.*;
import org.freedesktop.wayland.shared.WlPointerButtonState;
import org.freedesktop.wayland.shared.WlShellSurfaceResize;
import org.freedesktop.wayland.util.Fixed;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.IntBuffer;

public class Window {

    private final WlShellSurfaceProxy shellSurface;
    private final WlRegionProxy inputRegion;

    private final WlSurfaceProxy  surfaceProxy;
    private       WlCallbackProxy callbackProxy;
    private BufferPool bufferPool;

    public Window(final Display display,
                  final int width,
                  final int height) throws IOException {

        bufferPool = createBufferPool(display,width,height,2);

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

        inputRegion = display.getCompositorProxy().createRegion(new WlRegionEvents() {
        });
        inputRegion.add(0,
                        0,
                        width,
                        height);
        this.surfaceProxy.setInputRegion(inputRegion);

        this.shellSurface = display.getShellProxy()
                                        .getShellSurface(new WlShellSurfaceEvents() {
                                                             @Override
                                                             public void ping(final WlShellSurfaceProxy emitter,
                                                                              final int serial) {
                                                                 emitter.pong(serial);
                                                             }

                                                             @Override
                                                             public void configure(final WlShellSurfaceProxy emitter,
                                                                                   final int edges,
                                                                                   int width,
                                                                                   int height) {
                                                                 try {
                                                                     bufferPool.destroy();
                                                                     bufferPool = createBufferPool(display, width,
                                                                                                   height, 2);
                                                                     Window.this.inputRegion.add(0,
                                                                                                 0,
                                                                                                 width,
                                                                                                 height);
                                                                 } catch (IOException e) {
                                                                     e.printStackTrace();
                                                                 }

                                                             }

                                                             @Override
                                                             public void popupDone(final WlShellSurfaceProxy emitter) {

                                                             }
                                                         },
                                                         this.surfaceProxy);

        display.getSeatProxy()
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
                            if (this.buttonPressed && button == 1) {
                                Window.this.shellSurface.move(display.getSeatProxy(),
                                                              serial);
                            }
                            else if(this.buttonPressed && button == 3){
                                Window.this.shellSurface.resize(display.getSeatProxy(),
                                                                serial,
                                                                WlShellSurfaceResize.NONE.getValue());
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

    private BufferPool createBufferPool(Display display, int width, int height, int size) throws IOException {
        final WlShmPoolProxy wlShmPoolProxy = new BufferPoolFactory(display).create(width,
                                                                                    height,
                                                                                    2);
        final BufferPool bufferPool = (BufferPool) wlShmPoolProxy.getImplementation();
        wlShmPoolProxy.destroy();
        return bufferPool;
    }

    public void destroy() {
        this.surfaceProxy.destroy();
    }

    private int abs(final int i) {
        return i < 0 ? -i : i;
    }

    private void paintPixels(final Buffer buffer,
                             final int time) {
        final int halfh = buffer.getHeight() / 2;
        final int halfw = buffer.getWidth() / 2;
        int ir;
        int or;
        final IntBuffer image = buffer.getByteBuffer().asIntBuffer();
        image.clear();
        for (int i = 0; i < buffer.getWidth() * buffer.getHeight(); ++i) {
            image.put(0xffffffff);
        }
        image.clear();

        /* squared radii thresholds */
        or = (halfw < halfh ? halfw : halfh) - 8;
        ir = or - 32;
        or = or * or;
        ir = ir * ir;

        image.position(0);
        for (int y = 0; y < buffer.getHeight(); y++) {
            final int y2 = (y - halfh) * (y - halfh);

            image.position(image.position());
            for (int x = 0; x < buffer.getWidth(); x++) {
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

                if (abs(x - y) > 6 && abs(x + y - buffer.getHeight()) > 6) {
                    v |= 0xff000000;
                }

                image.put(v);
            }
            image.position(image.position());
        }
    }

    public void redraw(final int time) {
        final WlBufferProxy wlBufferProxy = bufferPool.popBuffer();
        final Buffer buffer = (Buffer) wlBufferProxy.getImplementation();
        paintPixels(buffer,
                    time);

        this.surfaceProxy.attach(wlBufferProxy,
                                 0,
                                 0);
        this.surfaceProxy.damage(0,
                                 0,
                                 buffer.getWidth(),
                                 buffer.getHeight());
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
