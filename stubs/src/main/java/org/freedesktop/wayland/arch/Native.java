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
package org.freedesktop.wayland.arch;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Native {
    private static final String      IO_TEMP_DIR = System.getProperty("java.io.tmpdir");
    private static final Set<String> libsLoaded  = new HashSet<String>();

    private Native() { }

    public static void loadLibrary(String libName) {
        libName = "lib" + libName + ".so";
        if (libsLoaded.contains(libName)) {
            // avoid loading native libs double.
            return;
        }

        final InputStream in = Native.class.getClassLoader()
                                           .getResourceAsStream(libName);
        if (in == null) {
            throw new IllegalArgumentException("Native library not found on classloader classpath: " + libName);
        }

        final File temp = new File(new File(IO_TEMP_DIR),
                                   libName);

        try {
            final byte[] buffer = new byte[4096];
            int read;
            final FileOutputStream fos = new FileOutputStream(temp);
            while ((read = in.read(buffer)) != -1) {
                fos.write(buffer,
                          0,
                          read);
            }
            fos.close();
            in.close();

            System.load(temp.getAbsolutePath());
            libsLoaded.add(libName);
        }
        catch (final FileNotFoundException e) {
            throw new Error(e);
        }
        catch (final IOException e) {
            throw new Error(e);
        }
    }
}

