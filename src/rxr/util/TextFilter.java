package rxr.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * The FileFilter to accept all directories and .txt files only.
 * 
 * @author jedihoho
 */
public class TextFilter extends FileFilter 
{
    /**
     * Check whether the File is directory/text file or not 
     * 
     * @param f
     * 			File to be checked
     */
    public boolean accept(File f) 
    {
    	String extension = null;
    	if (f.isDirectory()) 
    	{
    		return true;
    	}

    	String s = f.getName();
    	int i = s.lastIndexOf('.');
    	if (i > 0 &&  i < s.length() - 1) 
    	{
    		extension = s.substring(i+1).toLowerCase();
    	}
    	if (extension != null) 
    	{
    		if (extension.equals("txt")) 
    		{
            	return true;
    		}
            else 
            {
            	return false;
            }
    	}
        return false;
    }

    /**
     * The description of text filter
     */
    public String getDescription() 
    {
    	return ".txt";
    }
}