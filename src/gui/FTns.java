/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import machine.Config;
import machine.Tns;

/**
 *
 * @author Administrator
 */
public class FTns extends javax.swing.JFrame {
    
    private Tns m;
    private Screen scr;
    private Debugger dbg;
    
    /**
     * Creates new form JOndra
     */
    public FTns() {     
        initComponents();
        initEmulator();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fc = new javax.swing.JFileChooser();
        ToolBar = new javax.swing.JToolBar();
        bOpent = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        bReset = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        bNmi = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        bPause = new javax.swing.JToggleButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        bSettings = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        bDebug = new javax.swing.JButton();
        jSeparator11 = new javax.swing.JToolBar.Separator();
        Drv1 = new javax.swing.JLabel();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        Drv2 = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        Drv3 = new javax.swing.JLabel();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        Drv4 = new javax.swing.JLabel();
        jSeparator10 = new javax.swing.JToolBar.Separator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("TNS GC");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        ToolBar.setRollover(true);
        ToolBar.setPreferredSize(new java.awt.Dimension(100, 20));

        bOpent.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/open.png"))); // NOI18N
        bOpent.setToolTipText("Open tape for Load");
        bOpent.setFocusable(false);
        bOpent.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bOpent.setPreferredSize(new java.awt.Dimension(20, 20));
        bOpent.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bOpent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bOpentActionPerformed(evt);
            }
        });
        ToolBar.add(bOpent);
        ToolBar.add(jSeparator1);

        bReset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/reset.png"))); // NOI18N
        bReset.setToolTipText("Reset");
        bReset.setFocusable(false);
        bReset.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bReset.setPreferredSize(new java.awt.Dimension(20, 20));
        bReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bResetActionPerformed(evt);
            }
        });
        ToolBar.add(bReset);
        ToolBar.add(jSeparator2);

        bNmi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/nmi.png"))); // NOI18N
        bNmi.setToolTipText("Nmi");
        bNmi.setFocusable(false);
        bNmi.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bNmi.setPreferredSize(new java.awt.Dimension(20, 20));
        bNmi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bNmiActionPerformed(evt);
            }
        });
        ToolBar.add(bNmi);
        ToolBar.add(jSeparator4);

        bPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/pause.png"))); // NOI18N
        bPause.setSelected(true);
        bPause.setToolTipText("Run/Pause");
        bPause.setFocusable(false);
        bPause.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bPause.setPreferredSize(new java.awt.Dimension(16, 16));
        bPause.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/run.png"))); // NOI18N
        bPause.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bPauseActionPerformed(evt);
            }
        });
        ToolBar.add(bPause);
        ToolBar.add(jSeparator5);

        bSettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/settings.png"))); // NOI18N
        bSettings.setToolTipText("Settings");
        bSettings.setFocusable(false);
        bSettings.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bSettings.setPreferredSize(new java.awt.Dimension(20, 20));
        bSettings.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSettingsActionPerformed(evt);
            }
        });
        ToolBar.add(bSettings);
        ToolBar.add(jSeparator6);

        bDebug.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/debugger.png"))); // NOI18N
        bDebug.setToolTipText("Debugger");
        bDebug.setFocusable(false);
        bDebug.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bDebug.setPreferredSize(new java.awt.Dimension(20, 20));
        bDebug.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bDebug.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDebugActionPerformed(evt);
            }
        });
        ToolBar.add(bDebug);
        ToolBar.add(jSeparator11);

        Drv1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Drv1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/yellow.png"))); // NOI18N
        Drv1.setEnabled(false);
        Drv1.setFocusable(false);
        Drv1.setPreferredSize(new java.awt.Dimension(24, 24));
        ToolBar.add(Drv1);
        ToolBar.add(jSeparator7);

        Drv2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Drv2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/yellow.png"))); // NOI18N
        Drv2.setEnabled(false);
        Drv2.setFocusable(false);
        Drv2.setPreferredSize(new java.awt.Dimension(24, 24));
        ToolBar.add(Drv2);
        ToolBar.add(jSeparator8);

        Drv3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Drv3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/yellow.png"))); // NOI18N
        Drv3.setEnabled(false);
        Drv3.setFocusable(false);
        Drv3.setPreferredSize(new java.awt.Dimension(24, 24));
        ToolBar.add(Drv3);
        ToolBar.add(jSeparator9);

        Drv4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Drv4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/yellow.png"))); // NOI18N
        Drv4.setEnabled(false);
        Drv4.setFocusable(false);
        Drv4.setPreferredSize(new java.awt.Dimension(24, 24));
        ToolBar.add(Drv4);
        ToolBar.add(jSeparator10);

        getContentPane().add(ToolBar, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bResetActionPerformed
        m.Reset(false);
    }//GEN-LAST:event_bResetActionPerformed

    private void bNmiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bNmiActionPerformed
        m.Nmi();
    }//GEN-LAST:event_bNmiActionPerformed

    private void bSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSettingsActionPerformed
        boolean pau = m.isPaused();
        m.stopEmulation();
        
        FSettings set = new FSettings();
        set.showDialog(m.getConfig());
        if (set.isResetNeeded()) {
            m.Reset(false);
        }
        set.dispose();
        
        m.getConfig().SaveConfig();
        
        if (!pau) m.startEmulation();
    }//GEN-LAST:event_bSettingsActionPerformed

    private void bOpentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bOpentActionPerformed
        boolean pau = m.isPaused();
        m.stopEmulation();
        
        FDrives drv = new FDrives();
        drv.showDialog(m);
        drv.dispose();
        
        Config cf = m.getConfig();
        cf.drive1 = m.getWDC().getImage(1);
        cf.drive2 = m.getWDC().getImage(2);
        cf.drive3 = m.getWDC().getImage(3);
        cf.drive4 = m.getWDC().getImage(4);
        cf.SaveConfig();
        
        if (!pau) m.startEmulation();
    }//GEN-LAST:event_bOpentActionPerformed

    private void bPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bPauseActionPerformed
        if (bPause.isSelected()) {
            m.startEmulation();
        } 
        else {
            m.stopEmulation();
        }
    }//GEN-LAST:event_bPauseActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        
    }//GEN-LAST:event_formWindowClosing

    private void bDebugActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDebugActionPerformed
        boolean pau = m.isPaused();
    
        m.stopEmulation();
        dbg.showDialog();
        dbg.setAlwaysOnTop(true);
        
        if (!pau) m.startEmulation();
    }//GEN-LAST:event_bDebugActionPerformed

    private void initEmulator() {
        m = new Tns();
        scr = new Screen();
        
        m.setScreen(scr);
        scr.setImage(m.getImage());
        
        getContentPane().add(scr, BorderLayout.CENTER);
        pack();
        
        m.setLed1(Drv1);
        m.setLed2(Drv2);
        m.setLed3(Drv3);
        m.setLed4(Drv4);
        
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screen.width-getSize().width)/2, (screen.height-getSize().height)/2);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(m.getKeyboard());
        
        dbg = new Debugger(m);
        
        m.start();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Tns.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Tns.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Tns.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Tns.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new FTns().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Drv1;
    private javax.swing.JLabel Drv2;
    private javax.swing.JLabel Drv3;
    private javax.swing.JLabel Drv4;
    private javax.swing.JToolBar ToolBar;
    private javax.swing.JButton bDebug;
    private javax.swing.JButton bNmi;
    private javax.swing.JButton bOpent;
    private javax.swing.JToggleButton bPause;
    private javax.swing.JButton bReset;
    private javax.swing.JButton bSettings;
    private javax.swing.JFileChooser fc;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator10;
    private javax.swing.JToolBar.Separator jSeparator11;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    // End of variables declaration//GEN-END:variables

}
