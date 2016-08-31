package org.freedesktop.wayland.server.jaccall;

import org.freedesktop.jaccall.CType;
import org.freedesktop.jaccall.Field;
import org.freedesktop.jaccall.Struct;
import org.freedesktop.wayland.util.jaccall.wl_list;

@Struct({
                @Field(name = "listener_list",
                       type = CType.STRUCT,
                       dataType = wl_list.class)
        })
public final class wl_signal extends wl_signal_Jaccall_StructType {
}
