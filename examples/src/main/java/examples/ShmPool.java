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

import com.sun.jna.*;
import com.sun.jna.Native;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class ShmPool implements Closeable {
    private int        fd;
    private int       size;
    private ByteBuffer buffer;

    private static ByteBuffer map(final int fd,
                                  final int size) throws IOException {
        final ByteBuffer tmpBuff = mapNative(fd,
                                             size);
        tmpBuff.order(ByteOrder.nativeOrder());
        return tmpBuff;
    }

    public ShmPool(final int size) throws IOException {
        this.fd = createTmpFileNative();
        this.size = size;
        try {
            truncateNative(getFd(),
                           getSize());
            this.buffer = map(getFd(),
                              getSize());
        }
        catch (final IOException e) {
            closeNative(getFd());
            throw e;
        }
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

    @Override
    public void close() throws IOException {
        if (this.buffer != null) {
            unmapNative(asByteBuffer());
            closeNative(getFd());
            this.fd = -1;
            this.size = 0;
            this.buffer = null;
        }
    }

    public int getSize() {
        return size;
    }

    public int getFd() {
        return fd;
    }

    @Override
    public void finalize() throws Throwable {
        close();
        super.finalize();
    }

    private static int createTmpFileNative(){
        String template = "/wayland-java-shm-XXXXXX";
        String path = System.getenv("XDG_RUNTIME_DIR");
        if(path == null){
            throw new IllegalStateException("Cannot create temporary file: XDG_RUNTIME_DIR not set");
        }

        String name = path+template;
        Pointer m = new Memory(name.length() + 1); // WARNING: assumes ascii-only string
        m.setString(0, name);
        int fd = CLibrary.INSTANCE().mkstemp(m);

        try {
            int F_GETFD = 1;
            int flags = CLibrary.INSTANCE().fcntl(fd, F_GETFD, 0);
            int FD_CLOEXEC = 1;
            flags |= FD_CLOEXEC;
            int F_SETFD = 2;
            CLibrary.INSTANCE().fcntl(fd, F_SETFD, flags);
            return fd;
        }catch (LastErrorException e){
            CLibrary.INSTANCE().close(fd);
            throw e;
        }
    }

    private static ByteBuffer mapNative(int fd,
                                        int size){
        int PROT_READ = 0x01;
        int PROT_WRITE = 0x02;

        int prot = PROT_READ | PROT_WRITE;
        int MAP_SHARED = 0x001;
        Pointer buffer = CLibrary.INSTANCE().mmap(null, size, prot, MAP_SHARED, fd, 0);
        return buffer.getByteBuffer(0,size);
    }

    private static void unmapNative(ByteBuffer buffer){
        buffer.capacity();
        CLibrary.INSTANCE().munmap(Native.getDirectBufferPointer(buffer), buffer.capacity());
    }

    private static void truncateNative(int fd,
                                       int size){
        CLibrary.INSTANCE().ftruncate(fd, size);
    }

    private static void closeNative(int fd){
        CLibrary.INSTANCE().close(fd);
    }
}

