/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package machine;

import java.util.TimerTask;

public class MTimer extends TimerTask {

    private Tns m;
    private long now, diff;

    public MTimer(Tns m) {
        this.m = m;
    }

    @Override
    public synchronized void run() {

        now = System.currentTimeMillis();
        diff = now - scheduledExecutionTime();
        if (diff < 51) {
            m.ms20();
        }
    }
}
