package rxr.action;

import java.awt.event.*;
import java.io.*;

import javax.swing.*;

import rxr.*;
import rxr.ui.*;
import rxr.util.*;

/**
 * Save Button in menu bar. Save the Replace Result into text file
 * 
 * @author jedihoho
 */
public class SaveAction extends AbstractAction
{
	private static final long serialVersionUID = 7703071512045035974L;

	private static ImageIcon icon = new ImageIcon(RXR.load("res/media/14437.save.gif"));

	MainPanel panel;

	public SaveAction(MainPanel panel)
	{
		super("Save Result As...", icon);
		this.panel = panel;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		//Create a file chooser
		boolean done = false;
		JFileChooser fc = new JFileChooser();
		fc.addChoosableFileFilter(new TextFilter());
		fc.setAcceptAllFileFilterUsed(false);

		while(!done)
		{
			int returnVal = fc.showSaveDialog(RXR.window);
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				File file = fc.getSelectedFile();
				if(file.exists())
				{
					int n = JOptionPane.showConfirmDialog(RXR.window, "File exists. " + "Do you want to overwrite it?", "Overwrite?", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);

					if(n == 0)
					{
						done = savefile(file);
					}
				}
				else
				{
					done = savefile(file);
				}
			}
			else
			{
				done = true;
			}
		}
	}

	/**
	 * 
	 * @param file
	 *            The path that replace result to be saved
	 * @return True if successfully save file to specific File, otherwise False
	 */
	private boolean savefile(File file)
	{
		String s = file.getName();
		int i = s.lastIndexOf(".txt");
		String filename = file.getAbsolutePath();
		if(i <= 0 || i >= s.length() - 1)
		{
			filename = filename + ".txt";
		}

		try
		{
			FileWriter fstream = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(panel.getReplaceField().getText());
			out.close();
		}
		catch(Exception e)
		{
			System.err.println("Error: " + e.getMessage());
		}
		return true;
	}
}
