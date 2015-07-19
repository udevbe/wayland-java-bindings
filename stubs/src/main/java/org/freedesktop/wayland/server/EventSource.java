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
package org.freedesktop.wayland.server;

import com.sun.jna.Pointer;
import org.freedesktop.wayland.HasNative;
import org.freedesktop.wayland.server.jna.WaylandServerLibrary;
import org.freedesktop.wayland.util.ObjectCache;

public class EventSource implements HasNative<Pointer> {

    private final Pointer pointer;

    private boolean valid;

    protected EventSource(final Pointer pointer) {
        this.pointer = pointer;
        this.valid = true;
        ObjectCache.store(getNative(),
                          this);
    }

    public static EventSource create(final Pointer pointer) {
        return new EventSource(pointer);
    }

    public int updateFileDescriptor(final int mask) {
        return WaylandServerLibrary.INSTANCE()
                                   .wl_event_source_fd_update(getNative(),
                                                              mask);
    }

    public int updateTimer(final int msDelay) {
        return WaylandServerLibrary.INSTANCE()
                                   .wl_event_source_timer_update(getNative(),
                                                                 msDelay);
    }

    public int remove() {
        if (this.valid) {
            this.valid = false;
            ObjectCache.remove(getNative());
            return WaylandServerLibrary.INSTANCE()
                                       .wl_event_source_remove(getNative());
        }
        return 0;
    }

    public void check() {
        WaylandServerLibrary.INSTANCE()
                            .wl_event_source_check(getNative());
    }

    public Pointer getNative() {
        return this.pointer;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final EventSource that = (EventSource) o;

        return getNative().equals(that.getNative());

    }

    @Override
    public int hashCode() {
        return getNative().hashCode();
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }

    @Override
    protected void finalize() throws Throwable {
        remove();
        super.finalize();
    }
}
