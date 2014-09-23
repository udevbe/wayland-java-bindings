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

/**
 * Wrapper class for a {@link Message} to create a native wayland message for use with the native library. To create
 * a new native context for a given {@link Message}, use {@link #create(Message)}.
 *
 * @see InterfaceMeta
 */
public class MessageMeta implements HasPointer {

    private final long    pointer;
    private final Message message;

    protected MessageMeta(final long pointer,
                          final Message message) {
        this.pointer = pointer;
        this.message = message;
        ObjectCache.store(getPointer(),
                          this);
    }

    public static void init(long messagePointerStart,
                            int index,
                            final Message message) {
        final Class<?>[] types = message.types();
        final long[] typeInterfacePointers = new long[types.length];
        for (int i = 0; i < typeInterfacePointers.length; i++) {
            typeInterfacePointers[i] = InterfaceMeta.get(types[i])
                                                    .getPointer();
        }
        new MessageMeta(WlUtilJNI.initMessage(messagePointerStart,
                                              index,
                                              message.name(),
                                              message.signature(),
                                              typeInterfacePointers),
                        message);
    }

    public long getPointer() {
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

        return getPointer() == messageMeta.getPointer();
    }

    @Override
    public int hashCode() {
        return (int) getPointer();
    }
}