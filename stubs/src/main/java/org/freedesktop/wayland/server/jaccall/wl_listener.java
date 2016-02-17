package org.freedesktop.wayland.server.jaccall;

import com.github.zubnix.jaccall.CType;
import com.github.zubnix.jaccall.Field;
import com.github.zubnix.jaccall.Struct;
import org.freedesktop.wayland.util.jaccall.wl_list;

@Struct({
                @Field(name = "link",
                       type = CType.STRUCT,
                       dataType = wl_list.class),
                @Field(name = "notify$",
                       type = CType.POINTER,
                       dataType = wl_notify_func_t.class)
        })
public final class wl_listener extends wl_listener_Jaccall_StructType {
}
