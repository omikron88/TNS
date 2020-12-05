/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author Administrator
 */
public class RingBuffer {
    
    private byte buff[];
    private int size;
    private int pos;
    
    public RingBuffer(int size) {
        this.buff = new byte[size];
        this.size = size;
        this.pos = 0;
    }
    
    public void reset() {
        pos = 0;
    }
    
    public int pos() {
        return pos;    
    }
    
    public void put(byte b) {
        buff[pos] = b;
        pos++;
        if (pos==size) {
            pos = 0;
        }
    }
    
    public byte get() {
        byte b = buff[pos];
        pos++;
        if (pos==size) {
            pos = 0;
        }
        return b;
    }
    
    public void put(byte b[], int num) {
        for(int n=0; n<num; n++) {
            put(b[n]);
        }
    }
    
    public void get(byte b[], int num) {
        for(int n=0; n<num; n++) {
            b[n] = get();
        }
    }

}
