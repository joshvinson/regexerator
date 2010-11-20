package rxr.action;

import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import rxr.*;
import rxr.ui.*;
import rxr.util.*;

/**
 * Open Button in menu bar. Can open only text file to import it to Test Text
 * 
 * @author jedihoho
 */
public class OpenAction extends AbstractAction
{
	private static final long serialVersionUID = -229200358911036380L;

	private static ImageIcon icon = new ImageIcon(RXR.load("res/media/13568.folder_open.gif"));

	MainPanel panel;

	public OpenAction(MainPanel panel)
	{
		super("Open...", icon);
		this.panel = panel;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		boolean done = false;
		JFileChooser fc = new JFileChooser();
		fc.addChoosableFileFilter(new TextFilter());
		fc.setAcceptAllFileFilterUsed(false);
		while(!done)
		{
			int returnVal = fc.showOpenDialog(RXR.window);
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				File file = fc.getSelectedFile();
				if(file.exists())
				{
					if(!file.canRead())
					{
						WindowUtil.error(null, "File cannot be read", false);
					}
					StringBuilder contents = new StringBuilder();
					try
					{
						FileReader fstream = new FileReader(file);
						BufferedReader input = new BufferedReader(fstream);
						try
						{
							String line = null;
							while((line = input.readLine()) != null)
							{
								contents.append(line);
								contents.append(System.getProperty("line.separator"));
							}
						}
						finally
						{
							input.close();
						}
					}
					catch(IOException ex)
					{
						ex.printStackTrace();
					}
					String str = contents.toString();
					panel.getTextField().setText(str);
					done = true;
				}
				else
				{
					JOptionPane.showMessageDialog(RXR.window, "File does not exist.", "File does not exist", JOptionPane.WARNING_MESSAGE);
				}
			}
			else
			{
				done = true;
			}
		}
	}
}
