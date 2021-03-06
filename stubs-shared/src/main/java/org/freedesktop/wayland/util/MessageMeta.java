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
package org.freedesktop.wayland.util;

import org.freedesktop.jaccall.Pointer;
import org.freedesktop.wayland.util.jaccall.wl_interface;
import org.freedesktop.wayland.util.jaccall.wl_message;

import static org.freedesktop.jaccall.Pointer.malloc;
import static org.freedesktop.jaccall.Size.sizeof;

/**
 * Wrapper class for a {@link Message} to create a native wayland message for use with the native library. To create
 * a new native context for a given {@link Message}, use {@link #init(Pointer, Message)} .
 *
 * @see InterfaceMeta
 */
public class MessageMeta {

    public final  Pointer<wl_message> pointer;
    private final Message             message;

    protected MessageMeta(final Pointer<wl_message> pointer,
                          final Message message) {
        this.pointer = pointer;
        this.message = message;
        ObjectCache.store(getNative().address,
                          this);
    }

    public Pointer<wl_message> getNative() {
        return this.pointer;
    }

    public static void init(final Pointer<wl_message> wlMessagePointer,
                            final Message message) {
        //init args interfaces
        final Class<?>[] types = message.types();
        final Pointer<Pointer<wl_interface>> typesPointer = malloc(sizeof((Pointer) null) * types.length,
                                                                   wl_interface.class).castpp();
        for (int i = 0; i < types.length; i++) {
            typesPointer.set(i,
                             InterfaceMeta.get(types[i])
                                          .getNative());
        }

        final wl_message wlMessage = wlMessagePointer.get();

        //set name
        final Pointer<String> namePointer = malloc(sizeof(message.name()),
                                                   String.class);
        namePointer.set(message.name());
        wlMessage.setName(namePointer);

        //set signature
        final Pointer<String> signaturePointer = malloc(sizeof(message.signature()),
                                                        String.class);
        signaturePointer.set(message.signature());
        wlMessage.setSignature(signaturePointer);

        //set types
        wlMessage.setTypes(typesPointer);

        new MessageMeta(wlMessagePointer,
                        message);
    }

    public Message getMessage() {
        return this.message;
    }

    @Override
    public int hashCode() {
        return getNative().hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final MessageMeta messageMeta = (MessageMeta) o;

        return getNative().equals(messageMeta.getNative());
    }
}
