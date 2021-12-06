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
    public int ramsize=256;      //Size of Main RAM in KB
 
    public String drive1 = "";   //Drive 1 image
    public String drive2 = "";   //Drive 2 image
    public String drive3 = "";   //Drive 3 image
    public String drive4 = "";   //Drive 4 image
    //ulozene udaje pro debugger
    public boolean bBP1=false;
    public int nBP1Address=0;
    public boolean bBP2=false;
    public int nBP2Address=0;
    public boolean bBP3=false;
    public int nBP3Address=0;
    public boolean bBP4=false;
    public int nBP4Address=0;
    public boolean bBP5=false;
    public int nBP5Address=0;
    public boolean bBP6=false;
    public int nBP6Address=0;
    public int nMemAddress=0;
    public boolean bShowCode=false;

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
        prop.setProperty("RAMSIZE", String.valueOf(ramsize));
        prop.setProperty("BP1CHCK", String.valueOf(bBP1));
        prop.setProperty("BP1ADDRESS", String.valueOf(nBP1Address));
        prop.setProperty("BP2CHCK", String.valueOf(bBP2));
        prop.setProperty("BP2ADDRESS", String.valueOf(nBP2Address));
        prop.setProperty("BP3CHCK", String.valueOf(bBP3));
        prop.setProperty("BP3ADDRESS", String.valueOf(nBP3Address));
        prop.setProperty("BP4CHCK", String.valueOf(bBP4));
        prop.setProperty("BP4ADDRESS", String.valueOf(nBP4Address));
        prop.setProperty("BP5CHCK", String.valueOf(bBP5));
        prop.setProperty("BP5ADDRESS", String.valueOf(nBP5Address));   
        prop.setProperty("BP6CHCK", String.valueOf(bBP6));
        prop.setProperty("BP6ADDRESS", String.valueOf(nBP6Address));  
        prop.setProperty("MEMADDRESS", String.valueOf(nMemAddress)); 
        prop.setProperty("BSHOWCODE", String.valueOf(bShowCode));     
        
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
        ramsize = parseIntSafe(prop.getProperty("RAMSIZE"),256);
        bBP1=parseBooleanSafe(prop.getProperty("BP1CHCK"),false);
        nBP1Address=parseIntSafe(prop.getProperty("BP1ADDRESS"),0);
        bBP2=parseBooleanSafe(prop.getProperty("BP2CHCK"),false);
        nBP2Address=parseIntSafe(prop.getProperty("BP2ADDRESS"),0);
        bBP3=parseBooleanSafe(prop.getProperty("BP3CHCK"),false);
        nBP3Address=parseIntSafe(prop.getProperty("BP3ADDRESS"),0);
        bBP4=parseBooleanSafe(prop.getProperty("BP4CHCK"),false);
        nBP4Address=parseIntSafe(prop.getProperty("BP4ADDRESS"),0);
        bBP5=parseBooleanSafe(prop.getProperty("BP5CHCK"),false);
        nBP5Address=parseIntSafe(prop.getProperty("BP5ADDRESS"),0);
        bBP6=parseBooleanSafe(prop.getProperty("BP6CHCK"),false);
        nBP6Address=parseIntSafe(prop.getProperty("BP6ADDRESS"),0);
        nMemAddress=parseIntSafe(prop.getProperty("MEMADDRESS"),0);
        bShowCode=parseBooleanSafe(prop.getProperty("BSHOWCODE"),false);    
    }
}
