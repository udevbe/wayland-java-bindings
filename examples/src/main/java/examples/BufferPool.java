package examples;


import org.freedesktop.wayland.client.WlBufferProxy;
import org.freedesktop.wayland.client.WlShmPoolEvents;

import java.util.LinkedList;

public class BufferPool implements WlShmPoolEvents{

    private LinkedList<WlBufferProxy> bufferQueue = new LinkedList<WlBufferProxy>();


    public BufferPool() {

    }

    public void queueBuffer(WlBufferProxy buffer){
        this.bufferQueue.add(buffer);
    }

    public WlBufferProxy popBuffer(){
        return bufferQueue.pop();
    }
}
