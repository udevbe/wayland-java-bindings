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
package org.freedesktop.wayland.server.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.freedesktop.wayland.util.jaccall.wl_list;

import java.util.Arrays;
import java.util.List;

public class wl_listener extends Structure {
    public wl_list.ByValue  link;
    public wl_notify_func_t notify$;

    public wl_listener() {
        super();
    }

    public wl_listener(final Pointer peer) {
        super(peer);
    }

    protected List<?> getFieldOrder() {
        return Arrays.asList("link",
                             "notify$");
    }

    public static class ByReference extends wl_listener implements Structure.ByReference {
        public ByReference() {
        }

        public ByReference(final Pointer peer) {
            super(peer);
        }
    }

    public static class ByValue extends wl_listener implements Structure.ByValue {
        public ByValue() {
        }

        public ByValue(final Pointer peer) {
            super(peer);
        }
    }
}
