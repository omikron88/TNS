/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import machine.Tns;
import machine.Wap;
import machine.Wd;
import utils.DriveGeom;

/**
 *
 * @author admin
 */
public class FDrives extends javax.swing.JDialog {
    
    private Tns tns;
    private Wd wd;
    private Wap wap;
    private File dir;
    
    private final String imageExt[] = {".img",".8sd","cpm"};
    private final String hdExt[] = {".hdd"};
    private final ExtendedFileFilter imageFlt = new ExtendedFileFilter
            ("Image files",imageExt);
    private final ExtendedFileFilter hddFlt = new ExtendedFileFilter
            ("HDD images",hdExt);
    
    /**
     * Creates new form FDrives
     */
    public FDrives() {
        initComponents();
    }
    
    public void showDialog(Tns t) {
        
        tns = t;
        wd = tns.getWDC();
        wap = tns.getWAP();
        dir = new File(tns.getConfig().getMyPath());
        int use = wd.isInUse();
        
        path1.setText(wd.getImage(1));
        path2.setText(wd.getImage(2));
        path3.setText(wd.getImage(3));
        path4.setText(wd.getImage(4));
        path5.setText(wap.getImage());        
        
        path1.setEnabled(use != 1);
        open1.setEnabled(use != 1);
        eject1.setEnabled(use != 1);
        new1.setEnabled(use != 1);
        
        path2.setEnabled(use != 2);
        open2.setEnabled(use != 2);
        eject2.setEnabled(use != 2);
        new2.setEnabled(use != 2);
        
        path3.setEnabled(use != 3);
        open3.setEnabled(use != 3);
        eject3.setEnabled(use != 3);
        new3.setEnabled(use != 3);
        
        path4.setEnabled(use != 4);
        open4.setEnabled(use != 4);
        eject4.setEnabled(use != 4);
        new4.setEnabled(use != 4);
        
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screen.width-getSize().width)/2, (screen.height-getSize().height)/2);
        setModal(true);
        setVisible(true);
    }
    
    private void chooseImage(int num, String title, JTextField t) {
        fc.setDialogTitle(title);
        fc.resetChoosableFileFilters();
        fc.setAcceptAllFileFilterUsed(true);
        fc.setFileFilter(imageFlt);
        fc.setCurrentDirectory(dir);
        int val = fc.showOpenDialog(this);      
        if (val==JFileChooser.APPROVE_OPTION) {
            try {
                wd.insertImage(num,fc.getSelectedFile().getCanonicalPath());
                t.setText(wd.getImage(num));
            } catch (IOException ex) {
                Logger.getLogger(FDrives.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }
    
    private void chooseHDDImage(String title, JTextField t) {
        fc.setDialogTitle(title);
        fc.resetChoosableFileFilters();
        fc.setAcceptAllFileFilterUsed(true);
        fc.setFileFilter(hddFlt);
        fc.setCurrentDirectory(dir);
        int val = fc.showOpenDialog(this);      
        if (val==JFileChooser.APPROVE_OPTION) {
            try {
                wap.insertImage(fc.getSelectedFile().getCanonicalPath());
                t.setText(wap.getImage());
            } catch (IOException ex) {
                Logger.getLogger(FDrives.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }
    
    private void ejectImage(int num, JTextField t) {
        wd.ejectImage(num);
        t.setText("");
    }
    
    private void ejectHDDImage(JTextField t) {
        wap.ejectImage();
        t.setText("");
    }
    
    private void newImage(int num, String title, JTextField t) {
        Random r=new Random();
        String fn = String.format("new-%04X.img", r.nextInt(0x10000));
        RandomAccessFile f;
        byte b[];
        
        fc.setDialogTitle(title);
        fc.resetChoosableFileFilters();
        fc.setAcceptAllFileFilterUsed(true);
        fc.setFileFilter(new FileNameExtensionFilter("image files", "img"));
        fc.setCurrentDirectory(dir);
        fc.setSelectedFile(new File(fn));
        int val = fc.showSaveDialog(this);      
        if (val==JFileChooser.APPROVE_OPTION) {
            try {
                fn = fc.getSelectedFile().getCanonicalPath();
                f =  new RandomAccessFile(fn, "rw");
                DriveGeom dg = wd.getDriveGeometry(num);
                b = new byte[dg.bps * dg.sectors];
                Arrays.fill(b, (byte) 0xe5);
                int n, m = dg.sides * dg.tracks;
                for(n=0; n<m; n++) { f.write(b); }
                f.close();
                wd.insertImage(num, fn);
                t.setText(fn);
            } catch (IOException ex) {
                Logger.getLogger(FDrives.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }
        
    private void newHDDImage(String title, JTextField t) {
        Random r=new Random();
        String fn = String.format("new-%04X.hdd", r.nextInt(0x10000));
        RandomAccessFile f;
        byte b[];
        
        fc.setDialogTitle(title);
        fc.resetChoosableFileFilters();
        fc.setAcceptAllFileFilterUsed(true);
        fc.setFileFilter(new FileNameExtensionFilter("HDD images", "hdd"));
        fc.setCurrentDirectory(dir);
        fc.setSelectedFile(new File(fn));
        int val = fc.showSaveDialog(this);      
        if (val==JFileChooser.APPROVE_OPTION) {
            try {
                fn = fc.getSelectedFile().getCanonicalPath();
                f =  new RandomAccessFile(fn, "rw");
                b = new byte[64*128];             // SPT * S
                Arrays.fill(b, (byte) 0xe5);
                int n, m = 2610;                  // T
                for(n=0; n<m; n++) { f.write(b); }
                f.close();
                wap.insertImage(fn);
                t.setText(fn);
            } catch (IOException ex) {
                Logger.getLogger(FDrives.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
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
        panel = new javax.swing.JPanel();
        panel1 = new javax.swing.JPanel();
        label1 = new javax.swing.JLabel();
        path1 = new javax.swing.JTextField();
        open1 = new javax.swing.JButton();
        eject1 = new javax.swing.JButton();
        new1 = new javax.swing.JButton();
        panel2 = new javax.swing.JPanel();
        label2 = new javax.swing.JLabel();
        path2 = new javax.swing.JTextField();
        open2 = new javax.swing.JButton();
        eject2 = new javax.swing.JButton();
        new2 = new javax.swing.JButton();
        panel3 = new javax.swing.JPanel();
        label3 = new javax.swing.JLabel();
        path3 = new javax.swing.JTextField();
        open3 = new javax.swing.JButton();
        eject3 = new javax.swing.JButton();
        new3 = new javax.swing.JButton();
        panel4 = new javax.swing.JPanel();
        label4 = new javax.swing.JLabel();
        path4 = new javax.swing.JTextField();
        open4 = new javax.swing.JButton();
        eject4 = new javax.swing.JButton();
        new4 = new javax.swing.JButton();
        panel5 = new javax.swing.JPanel();
        label5 = new javax.swing.JLabel();
        path5 = new javax.swing.JTextField();
        open5 = new javax.swing.JButton();
        eject5 = new javax.swing.JButton();
        new5 = new javax.swing.JButton();
        bOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Drives");
        setAlwaysOnTop(true);
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(396, 468));
        setModal(true);
        setName("DrivesDlg"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridLayout(1, 0));

        panel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        panel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        label1.setText("1");

        path1.setEditable(false);

        open1.setText("O");
        open1.setPreferredSize(new java.awt.Dimension(40, 30));
        open1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                open1ActionPerformed(evt);
            }
        });

        eject1.setText("E");
        eject1.setPreferredSize(new java.awt.Dimension(40, 30));
        eject1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eject1ActionPerformed(evt);
            }
        });

        new1.setText("N");
        new1.setPreferredSize(new java.awt.Dimension(40, 30));
        new1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(path1, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(open1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(eject1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(new1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label1)
                    .addComponent(path1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(open1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(eject1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(new1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        label2.setText("2");

        path2.setEditable(false);

        open2.setText("O");
        open2.setPreferredSize(new java.awt.Dimension(40, 30));
        open2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                open2ActionPerformed(evt);
            }
        });

        eject2.setText("E");
        eject2.setPreferredSize(new java.awt.Dimension(40, 30));
        eject2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eject2ActionPerformed(evt);
            }
        });

        new2.setText("N");
        new2.setPreferredSize(new java.awt.Dimension(40, 30));
        new2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(path2, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(open2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(eject2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(new2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label2)
                    .addComponent(path2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(open2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(eject2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(new2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        label3.setText("3");

        path3.setEditable(false);

        open3.setText("O");
        open3.setPreferredSize(new java.awt.Dimension(40, 30));
        open3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                open3ActionPerformed(evt);
            }
        });

        eject3.setText("E");
        eject3.setPreferredSize(new java.awt.Dimension(40, 30));
        eject3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eject3ActionPerformed(evt);
            }
        });

        new3.setText("N");
        new3.setPreferredSize(new java.awt.Dimension(40, 30));
        new3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel3Layout = new javax.swing.GroupLayout(panel3);
        panel3.setLayout(panel3Layout);
        panel3Layout.setHorizontalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(path3, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(open3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(eject3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(new3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel3Layout.setVerticalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label3)
                    .addComponent(path3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(open3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(eject3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(new3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        label4.setText("4");

        path4.setEditable(false);

        open4.setText("O");
        open4.setPreferredSize(new java.awt.Dimension(40, 30));
        open4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                open4ActionPerformed(evt);
            }
        });

        eject4.setText("E");
        eject4.setPreferredSize(new java.awt.Dimension(40, 30));
        eject4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eject4ActionPerformed(evt);
            }
        });

        new4.setText("N");
        new4.setPreferredSize(new java.awt.Dimension(40, 30));
        new4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel4Layout = new javax.swing.GroupLayout(panel4);
        panel4.setLayout(panel4Layout);
        panel4Layout.setHorizontalGroup(
            panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(path4, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(open4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(eject4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(new4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel4Layout.setVerticalGroup(
            panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label4)
                    .addComponent(path4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(open4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(eject4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(new4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        label5.setText("H");

        path5.setEditable(false);

        open5.setText("O");
        open5.setPreferredSize(new java.awt.Dimension(40, 30));
        open5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                open5ActionPerformed(evt);
            }
        });

        eject5.setText("E");
        eject5.setPreferredSize(new java.awt.Dimension(40, 30));
        eject5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eject5ActionPerformed(evt);
            }
        });

        new5.setText("N");
        new5.setPreferredSize(new java.awt.Dimension(40, 30));
        new5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel5Layout = new javax.swing.GroupLayout(panel5);
        panel5.setLayout(panel5Layout);
        panel5Layout.setHorizontalGroup(
            panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(path5, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(open5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(eject5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(new5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel5Layout.setVerticalGroup(
            panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label5)
                    .addComponent(path5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(open5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(eject5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(new5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bOk.setText("Ok");
        bOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bOkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGap(164, 164, 164)
                        .addComponent(bOk)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addComponent(bOk)
                .addContainerGap())
        );

        getContentPane().add(panel);
    }// </editor-fold>//GEN-END:initComponents

    private void bOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bOkActionPerformed
        setModal(false);
        setVisible(false);
    }//GEN-LAST:event_bOkActionPerformed

    private void open1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_open1ActionPerformed
        chooseImage(1, "Select image for drive 1", path1);
    }//GEN-LAST:event_open1ActionPerformed

    private void open2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_open2ActionPerformed
        chooseImage(2, "Select image for drive 2", path2);
    }//GEN-LAST:event_open2ActionPerformed

    private void open3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_open3ActionPerformed
        chooseImage(3, "Select image for drive 3", path3);
    }//GEN-LAST:event_open3ActionPerformed

    private void open4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_open4ActionPerformed
        chooseImage(4, "Select image for drive 4", path4);
    }//GEN-LAST:event_open4ActionPerformed

    private void eject1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eject1ActionPerformed
        ejectImage(1, path1);
    }//GEN-LAST:event_eject1ActionPerformed

    private void eject2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eject2ActionPerformed
        ejectImage(2, path2);
    }//GEN-LAST:event_eject2ActionPerformed

    private void eject3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eject3ActionPerformed
        ejectImage(3, path3);
    }//GEN-LAST:event_eject3ActionPerformed

    private void eject4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eject4ActionPerformed
        ejectImage(4, path4);
    }//GEN-LAST:event_eject4ActionPerformed

    private void new1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_new1ActionPerformed
        newImage(1, "Create new image for drive 1", path1);
    }//GEN-LAST:event_new1ActionPerformed

    private void new2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_new2ActionPerformed
        newImage(2, "Create new image for drive 2", path2);
    }//GEN-LAST:event_new2ActionPerformed

    private void new3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_new3ActionPerformed
        newImage(3, "Create new image for drive 3", path3);
    }//GEN-LAST:event_new3ActionPerformed

    private void new4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_new4ActionPerformed
        newImage(4, "Create new image for drive 4", path4);
    }//GEN-LAST:event_new4ActionPerformed

    private void open5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_open5ActionPerformed
        chooseHDDImage("Select HDD image", path5);
    }//GEN-LAST:event_open5ActionPerformed

    private void eject5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eject5ActionPerformed
        ejectHDDImage(path5);
    }//GEN-LAST:event_eject5ActionPerformed

    private void new5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_new5ActionPerformed
        newHDDImage("Create new HDD image", path5);
    }//GEN-LAST:event_new5ActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bOk;
    private javax.swing.JButton eject1;
    private javax.swing.JButton eject2;
    private javax.swing.JButton eject3;
    private javax.swing.JButton eject4;
    private javax.swing.JButton eject5;
    private javax.swing.JFileChooser fc;
    private javax.swing.JLabel label1;
    private javax.swing.JLabel label2;
    private javax.swing.JLabel label3;
    private javax.swing.JLabel label4;
    private javax.swing.JLabel label5;
    private javax.swing.JButton new1;
    private javax.swing.JButton new2;
    private javax.swing.JButton new3;
    private javax.swing.JButton new4;
    private javax.swing.JButton new5;
    private javax.swing.JButton open1;
    private javax.swing.JButton open2;
    private javax.swing.JButton open3;
    private javax.swing.JButton open4;
    private javax.swing.JButton open5;
    private javax.swing.JPanel panel;
    private javax.swing.JPanel panel1;
    private javax.swing.JPanel panel2;
    private javax.swing.JPanel panel3;
    private javax.swing.JPanel panel4;
    private javax.swing.JPanel panel5;
    private javax.swing.JTextField path1;
    private javax.swing.JTextField path2;
    private javax.swing.JTextField path3;
    private javax.swing.JTextField path4;
    private javax.swing.JTextField path5;
    // End of variables declaration//GEN-END:variables
}
