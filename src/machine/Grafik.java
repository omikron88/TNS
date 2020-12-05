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
    private final int ofsy = 2;
    
    private final int pal[] = {
        0x000000, 0x000088, 0x008800, 0x008888, 0x880000, 0x880088, 0x888800, 0x888888,
        0xaaaaaa, 0x0000ff, 0x00ff00, 0x00ffff, 0xff0000, 0xff00ff, 0xffff00, 0xffffff
    };
    
    private Memory m;
    private BufferedImage i; 
    
    Grafik(Memory mem, BufferedImage img) {
        m = mem;
        i = img;
    }
    
    void g0() {
        int c,d,ad,adr;
        int ink, pap;
        byte b;
        
        adr = 0x7580;
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

}
