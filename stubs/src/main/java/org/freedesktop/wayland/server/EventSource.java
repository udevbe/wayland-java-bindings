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

import org.freedesktop.wayland.server.jaccall.WaylandServerCore;

public class EventSource {

    private final long pointer;

    protected EventSource(final long pointer) {
        this.pointer = pointer;
    }

    public static EventSource create(final long pointer) {
        return new EventSource(pointer);
    }

    public int updateFileDescriptor(final int mask) {
        return WaylandServerCore.INSTANCE()
                                .wl_event_source_fd_update(this.pointer,
                                                           mask);
    }

    public int updateTimer(final int msDelay) {
        return WaylandServerCore.INSTANCE()
                                .wl_event_source_timer_update(this.pointer,
                                                              msDelay);
    }

    public void check() {
        WaylandServerCore.INSTANCE()
                         .wl_event_source_check(this.pointer);
    }

    @Override
    public int hashCode() {
        return new Long(this.pointer).hashCode();
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

        return this.pointer == that.pointer;

    }

    public int remove() {
        return WaylandServerCore.INSTANCE()
                                .wl_event_source_remove(this.pointer);
    }
}
