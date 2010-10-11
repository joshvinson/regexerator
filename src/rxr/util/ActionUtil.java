package rxr.util;

import java.io.*;
import java.util.regex.*;

import javax.swing.*;

import rxr.*;

public class ActionUtil
{
	/**
	 * Pops up a window with the text from the specified file. Property names in
	 * the file will be replaced by their values.
	 * 
	 * @param file
	 *            the file to read from. Property names enclosed in %'s will be
	 *            replaced by their values.
	 */
	public static void popupWithReplace(String file)
	{
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(RXR.load(file).openStream(), "Cp1252"));

			Pattern p = Pattern.compile("%([^%]+)%");

			StringBuffer text = new StringBuffer();
			String line;
			while((line = in.readLine()) != null)
			{
				Matcher m = p.matcher(line);
				while(m.find())
				{
					String rep = RXR.get(m.group(1));
					m.appendReplacement(text, rep == null ? "[invalid property]" : rep);
				}
				m.appendTail(text);
				text.append("\n");
			}

			JOptionPane.showMessageDialog(RXR.window, text.toString());
		}
		catch(Exception ex)
		{
			//
		}
	}
}
