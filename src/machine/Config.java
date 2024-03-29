/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package machine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 *
 * @author Administrator
 */
public class Config {
    
    public final String filename = "tns.config";
    
    //ukladani settings
    public int ramsize = 256;      //Size of Main RAM in KB
    
    public boolean videobw = false; // is video BW?
    public boolean videoitk = false; // is video ITK?
    
    public String drive1 = "";   //Drive 1 image
    public String drive2 = "";   //Drive 2 image
    public String drive3 = "";   //Drive 3 image
    public String drive4 = "";   //Drive 4 image
    public String hdd = "";   //HDD image
    
    public String getMyPath() {
        String retVal = "";
        retVal = Tns.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (retVal.contains("/")) {
            int pos = retVal.lastIndexOf("/");
            retVal = retVal.substring(0, pos + 1);
        }
        return retVal;
    }

    public void SaveConfig() {
        Properties prop = new Properties();
        prop.setProperty("DRIVE1", drive1);
        prop.setProperty("DRIVE2", drive2);
        prop.setProperty("DRIVE3", drive3);
        prop.setProperty("DRIVE4", drive4);
        prop.setProperty("HDD", hdd);
        prop.setProperty("RAMSIZE", String.valueOf(ramsize));
        prop.setProperty("VIDEOBW", String.valueOf(videobw));
        prop.setProperty("VIDEOITK", String.valueOf(videoitk));
     
        String fileName = getMyPath() + filename;
        OutputStream os;
        try {
            os = new FileOutputStream(fileName);
            try {
                prop.store(os,"TNS config file");
            } catch (IOException ex) {
                System.out.println("Nelze ulozit " + fileName);
            }
            os.close();
        } catch (Exception ex) {
            
        }
    }
    
    private static int parseIntSafe(String strInt, int nDefault) {
        int nRet = nDefault;
        try {
            nRet = Integer.parseInt(strInt);
        } catch (Exception e) {
            nRet = nDefault;
        }
        return nRet;
    }
    
    private static boolean parseBooleanSafe(String strBoolean,boolean bDefault) {
        boolean bRet = bDefault;
        try {
            bRet = Boolean.parseBoolean(strBoolean);
        } catch (Exception e) {
            bRet = bDefault;
        }
        return bRet;
    }

    public void LoadConfig() {
        Properties prop = new Properties();
        String fileName = getMyPath() + filename;
        InputStream is;
        try {
            is = new FileInputStream(fileName);
            try {
                prop.load(is);
            } catch (IOException ex) {
                System.out.println("Nelze rozparsovat " + fileName);
            }
        } catch (FileNotFoundException ex) {                      
                SaveConfig(); 
                return;
        }

        drive1 = prop.getProperty("DRIVE1");
        drive2 = prop.getProperty("DRIVE2");
        drive3 = prop.getProperty("DRIVE3");
        drive4 = prop.getProperty("DRIVE4");
        hdd = prop.getProperty("HDD");
        ramsize = parseIntSafe(prop.getProperty("RAMSIZE"),256);
        videobw = parseBooleanSafe(prop.getProperty("VIDEOBW"),false);
        videoitk = parseBooleanSafe(prop.getProperty("VIDEOITK"),false);
    }
}
