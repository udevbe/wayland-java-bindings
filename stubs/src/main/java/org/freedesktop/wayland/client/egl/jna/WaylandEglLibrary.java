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
package org.freedesktop.wayland.client.egl.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public interface WaylandEglLibrary extends Library {
    public static final String            JNA_LIBRARY_NAME = "wayland-egl";
    public static final NativeLibrary     JNA_NATIVE_LIB   = NativeLibrary.getInstance(WaylandEglLibrary.JNA_LIBRARY_NAME);
    public static final WaylandEglLibrary INSTANCE         = (WaylandEglLibrary) Native.loadLibrary(WaylandEglLibrary.JNA_LIBRARY_NAME,
                                                                                                    WaylandEglLibrary.class);

    Pointer wl_egl_window_create(Pointer surface,
                                 int width,
                                 int height);

    void wl_egl_window_destroy(Pointer egl_window);

    void wl_egl_window_resize(Pointer egl_window,
                              int width,
                              int height,
                              int dx,
                              int dy);

    void wl_egl_window_get_attached_size(Pointer egl_window,
                                         IntByReference width,
                                         IntByReference height);


}

