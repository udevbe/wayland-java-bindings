package examples;


import org.freedesktop.wayland.client.*;

import javax.annotation.Nonnull;

public class Display {

    private final WlDisplayProxy  displayProxy;
    private final WlRegistryProxy registryProxy;
    private int shmFormats = 0;

    private WlCompositorProxy compositorProxy;
    private WlShellProxy      shellProxy;
    private WlShmProxy        shmProxy;


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
                                    interface_,
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
                        final String interface_,
                        final int version) {
        if (WlCompositorProxy.INTERFACE_NAME.equals(interface_)) {
            this.compositorProxy = this.registryProxy.<WlCompositorEvents, WlCompositorProxy>bind(name,
                                                                                             WlCompositorProxy.class,
                                                                                             1,
                                                                                             new WlCompositorEvents() {
                                                                                             });
        }
        else if (WlShellProxy.INTERFACE_NAME.equals(interface_)) {
            this.shellProxy = this.registryProxy.<WlShellEvents, WlShellProxy>bind(name,
                                                                              WlShellProxy.class,
                                                                              1,
                                                                              new WlShellEvents() {
                                                                              });
        }
        else if (WlShmProxy.INTERFACE_NAME.equals(interface_)) {
            this.shmProxy = this.registryProxy.<WlShmEvents, WlShmProxy>bind(name,
                                                                        WlShmProxy.class,
                                                                        1,
                                                                        new WlShmEvents() {
                                                                            @Override
                                                                            public void format(final WlShmProxy emitter,
                                                                                               final int format) {
                                                                                Display.this.shmFormats |= (1 << format);
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

    public WlShellProxy getShellProxy() {
        return this.shellProxy;
    }
}
