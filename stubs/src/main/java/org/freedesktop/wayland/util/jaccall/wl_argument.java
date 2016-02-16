package org.freedesktop.wayland.util.jaccall;

import com.github.zubnix.jaccall.CType;
import com.github.zubnix.jaccall.Field;
import com.github.zubnix.jaccall.Struct;

@Struct(union = true,
        value = {
                @Field(name = "i",
                       type = CType.INT),
                @Field(name = "u",
                       type = CType.INT),
                @Field(name = "f",
                       type = CType.INT),
                @Field(name = "s",
                       type = CType.POINTER,
                       dataType = String.class),
                @Field(name = "o",
                       type = CType.POINTER),
                @Field(name = "n",
                       type = CType.INT),
                @Field(name = "a",
                       type = CType.POINTER,
                       dataType = wl_array.class),
                @Field(name = "h",
                       type = CType.INT)
        })
public final class wl_argument extends wl_argument_Jaccall_StructType {
}
