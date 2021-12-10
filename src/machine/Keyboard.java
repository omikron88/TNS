/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package machine;

import java.awt.event.KeyEvent;;
import java.awt.event.KeyListener;

/**
 *
 * @author admin
 */
public final class Keyboard implements KeyListener {
    
    private int key;
    private boolean pressed;
    
    public void reset() {
        pressed = false;
        key = 0;
    }

    public int isKey() {
        return pressed ? 1 : 0;
    }
    
    public int getKey() {
        pressed = false;
        System.out.println(String.format("key: %04X", key));
        return key;
    }
    
    @Override
    public void keyTyped(KeyEvent ke) {
  
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        int kodkl;
        boolean isact;
        
        kodkl = ke.getKeyCode();isact=ke.isActionKey();
                
        switch(kodkl) {        
            case KeyEvent.VK_ENTER:
                key = 0x0d;
                break;
            case 33: if (isact){key = 6;}else{key = 33;};        // PageUp = ANO
                break;
            case 34: if (isact){key = 0x15;}else{key = 34;};     // PageDown = NE
                break;
            case 36: if (isact){key = 1;}else{key = 36;};        // Home = START
                break;
            case 35: if (isact){key = 4;}else{key = 35;};        // End = STOP
                break;
            default: key = (int) ke.getKeyChar();
        }  // switch
        pressed = true;
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        switch(ke.getKeyCode()) {
            
        }  //switch
    }

}
