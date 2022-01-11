/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package machine;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class Wap {

    private Tns m;
    private Config cfg;
    
    private String image;
    private RandomAccessFile f;
    
    private byte[] cmd;
    private byte bb[];

    private int del,pos,cpos,st,sek;
    private boolean exec;
    
    Wap(Tns machine) {
        m = machine;
        cfg = m.getConfig();
    
        cmd = new byte[5];
        bb = new byte[128];

        image = cfg.hdd;

        reset();
    }
    
    private boolean fileExist(String s) {
        Path p = Paths.get(s);
        return Files.exists(p);
    }
    
    public final void reset() {
        pos = 0;
        cpos = 0;
        del = 0;
        exec = false;
        
        if (fileExist(cfg.hdd)) {
            insertImage(cfg.hdd);
        }
    }
    
    public int isRInt() {
        return (f!=null) ? 1 : 0;
    }
    
    public int isWInt() {
        return (f!=null) ? 1 : 0;
    }

    public boolean insertImage(String fname) {
        image = fname;
        if (f!=null) {
            try {
                f.close();  
            } catch (IOException ex) {
                Logger.getLogger(Wap.class.getName()).log(Level.SEVERE, null, ex);
            }
            f = null;
        }
        if ((image.length()>0) && (fileExist(image))) {
            try {
                f =  new RandomAccessFile(image, "rw");
            } catch (IOException ex) {
                Logger.getLogger(Wap.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }
    
    public boolean ejectImage() {
        image = "";
        if (f!=null) {
            try {
                f.close();  
            } catch (IOException ex) {
                Logger.getLogger(Wap.class.getName()).log(Level.SEVERE, null, ex);
            }
            f = null;
        }
        return true;
    }
    
    public String getImage() {
        return image;
    }
    
    public void wapOut(int val) {
//        System.out.println(String.format("wapO: %02X", val));
        
        if (f!=null) {
            if (exec) {
                if (del > 0) {
                    bb[pos] = (byte) val; 
                    pos++;
                    del--;
                    if (del==0) {
                        exec = false;
                        if (cmd[0]==4) {
                            wrSec(64*128*st+128*sek);
                        }
                    }
                }
                else {
                    exec = false;
                }
            }
            else {
                cmd[cpos] = (byte) val;
                cpos++;
                st=Byte.toUnsignedInt(cmd[2])+256*Byte.toUnsignedInt(cmd[3]);
                sek=cmd[4];
                if (cmd[0] == 1) {
                    cpos=0;
                }
                if ((cpos==5)&&(cmd[0]==3)) {
                    del=128;
                    pos=0;
                    rdSec(64*128*st+128*sek);
                    exec = true;
                }
                if ((cpos==5)&&(cmd[0]==4)) {
                    del=128;
                    pos=0;
                    exec = true;
                }
                if (cpos>4) {
                    cpos=0;
                }
            }
        }     
    }
    
    public int wapIn() {
        int val = 0;
        
        if (f!=null) {
            if (del > 0) { 
                val = bb[pos] & 0xff;
                pos++;
                del--;
            }
            else {
                exec=false;
            }
        }
        
//        System.out.println(String.format("WapI: %02X", val));
        return val;
    }
    
    private void rdSec(long position) {
        try {
            f.seek(position);
            f.read(bb, 0, 128);
        } catch (IOException ex) {
            Logger.getLogger(Wap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void wrSec(long position) {
        try {
            f.seek(position);
            f.write(bb, 0, 128);
        } catch (IOException ex) {
            Logger.getLogger(Wap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
