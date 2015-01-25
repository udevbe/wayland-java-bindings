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

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class ShmPool implements Closeable {
    private int        fd;
    private long       size;
    private ByteBuffer buffer;

    private ShmPool(final int fd,
                    final long size,
                    final boolean dupFD)
            throws IOException {
        this.fd = fd;
        this.size = size;
        this.buffer = map(fd,
                          size,
                          dupFD);
    }

    private static ByteBuffer map(final int fd,
                                  final long size,
                                  final boolean dupFD) throws IOException {
        final ByteBuffer tmpBuff = mapNative(fd,
                                             size,
                                             dupFD,
                                             false);
        tmpBuff.order(ByteOrder.nativeOrder());
        return tmpBuff;
    }

    public ShmPool(final long size) throws IOException {
        this.fd = createTmpFileNative();
        this.size = size;
        try {
            truncateNative(this.fd,
                           this.size);
            this.buffer = map(this.fd,
                              this.size,
                              false);
        }
        catch (final IOException e) {
            closeNative(this.fd);
            throw e;
        }
    }

    public static ShmPool fromFileDescriptor(final int fd,
                                             final long size,
                                             final boolean dupFD,
                                             final boolean readOnly) throws IOException {
        return new ShmPool(fd,
                           size,
                           dupFD);
    }

    public ByteBuffer asByteBuffer() {
        if (this.buffer == null) {
            throw new IllegalStateException("ShmPool is closed");
        }

        return this.buffer;
    }

    public int getFileDescriptor() {
        return this.fd;
    }

    public long size() {
        return this.size;
    }

    public void resize(final long size,
                       final boolean truncate) throws IOException {
        if (this.buffer == null) {
            throw new IllegalStateException("ShmPool is closed");
        }

        unmapNative(this.buffer);

        this.size = size;
        if (truncate) {
            truncateNative(this.fd,
                           size);
        }

        this.buffer = map(this.fd,
                          size,
                          false);
    }

    public void resize(final long size) throws IOException {
        resize(size,
               true);
    }

    @Override
    public void close() throws IOException {
        if (this.buffer != null) {
            unmapNative(this.buffer);
            this.fd = -1;
            this.size = 0;
            this.buffer = null;
        }
    }

    @Override
    public void finalize() throws Throwable {
        close();
        super.finalize();
    }

    private static native int createTmpFileNative()
            throws IOException;

    private static native ByteBuffer mapNative(int fd,
                                               long size,
                                               boolean dupFD,
                                               boolean readOnly) throws IOException;

    private static native void unmapNative(ByteBuffer buffer)
            throws IOException;

    private static native void truncateNative(int fd,
                                              long size)
            throws IOException;

    private static native void closeNative(int fd)
            throws IOException;
}

