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

import org.freedesktop.jaccall.CType;
import org.freedesktop.jaccall.Field;
import org.freedesktop.jaccall.Struct;

@Struct({
                @Field(name = "name",
                       type = CType.POINTER,
                       dataType = String.class),
                @Field(name = "version",
                       type = CType.INT),
                @Field(name = "method_count",
                       type = CType.INT),
                @Field(name = "methods",
                       type = CType.POINTER,
                       dataType = wl_message.class),
                @Field(name = "event_count",
                       type = CType.INT),
                @Field(name = "events",
                       type = CType.POINTER,
                       dataType = wl_message.class)
        })
public final class wl_interface extends wl_interface_Jaccall_StructType {

}
