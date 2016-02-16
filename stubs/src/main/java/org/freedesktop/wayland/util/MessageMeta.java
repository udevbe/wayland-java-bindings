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

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import org.freedesktop.wayland.HasNative;
import org.freedesktop.wayland.util.jaccall.wl_message;

/**
 * Wrapper class for a {@link Message} to create a native wayland message for use with the native library. To create
 * a new native context for a given {@link Message}, use {@link #init(wl_message, Message)}.
 *
 * @see InterfaceMeta
 */
public class MessageMeta implements HasNative<wl_message> {

    private final wl_message pointer;
    private final Message    message;

    private boolean valid;

    protected MessageMeta(final wl_message pointer,
                          final Message message) {
        this.pointer = pointer;
        this.message = message;
        ObjectCache.store(getNative().getPointer(),
                          this);
    }

    public wl_message getNative() {
        return this.pointer;
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }

    public static void init(final wl_message messagePointer,
                            final Message message) {
        //init args interfaces
        final Class<?>[] types               = message.types();
        Pointer          typesPointerPointer = null;
        if (types.length > 0) {
            final Pointer typesPointer = new Memory(Pointer.SIZE * types.length);
            for (int i = 0; i < types.length; i++) {
                typesPointer.setPointer(i * Pointer.SIZE,
                                        InterfaceMeta.get(types[i])
                                                     .getNative()
                                                     .getPointer());
            }
            typesPointerPointer = typesPointer;
        }
        //set name
        final Pointer m = new Memory(message.name()
                                            .length() + 1);
        m.setString(0,
                    message.name());
        messagePointer.writeField("name",
                                  m);
        //set signature
        final Pointer s = new Memory(message.signature()
                                            .length() + 1);
        s.setString(0,
                    message.signature());
        messagePointer.writeField("signature",
                                  s);
        //set types
        messagePointer.writeField("types",
                                  typesPointerPointer);

        new MessageMeta(messagePointer,
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

    @Override
    protected void finalize() throws Throwable {
        this.valid = false;
        super.finalize();
    }
}
