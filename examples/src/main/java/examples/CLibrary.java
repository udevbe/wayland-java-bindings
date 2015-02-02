package examples;

import com.sun.jna.LastErrorException;
import com.sun.jna.Pointer;

public class CLibrary implements CLibraryMapping {

    private static CLibraryMapping INSTANCE;

    public static CLibraryMapping INSTANCE() {
        if (INSTANCE == null) {
            com.sun.jna.Native.register(CLibraryMapping.JNA_LIBRARY_NAME);
            INSTANCE = new CLibrary();
        }
        return INSTANCE;
    }

    @Override
    public native int mkstemp(final Pointer template) throws LastErrorException;

    @Override
    public native int fcntl(final int fd, final int cmd, final int arg) throws LastErrorException;

    @Override
    public native Pointer mmap(final Pointer addr, final int len, final int prot, final int flags, final int fildes,
                        final int off) throws LastErrorException;

    @Override
    public native int munmap(final Pointer addr, final int len) throws LastErrorException;

    @Override
    public native int close(final int fildes) throws LastErrorException;

    @Override
    public native int ftruncate(final int fildes, final int length) throws LastErrorException;
}
