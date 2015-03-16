package examples;


import org.freedesktop.wayland.client.WlBufferProxy;
import org.freedesktop.wayland.client.WlShmPoolEvents;

import java.util.LinkedList;

public class BufferPool implements WlShmPoolEvents{

    private LinkedList<WlBufferProxy> bufferQueue = new LinkedList<WlBufferProxy>();
    private boolean destroyed;


    public BufferPool() {
    }

    public void queueBuffer(WlBufferProxy buffer){
        if(destroyed){
            buffer.destroy();
        }else {
            this.bufferQueue.add(buffer);
        }
    }

    public WlBufferProxy popBuffer(){
        return bufferQueue.pop();
    }

    public void destroy(){
        for (WlBufferProxy wlBufferProxy : bufferQueue) {
            wlBufferProxy.destroy();
        }
        this.destroyed = true;
    }
}
