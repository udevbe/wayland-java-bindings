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
import java.io.Writer;

import static org.freedesktop.wayland.generator.impl.StringUtil.getJavaTypeNameEnum;
import static org.freedesktop.wayland.generator.impl.StringUtil.getJavaTypeNameResource;

/**
 * Created by Erik De Rijcke on 7/25/14.
 */
public class InterfaceWriter {

    public static final String ELEMENT = "interface";

    public static final String ATTRIBUTE_NAME = "name";


    public void write(final PackageElement packageElement,
                      final Filer filer,
                      final Protocol protocol,
                      final String copyright,
                      final Element interfaceElement) throws IOException {
        final String packageRoot = packageElement.getQualifiedName()
                                                 .toString();
        writeShared(packageElement,
                    filer,
                    packageRoot + "." + protocol.sharedPackage(),
                    copyright,
                    interfaceElement);
        if (protocol.generateServer()) {
            writeServer(packageElement,
                        filer,
                        packageRoot + "." + protocol.serverPackage(),
                        copyright,
                        interfaceElement);
        }
        if (protocol.generateClient()) {
            writeClient(packageElement,
                        filer,
                        packageRoot + "." + protocol.clientPackage(),
                        copyright,
                        interfaceElement);
        }
    }

    private void writeShared(final PackageElement packageElement,
                             final Filer filer,
                             final String sharedPackage,
                             final String copyright,
                             final Element interfaceElement) throws IOException {
        final NodeList enums = interfaceElement.getElementsByTagName("enum");
        for (int i = 0; i < enums.getLength(); i++) {
            final Element enumElement = (Element) enums.item(i);
            writeEnum(filer.createSourceFile(getJavaTypeNameEnum(sharedPackage,
                                                                 interfaceElement,
                                                                 enumElement),
                                             packageElement)
                           .openWriter(),
                      sharedPackage,
                      copyright,
                      interfaceElement,
                      enumElement);
        }
    }

    private void writeEnum(final Writer writer,
                           final String sharedPackage,
                           final String copyright,
                           final Element interfaceElement,
                           final Element enumElement) throws IOException {
        new EnumWriter().write(writer,
                               sharedPackage,
                               copyright,
                               interfaceElement,
                               enumElement);
        writer.close();
    }

    private void writeServer(final PackageElement packageElement,
                             final Filer filer,
                             final String serverPackage,
                             final String copyright,
                             final Element interfaceNode) throws IOException {
        writeRequests(filer,
                      serverPackage,
                      copyright,
                      interfaceNode);

        writeResource(filer.createSourceFile(getJavaTypeNameResource(serverPackage,
                                                                     interfaceNode,
                                                                     1),
                                             packageElement)
                           .openWriter(),
                      serverPackage,
                      copyright,
                      interfaceNode);
    }

    private void writeRequests(final Filer filer,
                               final String serverPackage,
                               final String copyright,
                               final Element interfaceNode) throws IOException {
        new RequestsWriter().write(filer,
                                   serverPackage,
                                   copyright,
                                   interfaceNode);
    }

    private void writeResource(final Writer writer,
                               final String serverPackage,
                               final String copyright,
                               final Element interfaceNode) throws IOException {
        new ResourceWriter().write(writer,
                                   serverPackage,
                                   copyright,
                                   interfaceNode);
        writer.close();
    }

    private void writeClient(final PackageElement packageElement,
                             final Filer filer,
                             final String clientPackage,
                             final String copyright,
                             final Element interfaceXMLNode) throws IOException {
        writeProxy(filer.createSourceFile(StringUtil.getJavaTypeNameProxy(clientPackage,
                                                                          interfaceXMLNode,
                                                                          1),
                                          packageElement)
                        .openWriter(),
                   clientPackage,
                   copyright,
                   interfaceXMLNode);

        writeEvents(filer,
                    clientPackage,
                    copyright,
                    interfaceXMLNode);
    }

    private void writeEvents(final Filer filer,
                             final String clientPackage,
                             final String copyright,
                             final Element interfaceXMLNode) throws IOException {
        new EventsWriter().write(filer,
                                 clientPackage,
                                 copyright,
                                 interfaceXMLNode);
    }

    private void writeProxy(final Writer writer,
                            final String clientPackage,
                            final String copyright,
                            final Element interfaceXMLNode) throws IOException {
        new ProxyWriter().write(writer,
                                clientPackage,
                                copyright,
                                interfaceXMLNode);
        writer.close();
    }
}
