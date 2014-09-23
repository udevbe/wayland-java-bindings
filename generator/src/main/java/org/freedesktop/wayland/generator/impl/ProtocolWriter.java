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
package org.freedesktop.wayland.generator.impl;

import org.freedesktop.wayland.generator.api.Protocol;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.annotation.processing.Filer;
import javax.lang.model.element.PackageElement;
import java.io.IOException;

public class ProtocolWriter {

    public static final String ELEMENT_COPYRIGHT = "copyright";

    private static final String ELEMENT_PROTOCOL_ATTRIBUTE_NAME = "name";

    public void write(final PackageElement packageElement,
                      final Filer filer,
                      final Protocol protocol,
                      final Element protocolElement) throws IOException {
        //TODO do something with name?
        final String name = protocolElement.getAttribute(ELEMENT_PROTOCOL_ATTRIBUTE_NAME);

        String copyright = "";
        final NodeList copyrightElements = protocolElement.getElementsByTagName(ELEMENT_COPYRIGHT);
        for (int i = 0; i < copyrightElements.getLength(); i++) {
            final Element copyrightElement = (Element) copyrightElements.item(i);
            copyright += "\n" + copyrightElement.getTextContent();
        }

        final NodeList interfaceElements = protocolElement.getElementsByTagName(InterfaceWriter.ELEMENT);
        for (int i = 0; i < interfaceElements.getLength(); i++) {
            final Element interfaceElement = (Element) interfaceElements.item(i);
            new InterfaceWriter().write(packageElement,
                                        filer,
                                        protocol,
                                        copyright,
                                        interfaceElement);
        }
    }
}
