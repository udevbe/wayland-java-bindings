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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.PackageElement;
import javax.tools.Diagnostic;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class ProtocolGenerator {

    public void scan(final Messager messager,
                     final PackageElement packageElement,
                     final Filer filer,
                     final Protocol protocol) throws IOException, SAXException, ParserConfigurationException {

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        if (protocol.dtd()) {
            factory.setValidating(true);
        }
        final DocumentBuilder xmlBuilder = factory.newDocumentBuilder();
        xmlBuilder.setErrorHandler(
                new ErrorHandler() {
                    public void warning(final SAXParseException e) throws SAXException {
                        messager.printMessage(Diagnostic.Kind.WARNING,
                                              e.getMessage());
                    }

                    public void error(final SAXParseException e) throws SAXException {
                        messager.printMessage(Diagnostic.Kind.ERROR,
                                              e.getMessage());
                        throw e;
                    }

                    public void fatalError(final SAXParseException e) throws SAXException {
                        messager.printMessage(Diagnostic.Kind.ERROR,
                                              e.getMessage());
                        throw e;
                    }
                });
        final Document doc = xmlBuilder.parse(new File(protocol.path()));
        doc.getDocumentElement()
           .normalize();

        final DocumentTraversal docTraversal = (DocumentTraversal) doc;
        final TreeWalker treeWalker = docTraversal.createTreeWalker(doc.getDocumentElement(),
                                                                    NodeFilter.SHOW_ALL,
                                                                    null,
                                                                    false);
        final Element protocolElement = (Element) treeWalker.getRoot();
        new ProtocolWriter().write(packageElement,
                                   filer,
                                   protocol,
                                   protocolElement);
    }
}

