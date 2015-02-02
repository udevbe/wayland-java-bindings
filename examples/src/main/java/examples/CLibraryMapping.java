package examples;

import com.sun.jna.LastErrorException;
import com.sun.jna.Pointer;

public interface CLibraryMapping {

    public static final String JNA_LIBRARY_NAME = "c";

    int mkstemp(Pointer template) throws LastErrorException;

    int fcntl(int fd, int cmd, int arg) throws LastErrorException;

    Pointer mmap(Pointer addr, int len, int prot, int flags,
                 int fildes, int off) throws LastErrorException;

    int munmap(Pointer addr, int len) throws LastErrorException;

    int close(int fildes) throws LastErrorException;

    int ftruncate(int fildes, int length) throws LastErrorException;
}
