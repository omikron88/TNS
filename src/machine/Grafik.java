/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package machine;

import java.awt.image.BufferedImage;

/**
 *
 * @author user
 */
public final class Grafik {
    
    private final int ofsx = 64;
    private final int ofsy = 0;
    
    private final int pal[] = { // index GRBI
        0x000000, 0x000000, 0x0000cc, 0x0000ff, 0xcc0000, 0xff0000, 0xcc00cc, 0xff00ff,
        0x00cc00, 0x00ff00, 0x00cccc, 0x00ffff, 0xcccc00, 0xffff00, 0xcccccc, 0xffffff,
    };
    
    private Memory m;
    private BufferedImage i; 
    
    private int mode;
    private boolean ir;
    
    Grafik(Memory mem, BufferedImage img) {
        m = mem;
        i = img;
        ir = false;
    }
    
    public void reset() {
        setMode(0);
        ir = false;
    }
    
    public void vSync() {
        ir = true;
    }
    
    public void setMode(int mod) {
        mode = mod;
    }
    
    public int getMode() {
        ir = false;         // returns nothing but resets int req
        return 0;
    }
    
    public int isInt() {
        return ir ? 1:0;
    }
    
    public void paint() {
        if ((mode&0x04)!=0) {
            g0();
        } 
        else {
            if ((mode&0x02)==0) {
                g1();
            }
            else {
                g2();
            }
        }
    }
    
    private void g0() {
        int c,d,ad,adr;
        int ink, pap;
        byte b;
        
        adr = ((mode&0x08)!=0) ? 0x7580 : 0xf580;
        for (int y=0; y<21; y++) {
            for (int x=0; x<64; x++) {
                ad = m.readVram(adr++) & 0xff;
                ink = pal[(ad & 0xf0) >> 4];
                pap = pal[ad & 0x0f];
                ad = ((m.readVram(adr++) & 0xff) << 4)+12;
                d = (y * 12) + ofsy;
                for (int a=0; a<12; a++) {
                    c = (x << 3) + ofsx;
                    b = m.readChar(--ad);
                    i.setRGB(c++, d, (b & 0x80) == 0 ? pap : ink);
                    i.setRGB(c++, d, (b & 0x40) == 0 ? pap : ink);
                    i.setRGB(c++, d, (b & 0x20) == 0 ? pap : ink);
                    i.setRGB(c++, d, (b & 0x10) == 0 ? pap : ink);
                    i.setRGB(c++, d, (b & 0x08) == 0 ? pap : ink);
                    i.setRGB(c++, d, (b & 0x04) == 0 ? pap : ink);
                    i.setRGB(c++, d, (b & 0x02) == 0 ? pap : ink);
                    i.setRGB(c++, d, (b & 0x01) == 0 ? pap : ink);
                    d++;
                }
            }       
        }
    }

    private void g1() {
        
    }

    private void g2() {
        
    }

}
