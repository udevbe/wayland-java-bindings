package org.freedesktop.wayland.server;

public class ClientCredentials {
    private final int pid;
    private final int uid;
    private final int gid;

    public ClientCredentials(final int pid,
                             final int uid,
                             final int gid) {
        this.pid = pid;
        this.uid = uid;
        this.gid = gid;
    }

    public int getPid() {
        return this.pid;
    }

    public int getUid() {
        return this.uid;
    }

    public int getGid() {
        return this.gid;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        final ClientCredentials that = (ClientCredentials) o;

        return this.pid == that.pid && this.uid == that.uid && this.gid == that.gid;
    }

    @Override
    public int hashCode() {
        int result = this.pid;
        result = 31 * result + this.uid;
        result = 31 * result + this.gid;
        return result;
    }
}
