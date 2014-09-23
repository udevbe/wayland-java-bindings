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
import org.freedesktop.wayland.generator.api.Protocols;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("org.freedesktop.wayland.generator.api.Protocols")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ProtocolsProcessor extends AbstractProcessor {
    @Override
    public boolean process(final Set<? extends TypeElement> annotations,
                           final RoundEnvironment roundEnv) {
        for (final Element elem : roundEnv.getElementsAnnotatedWith(Protocols.class)) {
            final Protocols protocols = elem.getAnnotation(Protocols.class);

            final PackageElement packageElement = getPackage(elem);
            for (final Protocol protocol : protocols.value()) {
                try {
                    new ProtocolGenerator().scan(this.processingEnv.getMessager(),
                                                 packageElement,
                                                 this.processingEnv.getFiler(),
                                                 protocol);
                }
                catch (final Exception e) {
                    this.processingEnv.getMessager()
                                      .printMessage(Diagnostic.Kind.ERROR,
                                                    "Got an error while trying to process protocolXML: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private PackageElement getPackage(Element element) {
        while (element.getKind() != ElementKind.PACKAGE) {
            element = element.getEnclosingElement();
        }
        return (PackageElement) element;
    }
}
