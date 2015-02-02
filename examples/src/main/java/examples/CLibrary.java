package examples;

public class CLibrary implements CLibraryMapping {


    private static CLibraryMapping INSTANCE;

    public static CLibraryMapping INSTANCE() {
        if (INSTANCE == null) {
            com.sun.jna.Native.register(CLibraryMapping.JNA_LIBRARY_NAME);
            INSTANCE = new CLibrary();
        }
        return INSTANCE;
    }

}
