package gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class ExtendedFileFilter extends FileFilter{

    String description,extension[];

    public ExtendedFileFilter(String desc, String[] ext) {
        description = desc;
        extension = ext;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public boolean accept(File file)
    {   
        if(file.isDirectory()) return true;
        for(int n=0; n<extension.length; n++) {
            if(file.getAbsolutePath().toLowerCase().endsWith(extension[n])) {
                return true;
            }
        }
        return false;
    }
}
