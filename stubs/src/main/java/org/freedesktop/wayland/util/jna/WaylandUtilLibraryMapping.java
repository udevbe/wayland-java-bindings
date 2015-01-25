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

import com.sun.jna.Library;
import com.sun.jna.Pointer;

public interface WaylandUtilLibraryMapping extends Library {

    void wl_list_init(Pointer list);

    void wl_list_insert(Pointer list,
                        Pointer elm);

    void wl_list_remove(Pointer elm);

    int wl_list_length(Pointer list);

    int wl_list_empty(Pointer list);

    void wl_list_insert_list(Pointer list,
                             Pointer other);

    void wl_array_init(Pointer array);

    void wl_array_release(Pointer array);

    Pointer wl_array_add(Pointer array,
                         long size);

    int wl_array_copy(Pointer array,
                      Pointer source);

    void free(Pointer pointer);
}
