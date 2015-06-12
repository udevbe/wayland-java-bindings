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

import org.freedesktop.wayland.client.WlBufferProxy;
import org.freedesktop.wayland.client.WlCallbackEvents;
import org.freedesktop.wayland.client.WlCallbackProxy;
import org.freedesktop.wayland.client.WlOutputProxy;
import org.freedesktop.wayland.client.WlPointerEventsV3;
import org.freedesktop.wayland.client.WlPointerProxy;
import org.freedesktop.wayland.client.WlRegionEvents;
import org.freedesktop.wayland.client.WlRegionProxy;
import org.freedesktop.wayland.client.WlShellSurfaceEvents;
import org.freedesktop.wayland.client.WlShellSurfaceProxy;
import org.freedesktop.wayland.client.WlSurfaceEventsV3;
import org.freedesktop.wayland.client.WlSurfaceProxy;
import org.freedesktop.wayland.shared.WlPointerButtonState;
import org.freedesktop.wayland.shared.WlShellSurfaceResize;
import org.freedesktop.wayland.util.Fixed;

import java.io.IOException;
import java.nio.IntBuffer;

import javax.annotation.Nonnull;

import static org.freedesktop.wayland.shared.WlShmFormat.XRGB8888;

public class Window implements WlShellSurfaceEvents,
                               WlSurfaceEventsV3,
                               WlPointerEventsV3,
                               WlRegionEvents {

    private static final int BTN_LEFT = 0x110;
    private static final int BTN_RIGHT = 0x111;

    private final WlShellSurfaceProxy shellSurfaceProxy;
    private final WlRegionProxy regionProxy;

    private final WlSurfaceProxy surfaceProxy;
    private final Display display;
    private final WlPointerProxy pointerProxy;

    private WlCallbackProxy callbackProxy;
    private BufferPool bufferPool;


    private int dx;
    private int dy;
    private int width;
    private int height;

    private int pointerX;
    private int pointerY;

    public Window(final Display display,
                  final int width,
                  final int height) throws IOException {
        this.width = width;
        this.height = height;

        this.display = display;
        this.bufferPool = createBufferPool(this.display,
                                           2);
        this.surfaceProxy = this.display.getCompositorProxy()
                .createSurface(this);
        this.regionProxy = this.display.getCompositorProxy()
                .createRegion(this);
        this.surfaceProxy.setInputRegion(regionProxy);
        this.shellSurfaceProxy = this.display.getShellProxy()
                .getShellSurface(this,
                                 this.surfaceProxy);
        this.pointerProxy = this.display.getSeatProxy()
                .getPointer(this);

        this.surfaceProxy.damage(0,
                                 0,
                                 this.width,
                                 this.height);
        this.regionProxy.add(0,
                             0,
                             this.width,
                             this.height);
    }

    private BufferPool createBufferPool(final Display display,
                                        final int size) throws IOException {
        return new BufferPoolFactory(display).create(this.width,
                                                     this.height,
                                                     size,
                                                     XRGB8888);
    }

    @Override
    public void enter(final WlPointerProxy emitter,
                      final int serial,
                      @Nonnull final WlSurfaceProxy surface,
                      @Nonnull final Fixed surfaceX,
                      @Nonnull final Fixed surfaceY) {
        System.out.println(String.format("Pointer - enter : serial=%d, surface=%s, x=%s, y=%s",
                                         serial,
                                         surface,
                                         surfaceX,
                                         surfaceY));
    }

    @Override
    public void leave(final WlPointerProxy emitter,
                      final int serial,
                      @Nonnull final WlSurfaceProxy surface) {
        System.out.println(String.format("Pointer - leave : serial=%d, surface=%s",
                                         serial,
                                         surface));
    }

    @Override
    public void motion(final WlPointerProxy emitter,
                       final int time,
                       @Nonnull final Fixed surfaceX,
                       @Nonnull final Fixed surfaceY) {
        System.out.println(String.format("Pointer - motion : time=%d, x=%s, y=%s",
                                         time,
                                         surfaceX,
                                         surfaceY));
        this.pointerX = surfaceX.asInt();
        this.pointerY = surfaceY.asInt();
    }

    @Override
    public void button(final WlPointerProxy emitter,
                       final int serial,
                       final int time,
                       final int button,
                       final int state) {
        System.out.println(String.format("Pointer - button : serial=%d, time=%d, button=%d, state=%d",
                                         serial,
                                         time,
                                         button,
                                         state));

        final boolean buttonPressed = state == WlPointerButtonState.PRESSED.getValue();
        if (buttonPressed && button == BTN_LEFT) {
            this.shellSurfaceProxy.move(display.getSeatProxy(),
                                        serial);
        } else if (buttonPressed && button == BTN_RIGHT) {
            this.shellSurfaceProxy.resize(display.getSeatProxy(),
                                          serial,
                                          edge().getValue());
        }
    }

    private WlShellSurfaceResize edge() {
        boolean bottom = this.pointerY > (this.height / 2);
        boolean right = this.pointerX > (this.width / 2);

        if (bottom && right) {
            return WlShellSurfaceResize.BOTTOM_RIGHT;
        } else if (bottom) {
            return WlShellSurfaceResize.BOTTOM_LEFT;
        } else if (right) {
            return WlShellSurfaceResize.TOP_RIGHT;
        } else {
            return WlShellSurfaceResize.TOP_LEFT;
        }
    }

    @Override
    public void axis(final WlPointerProxy emitter,
                     final int time,
                     final int axis,
                     @Nonnull final Fixed value) {
        System.out.println(String.format("Pointer - axis : time=%d, axis=%d, value=%s",
                                         time,
                                         axis,
                                         value));
    }

    @Override
    public void ping(final WlShellSurfaceProxy emitter,
                     final int serial) {
//        System.out.println(String.format("shell surface - ping : serial=%d",
//                                         serial));

        emitter.pong(serial);
    }

    @Override
    public void configure(final WlShellSurfaceProxy emitter,
                          final int edges,
                          int width,
                          int height) {
        try {
            this.dx = this.width - width;
            this.dy = this.height - height;

            this.width = width;
            this.height = height;

            this.bufferPool.destroy();
            this.bufferPool = createBufferPool(display,
                                               2);
            this.regionProxy.add(0,
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

    @Override
    public void enter(final WlSurfaceProxy emitter,
                      @Nonnull final WlOutputProxy output) {

    }

    @Override
    public void leave(final WlSurfaceProxy emitter,
                      @Nonnull final WlOutputProxy output) {

    }

    public void destroy() {
        this.shellSurfaceProxy.destroy();
        this.surfaceProxy.destroy();
        this.regionProxy.destroy();
        this.pointerProxy.destroy();
        if (callbackProxy != null) {
            this.callbackProxy.destroy();
        }
        this.bufferPool.destroy();
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

        /* squared radii thresholds */
        or = (halfw < halfh ? halfw : halfh) - 8;
        ir = or - 32;
        or = or * or;
        ir = ir * ir;

        image.clear();
        image.position(0);
        for (int y = 0; y < buffer.getHeight(); y++) {
            final int y2 = (y - halfh) * (y - halfh);

            for (int x = 0; x < buffer.getWidth(); x++) {
                int v;

                final int r2 = (x - halfw) * (x - halfw) + y2;

                if (r2 < ir) {
                    v = (r2 / 32 + time / 64) * 0x0080401;
                } else if (r2 < or) {
                    v = (y + time / 32) * 0x0080401;
                } else {
                    v = (x + time / 16) * 0x0080401;
                }
                v &= 0x00ffffff;

                if (abs(x - y) > 6 && abs(x + y - buffer.getHeight()) > 6) {
                    v |= 0xff000000;
                }

                image.put(v);
            }
        }
    }

    public void redraw(final int time) {
        final WlBufferProxy wlBufferProxy = bufferPool.popBuffer();
        final Buffer buffer = (Buffer) wlBufferProxy.getImplementation();
        paintPixels(buffer,
                    time);

        this.surfaceProxy.attach(wlBufferProxy,
                                 this.dx,
                                 this.dy);
        this.surfaceProxy.damage(0,
                                 0,
                                 buffer.getWidth(),
                                 buffer.getHeight());
        //cleanup the previous frame callback
        if (this.callbackProxy != null) {
            this.callbackProxy.destroy();
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
        this.dx = 0;
        this.dy = 0;
    }
}
