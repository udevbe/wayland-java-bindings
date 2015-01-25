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
package examples;

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

