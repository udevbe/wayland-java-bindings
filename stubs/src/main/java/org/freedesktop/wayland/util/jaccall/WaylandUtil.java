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
package org.freedesktop.wayland.util.jaccall;


import com.github.zubnix.jaccall.Ptr;

public interface WaylandUtil {

    void wl_list_init(@Ptr(wl_list.class) long list);

    void wl_list_insert(@Ptr(wl_list.class) long list,
                        @Ptr(wl_list.class) long elm);

    void wl_list_remove(@Ptr(wl_list.class) long elm);

    int wl_list_length(@Ptr(wl_list.class) long list);

    int wl_list_empty(@Ptr(wl_list.class) long list);

    void wl_list_insert_list(@Ptr(wl_list.class) long list,
                             @Ptr(wl_list.class) long other);

    void wl_array_init(@Ptr(wl_array.class) long array);

    void wl_array_release(@Ptr(wl_array.class) long array);

    void wl_array_add(@Ptr(wl_array.class) long array,
                      long size);

    int wl_array_copy(@Ptr(wl_array.class) long array,
                      @Ptr(wl_array.class) long source);

    void free(@Ptr long pointer);
}
