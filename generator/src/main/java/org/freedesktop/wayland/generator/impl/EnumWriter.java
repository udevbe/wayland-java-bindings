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

import com.squareup.javawriter.JavaWriter;
import org.freedesktop.wayland.util.HasValue;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.Writer;
import java.util.EnumSet;

import static org.freedesktop.wayland.generator.impl.StringUtil.getDoc;
import static org.freedesktop.wayland.generator.impl.StringUtil.getJavaTypeNameEnum;

public class EnumWriter {
    public void write(final Writer writer,
                      final String sharedPackage,
                      final String copyright,
                      final Element interfaceNode,
                      final Element enumNode) throws IOException {
        final JavaWriter javaWriter = new JavaWriter(writer);
        //imports
        javaWriter.emitPackage(sharedPackage)
                  .emitSingleLineComment(copyright.replace("\n",
                                                           "\n//"));
        //class javadoc
        javaWriter.emitJavadoc(getDoc(enumNode));
        //begin type
        javaWriter.beginType(getJavaTypeNameEnum(sharedPackage,
                                                 interfaceNode,
                                                 enumNode),
                             "enum",
                             EnumSet.of(Modifier.PUBLIC),
                             null,
                             HasValue.class.getName());

        //enum values
        //javaWriter.emitEnumValue()
        javaWriter.emitEmptyLine();
        final NodeList enumEntries = enumNode.getElementsByTagName("entry");
        for (int i = 0; i < enumEntries.getLength(); i++) {
            final Element enumEntry = (Element) enumEntries.item(i);
            final String summary = enumEntry.getAttribute("summary");
            String name = enumEntry.getAttribute("name");
            final String value = enumEntry.getAttribute("value");

            if (Character.isDigit(name.charAt(0))) {
                name = "_" + name;
            }

            javaWriter.emitJavadoc(summary)
                      .emitEnumValue(name.toUpperCase() + "(" + value + ")",
                                     (i + 1) == enumEntries.getLength());
        }

        //field
        javaWriter.emitEmptyLine()
                  .emitField(int.class.getName(),
                             "value",
                             EnumSet.of(Modifier.PRIVATE,
                                        Modifier.FINAL));
        //constructor
        javaWriter.emitEmptyLine()
                  .beginConstructor(EnumSet.of(Modifier.PRIVATE),
                                    int.class.getName(),
                                    "value")
                  .emitStatement("this.value = value")
                  .endConstructor();
        //getter
        javaWriter.emitEmptyLine()
                  .beginMethod(int.class.getName(),
                               "getValue",
                               EnumSet.of(Modifier.PUBLIC))
                  .emitStatement("return this.value")
                  .endMethod();

        javaWriter.endType();
    }
}
