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

public class wl_list extends Structure {
    /**
     * C type : wl_list*
     */
    public Pointer prev;
    /**
     * C type : wl_list*
     */
    public Pointer next;

    public wl_list() {
        super();
    }

    protected List<?> getFieldOrder() {
        return Arrays.asList("prev",
                             "next");
    }

    public wl_list(final Pointer peer) {
        super(peer);
    }

    public static class ByReference extends wl_list implements Structure.ByReference {

        public ByReference() {
        }

        public ByReference(final Pointer peer) {
            super(peer);
        }
    }

    public static class ByValue extends wl_list implements Structure.ByValue {

        public ByValue() {
        }

        public ByValue(final Pointer peer) {
            super(peer);
        }
    }
}
