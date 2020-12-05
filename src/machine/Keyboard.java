/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package machine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author admin
 */
public final class Keyboard implements KeyListener {
    
    private char key;
    private boolean pressed;
    
    public void reset() {
        pressed = false;
        key = 0;
    }

    public int isKey() {
        return pressed ? 1:0;
    }
    
    public int getKey() {
        pressed = false;
        return key;
    }
    
    @Override
    public void keyTyped(KeyEvent ke) {
  
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        switch(ke.getKeyCode()) {
            
            default: key = ke.getKeyChar();
        }  // switch
        pressed = true;
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        switch(ke.getKeyCode()) {
            
        }  //switch
        pressed = false;
    }

}
