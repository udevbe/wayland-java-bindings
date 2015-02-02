package examples;

import com.sun.jna.Pointer;

public interface CLibraryMapping {
    public static final String JNA_LIBRARY_NAME = "c";

    int mkstemp(Pointer template);

    int fcntl(int fd, int cmd, int arg);

    Pointer mmap(Pointer addr, int len, int prot, int flags,
               int fildes, int off);

    int munmap(Pointer addr, int len);

    int close(int fildes);

    int ftruncate(int fildes, int length);
}
