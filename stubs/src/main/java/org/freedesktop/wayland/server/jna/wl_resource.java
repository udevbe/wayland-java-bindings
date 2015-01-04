package org.freedesktop.wayland.server.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.freedesktop.wayland.util.jna.wl_list;
import org.freedesktop.wayland.util.jna.wl_object;

import java.util.Arrays;
import java.util.List;

public class wl_resource extends Structure {
    public wl_object                  object;
    public wl_resource_destroy_func_t destroy;
    public wl_list                    link;
    public wl_signal                  destroy_signal;
    public wl_client                  client;
    public Pointer                    data;

    public wl_resource() {
        super();
    }

    protected List<?> getFieldOrder() {
        return Arrays.asList("object",
                             "destroy",
                             "link",
                             "destroy_signal",
                             "client",
                             "data");
    }

    public wl_resource(final wl_object object,
                       final wl_resource_destroy_func_t destroy,
                       final wl_list link,
                       final wl_signal destroy_signal,
                       final wl_client client,
                       final Pointer data) {
        super();
        this.object = object;
        this.destroy = destroy;
        this.link = link;
        this.destroy_signal = destroy_signal;
        this.client = client;
        this.data = data;
    }

    public wl_resource(final Pointer peer) {
        super(peer);
    }

    protected ByReference newByReference() { return new ByReference(); }

    protected ByValue newByValue() { return new ByValue(); }

    protected wl_resource newInstance() { return new wl_resource(); }

    public static class ByReference extends wl_resource implements Structure.ByReference {
        public ByReference() {
        }

        public ByReference(final wl_object object,
                           final wl_resource_destroy_func_t destroy,
                           final wl_list link,
                           final wl_signal destroy_signal,
                           final wl_client client,
                           final Pointer data) {
            super(object,
                  destroy,
                  link,
                  destroy_signal,
                  client,
                  data);
        }

        public ByReference(final Pointer peer) {
            super(peer);
        }
    }

    public static class ByValue extends wl_resource implements Structure.ByValue {
        public ByValue() {
        }

        public ByValue(final wl_object object,
                       final wl_resource_destroy_func_t destroy,
                       final wl_list link,
                       final wl_signal destroy_signal,
                       final wl_client client,
                       final Pointer data) {
            super(object,
                  destroy,
                  link,
                  destroy_signal,
                  client,
                  data);
        }

        public ByValue(final Pointer peer) {
            super(peer);
        }
    }
}
