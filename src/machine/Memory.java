/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package machine;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Memory {

    public final int PAGE_SIZE = 8192;
    public final int PAGE_MASK = PAGE_SIZE - 1;
    public final byte PAGE_BIT = 13;
    
    public int rampages; 
    
    private byte[][] Ram = new byte[128][PAGE_SIZE];
    private byte[][] VRam = new byte[8][PAGE_SIZE];
    private byte[][] BootG = new byte[1][PAGE_SIZE];
    private byte[][] BootS = new byte[1][PAGE_SIZE];
    private byte[][] Char = new byte[1][PAGE_SIZE];
    private byte[][] IChar = new byte[1][PAGE_SIZE];
    
    private byte[] fakeRAM = new byte[PAGE_SIZE];  // contains FFs for no memory
    private byte[] nullRAM = new byte[PAGE_SIZE];  // for no mem writes
    private byte[][] readPages = new byte[128][];
    private byte[][] writePages = new byte[128][];
    
    private Config cf;
    
    public Memory(Config cnf) {
        
        cf = cnf;
        
        loadRoms();
    }

    public void reset(boolean dirty) {
        
        rampages = cf.ramsize >>> 3;
        
        if (dirty) {
            char c = 0;
            int a = 0;
            for(int i=0; i<128; i++) {
                for(int j=0; j<PAGE_SIZE; j++) {
                    Ram[i][j] = (byte) c;
                    if (i<8) {VRam[i][j] = (byte) c;}
                    a = (a+1) & 0X7f;
                    if (a==0) { c ^= 0xff; };
                }
            }
        }
  
        for(int i=0; i<PAGE_SIZE; i++) {
                fakeRAM[i] = (byte) 0xff;   
            }

        for(int i=0; i<128; i++) {
                readPages[i] = fakeRAM;
                writePages[i] = nullRAM;
            }
        
        for(int i=0; i<rampages; i++) {
                readPages[i] = writePages[i] = Ram[i];   
            }

        if (!cf.videoitk) {
            int a = 0x0e0000 >>> PAGE_BIT;
            for(int i=0; i<8; i++) {
                readPages[a] = writePages[a] = VRam[i];
                a++;
            }
        }
    }  
    
    public void dumpRam(String fname, int first, int last) {
        RandomAccessFile f;
        try {
            f = new RandomAccessFile(fname, "rw");
            for(int i=first; i<(last+1); i++) {
                f.write(Ram[i]);
            }
            f.close();
        } catch (IOException ex) {
            Logger.getLogger(Memory.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public byte readIChar(int address) {
        return IChar[address >>> PAGE_BIT][address & PAGE_MASK];
    }

    public byte readChar(int address) {
        return Char[address >>> PAGE_BIT][address & PAGE_MASK];
    }

    public byte readVram(int address) {
        return VRam[address >>> PAGE_BIT][address & PAGE_MASK];
    }

    public byte readByte(int address) {
        return readPages[address >>> PAGE_BIT][address & PAGE_MASK];
    }
    
    public void writeByte(int address, byte value) {
        writePages[address >>> PAGE_BIT][address & PAGE_MASK] = value;
    }
      
    public void bootRom(boolean state) {
        if (!state) {
            readPages[0] = writePages[0];
        }
        else {
            readPages[0] = (cf.videoitk) ? BootS[0] : BootG[0];
        }
    }
    
    private void loadRoms() {
        if (!loadRomAsFile("roms/chars.bin", Char, 0, PAGE_SIZE)) {
            loadRomAsResource("/roms/chars.bin", Char, 0, PAGE_SIZE);
        }
        if (!loadRomAsFile("roms/charsITK.bin", IChar, 0, PAGE_SIZE)) {
            loadRomAsResource("/roms/charsITK.bin", IChar, 0, PAGE_SIZE);
        }
        if (!loadRomAsFile("roms/bootGC.bin", BootG, 0, PAGE_SIZE)) {
            loadRomAsResource("/roms/bootGC.bin", BootG, 0, PAGE_SIZE);
        }
        if (!loadRomAsFile("roms/bootSC.bin", BootS, 0, PAGE_SIZE)) {
            loadRomAsResource("/roms/bootSC.bin", BootS, 0, PAGE_SIZE);
        }
    }

    private boolean loadRomAsResource(String filename, byte[][] rom, int page, int size) {

        InputStream inRom = Tns.class.getResourceAsStream(filename);
        boolean res = false;

        if (inRom == null) {
            String msg =
                java.util.ResourceBundle.getBundle("machine/Bundle").getString(
                "RESOURCE_ROM_ERROR");
            System.out.println(String.format("%s: %s", msg, filename));
            return false;
        }

        try {
            for (int frag = 0; frag < size / PAGE_SIZE; frag++) {
                int count = 0;
                while (count != -1 && count < PAGE_SIZE) {
                    count += inRom.read(rom[page + frag], count, PAGE_SIZE - count);
                }

                if (count != PAGE_SIZE) {
                    String msg =
                        java.util.ResourceBundle.getBundle("machine/Bundle").getString(
                        "ROM_SIZE_ERROR");
                    System.out.println(String.format("%s: %s", msg, filename));
                } else {
                    res = true;
                }
            }
        } catch (IOException ex) {
            String msg =
                java.util.ResourceBundle.getBundle("machine/Bundle").getString(
                "RESOURCE_ROM_ERROR");
            System.out.println(String.format("%s: %s", msg, filename));
            Logger.getLogger(Tns.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                inRom.close();
            } catch (IOException ex) {
                Logger.getLogger(Memory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (res) {
            String msg =
                java.util.ResourceBundle.getBundle("machine/Bundle").getString(
                "ROM_RESOURCE_LOADED");
            System.out.println(String.format("%s: %s", msg, filename));
        }

        return res;
    }

    private boolean loadRomAsFile(String filename, byte[][] rom, int page, int size) {
        BufferedInputStream fIn = null;
        boolean res = false;

        try {
            try {
                fIn = new BufferedInputStream(new FileInputStream(filename));
            } catch (FileNotFoundException ex) {
                String msg =
                    java.util.ResourceBundle.getBundle("machine/Bundle").getString(
                    "FILE_ROM_ERROR");
                System.out.println(String.format("%s: %s", msg, filename));
                //Logger.getLogger(Spectrum.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

            for (int frag = 0; frag < size / PAGE_SIZE; frag++) {
                int count = 0;
                while (count != -1 && count < PAGE_SIZE) {
                    count += fIn.read(rom[page + frag], count, PAGE_SIZE - count);
                }

                if (count != PAGE_SIZE) {
                    String msg =
                        java.util.ResourceBundle.getBundle("machine/Bundle").getString(
                        "ROM_SIZE_ERROR");
                    System.out.println(String.format("%s: %s", msg, filename));
                } else {
                    res = true;
                }
            }
        } catch (IOException ex) {
            String msg =
                java.util.ResourceBundle.getBundle("machine/Bundle").getString(
                "FILE_ROM_ERROR");
            System.out.println(String.format("%s: %s", msg, filename));
            Logger.getLogger(Tns.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (fIn != null) {
                    fIn.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Memory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (res) {
            String msg =
                java.util.ResourceBundle.getBundle("machine/Bundle").getString(
                "ROM_FILE_LOADED");
            System.out.println(String.format("%s: %s", msg, filename));
        }

        return res;
    }

}
