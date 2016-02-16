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

import com.github.zubnix.jaccall.CType;
import com.github.zubnix.jaccall.Field;
import com.github.zubnix.jaccall.Struct;
import org.freedesktop.wayland.util.jna.wl_list_Jaccall_StructType;

@Struct({
                @Field(name = "prev",
                       type = CType.POINTER,
                       dataType = wl_list.class),
                @Field(name = "next",
                       type = CType.POINTER,
                       dataType = wl_list.class)
        })
public final class wl_list extends wl_list_Jaccall_StructType {

}
