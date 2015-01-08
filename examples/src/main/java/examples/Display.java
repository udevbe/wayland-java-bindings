package examples;


import org.freedesktop.wayland.client.*;

public class Display {

    private final WlDisplayProxy  displayProxy;
    private final WlRegistryProxy registryProxy;

    private int shmFormats = 0;

    private WlCompositorProxy compositorProxy;
    private WlShmProxy        shmProxy;
    private WlSeatProxy       seatProxy;
    private WlShellProxy      shellProxy;


    public Display() {
        this.displayProxy = WlDisplayProxy.connect("wayland-0");
        this.registryProxy = this.displayProxy.getRegistry(new WlRegistryEvents() {
            @Override
            public void global(final WlRegistryProxy emitter,
                               final int name,
                               @Nonnull final String interface_,
                               final int version) {
                Display.this.global(emitter,
                                    name,
                                    interfaceName,
                                    version);
            }

            @Override
            public void globalRemove(final WlRegistryProxy emitter,
                                     final int name) {
                Display.this.globalRemove(emitter,
                                          name);
            }
        });
        this.displayProxy.roundtrip();

        if (this.shmProxy == null) {
            throw new NullPointerException("wl_shm not found!");
        }

        this.displayProxy.roundtrip();
    }

    private void global(final WlRegistryProxy emitter,
                        final int name,
                        final String interfaceName,
                        final int version) {
        if (WlCompositorProxy.INTERFACE_NAME.equals(interfaceName)) {
            this.compositorProxy = this.registryProxy.<WlCompositorEvents, WlCompositorProxy>bind(name,
                                                                                                  WlCompositorProxy.class,
                                                                                                  WlCompositorEventsV3.VERSION,
                                                                                                  new WlCompositorEventsV3() {
                                                                                                  });
        }
        else if (WlShmProxy.INTERFACE_NAME.equals(interfaceName)) {
            this.shmProxy = this.registryProxy.<WlShmEvents, WlShmProxy>bind(name,
                                                                             WlShmProxy.class,
                                                                             WlShmEvents.VERSION,
                                                                             new WlShmEvents() {
                                                                                 @Override
                                                                                 public void format(final WlShmProxy emitter,
                                                                                                    final int format) {
                                                                                     Display.this.shmFormats |= (1 << format);
                                                                                 }
                                                                             });
        }
        else if (WlShellProxy.INTERFACE_NAME.equals(interfaceName)) {
            this.shellProxy = this.registryProxy.<WlShellEvents, WlShellProxy>bind(name,
                                                                                   WlShellProxy.class,
                                                                                   WlShellEvents.VERSION,
                                                                                   new WlShellEvents() {
                                                                                   });
        }
        else if (WlSeatProxy.INTERFACE_NAME.equals(interfaceName)) {
            this.seatProxy = this.registryProxy.<WlSeatEvents, WlSeatProxy>bind(name,
                                                                                WlSeatProxy.class,
                                                                                WlSeatEventsV4.VERSION,
                                                                                new WlSeatEventsV4() {
                                                                                    @Override
                                                                                    public void capabilities(final WlSeatProxy emitter,
                                                                                                             final int capabilities) {

                                                                                    }

                                                                                    @Override
                                                                                    public void name(final WlSeatProxy emitter,
                                                                                                     final String name) {
                                                                                        System.out.println("Got seat with name " + name);
                                                                                    }
                                                                                });
        }
    }

    private void globalRemove(final WlRegistryProxy wlRegistryProxy,
                              final int i) {

    }

    public void destroy() {
        if (this.shmProxy != null) {
            this.shmProxy.destroy();
        }
        if (this.shellProxy != null) {
            this.shellProxy.destroy();
        }

        this.compositorProxy.destroy();
        this.registryProxy.destroy();
        this.displayProxy.flush();
        this.displayProxy.disconnect();
    }

    public WlDisplayProxy getDisplayProxy() {
        return this.displayProxy;
    }

    public WlShmProxy getShmProxy() {
        return this.shmProxy;
    }

    public WlCompositorProxy getCompositorProxy() {
        return this.compositorProxy;
    }

    public WlSeatProxy getSeatProxy() {
        return this.seatProxy;
    }

    public WlShellProxy getShellProxy() {
        return this.shellProxy;
    }
}
