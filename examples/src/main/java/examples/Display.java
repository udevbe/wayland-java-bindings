package examples;


import org.freedesktop.wayland.client.*;

public class Display {

    private final WlDisplayProxy  displayProxy;
    private final WlRegistryProxy registryProxy;
    private int shmFormats = 0;

    private WlCompositorProxy compositorProxy;
    private WlShmProxy        shmProxy;


    public Display() {
        displayProxy = WlDisplayProxy.connect("wayland-0");
        registryProxy = displayProxy.getRegistry(new WlRegistryEvents() {
            @Override
            public void global(final WlRegistryProxy emitter,
                               final int name,
                               final String interface_,
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
        displayProxy.roundtrip();

        if (shmProxy == null) {
            throw new NullPointerException("wl_shm not found!");
        }

        displayProxy.roundtrip();
    }

    private void global(final WlRegistryProxy emitter,
                        final int name,
                        final String interface_,
                        final int version) {
        if (WlCompositorProxy.INTERFACE_NAME.equals(interface_)) {
            compositorProxy = registryProxy.<WlCompositorEvents, WlCompositorProxy>bind(name,
                                                                                        WlCompositorProxy.class,
                                                                                        1,
                                                                                        new WlCompositorEvents() {
                                                                                        });
        }
        else if (WlShmProxy.INTERFACE_NAME.equals(interface_)) {
            shmProxy = registryProxy.<WlShmEvents, WlShmProxy>bind(name,
                                                                   WlShmProxy.class,
                                                                   1,
                                                                   new WlShmEvents() {
                                                                       @Override
                                                                       public void format(final WlShmProxy emitter,
                                                                                          final int format) {
                                                                           shmFormats |= (1 << format);
                                                                       }
                                                                   });
        }
    }

    private void globalRemove(final WlRegistryProxy wlRegistryProxy,
                              final int i) {

    }

    public void destroy() {
        if (shmProxy != null) {
            shmProxy.destroy();
        }

        compositorProxy.destroy();
        registryProxy.destroy();
        displayProxy.flush();
        displayProxy.disconnect();
    }

    public WlDisplayProxy getDisplayProxy() {
        return displayProxy;
    }

    public WlShmProxy getShmProxy() {
        return shmProxy;
    }

    public WlCompositorProxy getCompositorProxy() {
        return compositorProxy;
    }
}
