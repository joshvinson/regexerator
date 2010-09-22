package rxr.action;

import java.awt.event.*;
import java.io.*;
import java.util.regex.*;

import javax.swing.*;

import rxr.*;

public class AboutAction extends AbstractAction
{
	public AboutAction()
	{
		super("About");
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(RXR.load("res/txt/about").openStream()));

			Pattern p = Pattern.compile("%([^%]+)%");

			StringBuffer text = new StringBuffer();
			String line;
			while((line = in.readLine()) != null)
			{
				Matcher m = p.matcher(line);
				while(m.find())
				{
					String rep = RXR.properties.getProperty(m.group(1));
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
