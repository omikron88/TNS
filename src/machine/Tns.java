/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package machine;

import gui.Screen;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import z80core.Z80;

/**
 *
 * @author Administrator
 */
public class Tns extends Thread 
 implements z80core.MemIoOps, z80core.NotifyOps {
    
    private final int TNS_VERSION = 0x01; //version 0.1
    private final int t_frame     = 40000; //pocet T procesoru mezi dvema Vsync
    
    private Screen scr;
    private BufferedImage img;
    private Config cfg;
    private Keyboard key;
    private Memory mem;
    private Timer tim;
    private MTimer task;
    private Clock clk;
    private Z80 cpu;
    private Grafik grf;
    private Wd wdc;
    
    public JLabel Led1, Led2, Led3, Led4;
    
    private boolean paused;
    private boolean boot;
    private boolean fdcsync;
    private boolean runap;
    
    private int ap;
    private boolean mask[];
    private int pfr, pfw;
    private int map[][];
    private int mapp;
    private boolean mape[];
    private boolean mapa;

    public Tns() {
        cfg = new Config(); cfg.LoadConfig();
        img = new BufferedImage(640, 256, BufferedImage.TYPE_INT_RGB);
        mem = new Memory(cfg);
        tim = new Timer("Timer");
        clk = new Clock();
        cpu = new Z80(clk, this, this);
        grf = new Grafik(mem, img);
        key = new Keyboard();
        wdc = new Wd(this);

        paused = true;
        
        mask = new boolean[128];
        map  = new int[2][8];
        mape = new boolean[4];

        Reset(true);
    }
    
    public Config getConfig() {
        return cfg;
    }
    
    public Wd getWDC() {
        return wdc;
    }
        
    public void setScreen(Screen screen) {
        scr = screen;
    }
   
    public BufferedImage getImage() {
        return img;
    }
    
    public Keyboard getKeyboard() {
        return key;
    }
    
    public void setLed1(JLabel led) {
        Led1 = led;
    }
    
    public void setLed2(JLabel led) {
        Led2 = led;
    }
    
    public void setLed3(JLabel led) {
        Led3 = led;
    }
    
    public void setLed4(JLabel led) {
        Led4 = led;
    }
    
    int getPC() {
        return cpu.getRegPC();
    }
    
    public final void Reset(boolean dirty) {
        boot = true;
        fdcsync = false;
        runap = false;
        ap = 0x00;
        pfr = pfw = 0;
        mapp = 0;
        mapa = false;
        Arrays.fill(mape, false);
        mem.reset(dirty);
        mem.bootRom(true);
        clk.reset();
        cpu.reset();
        grf.reset();
        key.reset();
        wdc.reset();
    }
    
    public final void Nmi() {
//        cpu.setNMI(true);
//        cpu.execute(clk.getTstates()+8);
//        cpu.setNMI(false);
        mem.dumpRam("dump.bin", 0, 7);
    }

    public void startEmulation() {
        if (!paused) return;
        
        scr.repaint();
        paused = false;
        task = new MTimer(this);
        tim.scheduleAtFixedRate(task, 250, 20);
       }
    
    public void stopEmulation() {
        if (paused) return;
        
        paused = true;
        task.cancel();
    }
    
    public boolean isPaused() {
        return paused;
    }
    
    public void ms20() {        
        if (!paused) {           
            cpu.execute(clk.getTstates()+t_frame);
            grf.vSync();
            grf.paint();
            scr.repaint();
        }  
    }
    
    @Override
    public void run() {
        startEmulation();
        
        boolean forever = true;
        while(forever) {
            try {
                sleep(Long.MAX_VALUE);
            } catch (InterruptedException ex) {
                Logger.getLogger(Tns.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }    
            
    @Override
    public int fetchOpcode(int address) {
        clk.addTstates(4);
        mape[3] = mape[2];  //mapping enable/disable is 2 M1 cycles delayed
        mape[2] = mape[1];
        mape[1] = mape[0];
        
        if (runap) {
            ap = ((ap + 2) & 0xfe) | 1;
            if ((this.inPort(ap) & 1) !=0 )  {
                if (mask[ap>>>1]) {
                    runap = false;
                    cpu.setINTLine(true);
//                    System.out.println(String.format("***int: %04X", ap));
                }
            }            
        }
  
        int addr = address;
        if (mape[3]) {
            addr &= 0x1fff;     //8K boundary 
            addr |= map[mapp][address>>>13];
        }
        
        int opcode = mem.readByte(addr) & 0xff;
//        System.out.println(String.format("PC: %04X (%02X)", address,opcode));
        if (fdcsync==true) {
            fdcsync = false;
            opcode = 0x00;      // NOP
        }
        
        if (address == 0xf7f8) System.out.println("INT od FDC read");
        
        return opcode;
    }

    @Override
    public int peek8(int address) {
        clk.addTstates(3);
        
        int addr = address;
        if (pfr==0) {
            if (mape[3]) {
                addr &= 0x1fff;     //8K boundary 
                addr |= map[mapp][address>>>13];
            }   
        }
        else {
            addr |= pfr;
        }

        int value = mem.readByte(addr) & 0xff;
//        System.out.println(String.format("Peek: %04X,%02X (%04X)", address,value,cpu.getRegPC()));            
        return value;
    }

    @Override
    public void poke8(int address, int value) {
//        System.out.println(String.format("Poke: %04X,%02X (%04X)", address,value,cpu.getRegPC()));
        clk.addTstates(3);
        
        int addr = address;
        if (pfw==0) {
            if (mape[3]) {
                addr &= 0x1fff;     //8K boundary 
                addr |= map[mapp][address>>>13];
            }   
        }
        else {
            addr |= pfw;
        }

        mem.writeByte(addr, (byte) value);
    }

    @Override
    public int peek16(int address) {
        clk.addTstates(6);
        int lsb = mem.readByte(address) & 0xff;
        address = (address+1) & 0xffff;
        return ((mem.readByte(address) << 8) & 0xff00 | lsb);
    }

    @Override
    public void poke16(int address, int word) {
        clk.addTstates(6);
        mem.writeByte(address, (byte) word);
        address = (address+1) & 0xffff;
        mem.writeByte(address, (byte) (word >>> 8));
    }

    @Override
    public int intAck(int address) {
        clk.addTstates(6);
        if (mapa==false) {Arrays.fill(mape, false);}
        cpu.setINTLine(false);
        address |= (ap & 0x00fe);
//        System.out.println(String.format("###intack: %04X", address));
        int lsb = mem.readByte(address) & 0xff;
        address = (address+1) & 0xffff;
        return ((mem.readByte(address) << 8) & 0xff00 | lsb);
    }

    @Override
    public int inPort(int port) {
        clk.addTstates(4);
        int tmp = 0x00;       
        
        switch(port & 0xff) {
            case 0x2c:
                return grf.getMode();
            case 0x2d:
                return grf.isInt();
            case 0x2e:
                return key.getKey(); 
            case 0x2f:
                return key.isKey();
            case 0x3a:
                {pfr = pfw = 0; return 0;}
            case 0x5c:
                {
                    int val;
                    if ((port&0x1000)==0) {  // bank bit
                        val = map[0][(port&0xe000)>>>13] >>> 12;
                    } 
                    else {
                        val = map[1][(port&0xe000)>>>13] >>> 12;                        
                    }
                    return val;    
                }
            case 0x5e:
                {
                    int val = 0;
                    if (mape[0]) {val |= 0x80;}
                    if (mapa)    {val |= 0x02;}
                    if (mapp==1) {val |= 0x01;}
                    return val;
                }
            case 0x60:
                return wdc.getRes();
            case 0x62:
                return wdc.getTrk();
            case 0x64:
                return wdc.getSec();
            case 0x66:
                return wdc.getDat();
            case 0x68:
                return 0xff;        // FDC3 presence flag
            case 0x6A:
                return wdc.getBuf();
            case 0x61:
                return wdc.isInt();
            default:
        }
        
        if ((port & 0x0001) != 0) {
            tmp |= mask[ap>>>1] ? 0x08 : 0x00; 
        }
        else {
            System.out.println(String.format("In: %04X (%04X)",port,cpu.getRegPC()));
        }
        return tmp;
    }

    @Override
    public void outPort(int port, int value) {
        clk.addTstates(4);
        value = value & 0xff;
        
        if (boot) {
            boot = false;
            mem.bootRom(boot);
        }
        
        if ((port & 0x0001) != 0) {
            mask[(port&0xff)>>>1] = ((value & 1) != 0);
            runap = true;
//            System.out.println(String.format("mask: %04X,%02X (%04X)", port,value&1,cpu.getRegPC()));
        }
                
        switch(port & 0xff) {
            case 0x0e: {mape[0] = !mape[0]; break;}
            case 0x2c:
                {
//                    System.out.println(String.format("BGD: %04X,%02X (%04X)", port,value,cpu.getRegPC()));
                    grf.setMode(value);
                    break;
                }
            case 0x3a:
                {
//                    System.out.println(String.format("PFL: %02X (%04X)", value,cpu.getRegPC()));
                    pfr = (value & 0x0f) << 16;
                    pfw = (value & 0xf0) << 12;
                    break;
                }
            case 0x5c:
                {
                    if ((port&0x1000)==0) {  // bank bit
                        map[0][(port&0xe000)>>>13] = (value&0xfe) << 12;
//                        System.out.println(String.format("MAP0: %02X %02X (%04X)", port>>>13,value>>>1,cpu.getRegPC()));
                    } 
                    else {
                        map[1][(port&0xe000)>>>13] = (value&0xfe) << 12;
//                        System.out.println(String.format("MAP1: %02X %02X (%04X)", port>>>13,value>>>1,cpu.getRegPC()));                        
                    }
                    break;
                }
            case 0x5e:
                {
//                    System.out.println(String.format("MAP ctrl: %04X,%02X (%04X)", port,value,cpu.getRegPC()));
                    mapp = (value&1)==0 ? 0 : 1;
                    mapa = (value&2) != 0;
                    break;
                }
            case 0x60:
                {wdc.setCmd(value); break;}
            case 0x62:
                {wdc.setTrk(value); break;}
            case 0x64:
                {wdc.setSec(value); break;}
            case 0x66:
                {wdc.setDat(value); break;}
            case 0x68:
                {wdc.set3212(value); break;}
            case 0x6A:
                {wdc.setBuf(value); break;}
            case 0x6C:
                {fdcsync = true; break;}
            case 0x6E:
                {wdc.setMode(value); break;}
            default: 
            {
                if ((port & 0x0001) == 0) {
                    System.out.println(String.format("Out: %04X,%02X (%04X)", port,value&1,cpu.getRegPC()));
                }
            }
        }        
    }

    @Override
    public void contendedStates(int address, long tstates) {
    //    clk.addTstates(tstates);
    }

    @Override
    public int atAddress(int address, int opcode) {
        System.out.println(String.format("bp: %04X,%02X", address,opcode));
        System.out.println(String.format("AF:%04X BC:%04X DE:%04X HL:%04X",
                cpu.getRegAF(),cpu.getRegBC(),cpu.getRegDE(),cpu.getRegHL()));
        
        mem.dumpRam("dump.bin", 0, 7);
//        System.exit(0);
        return opcode;
    }

    @Override
    public void execDone() {
    
    }
}
