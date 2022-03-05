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
public final class Itk {
    
    private final int ofsx = 64;
    private final int ofsy = 0;
        
    private Tns ma;
    private Memory m;
    private BufferedImage i;
    
    private int pal;
    private boolean ir;
    
    Itk(Tns machine, Memory mem) {
        ma = machine;
        m = mem;
        i = ma.getImage();
        ir = false;
        
        reSetPalette();
    }
    
    public void reSetPalette() {
        pal = ma.getConfig().videobw ? 0xffffff : 0x00ff00; //RGB
    }
    
    public void reset() {
        ir = false;
    }
    
    public void vSync() {
        ir = true;
    }
    
    public int getMode() {
        ir = false;         // returns nothing but resets int req
        return 0;
    }
    
    public void setMode(int val) {
        ir = false;         // does nothing but resets int req
    }
    
    public int isInt() {
        boolean tmp = ir;
        ir = false;
        return tmp ? 1:0;
    }
    
    public void paint() {
        int c,d,ad,adr;
        byte b;
        
        adr = 0xFC00;		// vždy pouze stránka 0 !!!! FC00-FFFF 1kB=16*64
        for (int y=0; y<16; y++) {
            for (int x=0; x<64; x++) {
                ad = m.readByte(adr++) & 0xff;
                d = (y * 12) + ofsy;
                for (int a=0; a<12; a++) {
                    c = (x *6) + ofsx;   // celkový rastr je 6x12  obraz je tedy 384x192
                    b = m.readIChar(ad*12+a);   // nutno přidat do memory.java a načíst ze souboru předlohy
                      i.setRGB(c++, d, (b & 0x20) == 0 ? 0 : pal);
                      i.setRGB(c++, d, (b & 0x10) == 0 ? 0 : pal);
                      i.setRGB(c++, d, (b & 0x08) == 0 ? 0 : pal);
                      i.setRGB(c++, d, (b & 0x04) == 0 ? 0 : pal);
                      i.setRGB(c++, d, (b & 0x02) == 0 ? 0 : pal);
                      i.setRGB(c++, d, (b & 0x01) == 0 ? 0 : pal);
                      d++;
                }
            }       
        }
    }
    
}
