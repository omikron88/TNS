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
import utils.DriveGeom;
import utils.RingBuffer;

/**
 *
 * @author user
 */
public class Wd {

    private Tns m;
    
    private String image[];
    private DriveGeom dg[];
    private int open;
    private DriveGeom d;
    private RandomAccessFile f;
    
    private int trk, sec, ctl, res, dat, stat;
    private int cnt, timeout;
    private int mode, r3212;
    private long position;
    private byte bb[];
    private RingBuffer buff;
    
    private final int IDLE = 0;
    private final int READ = 1;
    private final int WRITE = 2;
    private final int HEAD = 3;
    private final int SEEK = 4;
    
    private final int R_NRDY = 0x80;
    private final int R_WINH = 0x40;
    private final int R_NFND = 0x10;
    private final int R_TR00 = 0x04;
    private final int R_DREQ = 0x02;
    private final int R_BUSY = 0x01;

    Wd(Tns machine) {
        m = machine;
                
        image = new String[4];
        dg = new DriveGeom[4];
        dg[0] = new DriveGeom();
        dg[1] = new DriveGeom();
        dg[2] = new DriveGeom();
        dg[3] = new DriveGeom();
        bb = new byte[1024];
        buff = new RingBuffer(1024);
        
        image[0] = "091.img";
        image[1] = "092.img";
        image[2] = "";
        image[3] = "";
        
        r3212 = 0;
        mode = 0;
        
        reset();
    }
    
    private boolean fileExist(String s) {
        Path p = Paths.get(s);
        return Files.exists(p);
    }
    
    public final void reset() {
        trk = 0;
        sec = 1;
        ctl = 0;
        res = R_TR00;
        dat = 0;
        cnt = 0;
        timeout = 0;
        stat = IDLE;
    }
    
    public boolean insertImage(int index, String fname) {
        if (index==open) {
            return false;
        }
        else {
            image[index-1] = fname;
            return true;
        }
    }
    
    public boolean ejectImage(int index) {
        if (index==open) {
            return false;
        }
        else {
            image[index-1] = "";
            return true;
        }
    }
    
    public String getImage(int index) {
        return image[index-1];
    }
    
    public int isInUse() {
        return open;
    }
    
    public void setTrk(int val) {
        trk = val;
        System.out.println(String.format("Trk: %02X", val));
    }

    public int getTrk() {
       return trk;
    }

    public void setSec(int val) {
        sec = val;
        System.out.println(String.format("Sec: %02X", val));
    }

    public int getSec() {
       return sec;
    }

    public void setCmd(int val) {
        ctl = val;
        System.out.println(String.format("Cmd: %02X (%04X)", val,m.getPC()));
        switch(ctl&0xf0) {
            case 0x00: {
                doSeek(0); 
                break;
            }
            case 0x10: {
                doSeek(getDat()); 
                break;
            }
            case 0x80:
            case 0x90: {
                doRead(); 
                break;
            }
            case 0xc0: {
                doRdHd(); 
                break;
            }
            case 0xd0: {
                doInt(); 
                break;
            }
        }
        timeout = 0;
    }

    public int getRes() {
        state('S');
        System.out.println(String.format("Res: %02X (%04X)", res,m.getPC()));
        return res;
    }
    
    public void setDat(int val) {
        dat = val;
        System.out.println(String.format("ODat: %02X", val));
        cnt++;
        state('W');
        
    }

    public int getDat() {
        dat = bb[cnt] & 0xff;
//        System.out.println(String.format("IDat: %02X (%02X)", dat,cnt));
        cnt++;
        state('R');
        return dat;
    }

    public void setBuf(int val) {
        System.out.println(String.format("OB: %03X", buff.pos()));
        if ((mode&0x02)!=0) {        
            buff.put((byte)(val & 0xff));
        }
    }

    public int getBuf() {
//        System.out.println(String.format("IB: %03X", buff.pos()));
        if ((mode&0x02)!=0) {
            int tmp = buff.get() & 0xff;
            return tmp;
        }
        else {
            return dat;
        }
    }

    public void setMode(int val) {
        mode = val;
        System.out.println(String.format("Mod: %02X (%04X)", val,m.getPC()));
        if ((mode & 0x01)!=0) { buff.reset(); } 
    }

    public void set3212(int val) {
        r3212 = val;
        System.out.println(String.format("Reg: %02X (%04X)", val,m.getPC()));
        if ((r3212&0x04)==0) reset();
        switch(r3212 & 0x6a) {
            case 0x40: {selDrive(1); break;}
            case 0x20: {selDrive(2); break;}
            case 0x08: {selDrive(3); break;}
            case 0x02: {selDrive(4); break;}
            default  : {selDrive(0); break;}
        }
    }

    private void selDrive(int num) {
        m.Led1.setEnabled(num==1);
        m.Led2.setEnabled(num==2);
        m.Led3.setEnabled(num==3);
        m.Led4.setEnabled(num==4);
        if (open!=num) {
            if (open!=0) {
                try {
                    f.close();  
                } catch (IOException ex) {
                    Logger.getLogger(Wd.class.getName()).log(Level.SEVERE, null, ex);
                }
                f = null;
                open = 0;
                d = null;
                }
            if (num!=0) {
                if ((image[num-1].length()>0) && (fileExist(image[num-1]))) {
                    try {
                        f =  new RandomAccessFile(image[num-1], "rw");
                    } catch (IOException ex) {
                        Logger.getLogger(Wd.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (f!=null) {
                        open = num;
                        d = dg[num-1];
                    }
                }
            } 
        }
    }
    
    private boolean find() {
        if ((sec<1) || (sec>d.sectors)) return false; 
        if ((trk<0) || (trk>(d.tracks-1))) return false; 
        
        position = trk * (d.sectors * d.bps);
        position += (sec-1) * d.bps;
        System.out.println(String.format("seek: T%02X S%02X - %08X", trk,sec,position));

        return true;
    }

    private void state(char t) {
        if (stat!=IDLE) {
            timeout++;
            if (timeout>148) {
                stat = IDLE;
                res = 0x00;
            }
        }
        
        if ((stat==READ) && (t=='R')){
            if (cnt==d.bps) {
                cnt = 0;
                if ((ctl&0x10)!=0) {
                    sec++;
                    if (find()==true) {
                        try {
                            f.seek(position);
                            f.read(bb, 0, d.bps);
                            if ((mode&0x08)!=0) { buff.put(bb, d.bps); }
                            timeout = 0;
                        } catch (IOException ex) {
                            Logger.getLogger(Wd.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else {
                        stat = IDLE;
                        res = 0;
                    }
                }
                else {
                    stat = IDLE;
                    res = 0;
                }
            }        
        }
        
        if ((stat==HEAD) && (t=='R')) {
            if (cnt==6) {
                stat = IDLE;
                res = 0;
            }        
        }
        
        if ((stat==SEEK) && (t=='S')) {
            cnt++;
            if (cnt==8) {
                stat = IDLE;
                res = 0;
            }        
        }    

    }
    
    private void doRead() {
        if (open==0) {
            res = R_NRDY;
        }
        else {
            if (find()==true) {
                res = R_BUSY;
                try {
                    f.seek(position);
                    f.read(bb, 0, d.bps);
                    cnt = 0;
                    if ((mode&0x08)!=0) { 
                        buff.put(bb, d.bps);
                        cnt = d.bps;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Wd.class.getName()).log(Level.SEVERE, null, ex);
                }
                stat = READ;
                res |= R_DREQ;
            }
            else {
                res = R_NFND;
            }
        }
    }

    private void doRdHd() {
        if (open==0) {
            res = R_NRDY;
        }
        else {
            if (find()==true) {
                res = R_BUSY;
                bb[0] = (byte) trk;
                bb[1] = (byte) 0;
                bb[2] = (byte) sec;
                bb[3] = (byte) 0;
                bb[4] = (byte) 0x81;
                bb[5] = (byte) 0x82;
                cnt = 0;
                if ((mode&0x08)!=0) { 
                    buff.put(bb, d.bps);
                    cnt = 6;
                }
                stat = HEAD;
                res |= R_DREQ;
            }
            else {
                res = R_NFND;
            }
        }
    }

    private void doSeek(int track) {
        if (open==0) {
            res = R_NRDY;
        }
        else {
            res = R_BUSY;
            stat = SEEK;
            cnt = 0;
            trk = track;
        }
    }

    private void doInt() {
        res = 0;
        stat = IDLE;
    }

}
