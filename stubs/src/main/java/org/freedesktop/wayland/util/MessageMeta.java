/*
 * Copyright © 2014 Erik De Rijcke
 *
 * Permission to use, copy, modify, distribute, and sell this software and its
 * documentation for any purpose is hereby granted without fee, provided that
 * the above copyright notice appear in all copies and that both that copyright
 * notice and this permission notice appear in supporting documentation, and
 * that the name of the copyright holders not be used in advertising or
 * publicity pertaining to distribution of the software without specific,
 * written prior permission.  The copyright holders make no representations
 * about the suitability of this software for any purpose.  It is provided "as
 * is" without express or implied warranty.
 *
 * THE COPYRIGHT HOLDERS DISCLAIM ALL WARRANTIES WITH REGARD TO THIS SOFTWARE,
 * INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS, IN NO
 * EVENT SHALL THE COPYRIGHT HOLDERS BE LIABLE FOR ANY SPECIAL, INDIRECT OR
 * CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE,
 * DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 * TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE
 * OF THIS SOFTWARE.
 */
package org.freedesktop.wayland.util;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import org.freedesktop.wayland.HasNative;
import org.freedesktop.wayland.util.jna.wl_message;

/**
 * Wrapper class for a {@link Message} to create a native wayland message for use with the native library. To create
 * a new native context for a given {@link Message}, use {@link #init(org.freedesktop.wayland.util.jna.wl_message, Message)}.
 *
 * @see InterfaceMeta
 */
public class MessageMeta implements HasNative<wl_message> {

    private final wl_message pointer;
    private final Message    message;

    protected MessageMeta(final wl_message pointer,
                          final Message message) {
        this.pointer = pointer;
        this.message = message;
        ObjectCache.store(Pointer.nativeValue(getNative().getPointer()),
                          this);
    }

    public static void init(final wl_message messagePointer,
                            final Message message) {
        //init args interfaces
        final Class<?>[] types = message.types();
        Pointer typesPointerPointer = null;
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

    public wl_message getNative() {
        return this.pointer;
    }

    public Message getMessage() {
        return this.message;
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
    public int hashCode() {
        return getNative().hashCode();
    }
}
