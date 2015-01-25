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
package org.freedesktop.wayland.util.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class wl_interface extends Structure {
    /**
     * C type : const char*
     */
    public Pointer                name;
    public int                    version;
    public int                    method_count;
    /**
     * C type : wl_message*
     */
    public wl_message.ByReference methods;
    public int                    event_count;
    /**
     * C type : wl_message*
     */
    public wl_message.ByReference events;

    public wl_interface() {
        super();
    }

    protected List<?> getFieldOrder() {
        return Arrays.asList("name",
                             "version",
                             "method_count",
                             "methods",
                             "event_count",
                             "events");
    }

    /**
     * @param name    C type : const char*<br>
     * @param methods C type : wl_message*<br>
     * @param events  C type : wl_message*
     */
    public wl_interface(final Pointer name,
                        final int version,
                        final int method_count,
                        final wl_message.ByReference methods,
                        final int event_count,
                        final wl_message.ByReference events) {
        super();
        this.name = name;
        this.version = version;
        this.method_count = method_count;
        this.methods = methods;
        this.event_count = event_count;
        this.events = events;
    }

    public wl_interface(final Pointer peer) {
        super(peer);
    }

    protected ByReference newByReference() { return new ByReference(); }

    protected ByValue newByValue() { return new ByValue(); }

    protected wl_interface newInstance() { return new wl_interface(); }

    public static class ByReference extends wl_interface implements Structure.ByReference {
        public ByReference() {
        }

        public ByReference(final Pointer name,
                           final int version,
                           final int method_count,
                           final wl_message.ByReference methods,
                           final int event_count,
                           final wl_message.ByReference events) {
            super(name,
                  version,
                  method_count,
                  methods,
                  event_count,
                  events);
        }

        public ByReference(final Pointer peer) {
            super(peer);
        }
    }

    public static class ByValue extends wl_interface implements Structure.ByValue {
        public ByValue() {
        }

        public ByValue(final Pointer name,
                       final int version,
                       final int method_count,
                       final wl_message.ByReference methods,
                       final int event_count,
                       final wl_message.ByReference events) {
            super(name,
                  version,
                  method_count,
                  methods,
                  event_count,
                  events);
        }

        public ByValue(final Pointer peer) {
            super(peer);
        }
    }
}
