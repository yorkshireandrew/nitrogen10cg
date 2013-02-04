package cg;
import java.io.File;

import javax.swing.filechooser.FileFilter;

public class GeneralFileFilter extends FileFilter{
	
	String[] validExtensions;
	String description = "";
	
	GeneralFileFilter(String validExtension)
	{
		validExtensions = new String[1];
		validExtensions[0] = validExtension;
		
		String d = "";
		for(String s:validExtensions)
		{
			d = d + "." + s +" ";
		}
		description = d;
	}
	
	GeneralFileFilter(String... validExtensions)
	{
		this.validExtensions = validExtensions;
		
		String d = "";
		for(String s:validExtensions)
		{
			d = d + "." + s +" ";
		}
		description = d;
	}

	@Override
	public boolean accept(File f) {
		if(f == null)return false;
		if(f.isDirectory())return true;
		String ext = getExtension(f);
		if(ext == null)
		{
			// its not a directory but has no file extension so return false
			return false;
		}
		for(String s:validExtensions)
		{
			if(ext.equals(s))return true;
		}		
		return false;
	}

	@Override
	public String getDescription() {
		return description;
	}
	
    private String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
            return ext;
        }
        return ext;
    }

}
