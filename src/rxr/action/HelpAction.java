package rxr.action;

import java.awt.event.*;

import javax.swing.*;

import rxr.*;
import rxr.component.doc.*;
import rxr.util.*;

public class HelpAction extends AbstractAction
{
	private static final long serialVersionUID = 5277795480958338358L;

	private static boolean windowOpen = false;
	private static JFrame window;
	
	final protected static ImageIcon icon = new ImageIcon(RXR.load("res/media/13165.help.gif"));

	public HelpAction()
	{
		super("Help", icon);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(!windowOpen)
		{
			window = new JFrame("Regexerator Help");

			window.addWindowListener(new WindowAdapter()
			{
				@Override
				public void windowClosing(WindowEvent e)
				{
					windowOpen = false;
				}
			});

			DocBrowser db = new DocBrowser(RXR.load("res/html/help/"));

			window.add(db);

			window.setSize(550, 350);
			WindowUtil.center(window);
			window.setVisible(true);
			windowOpen = true;
		}
		else
		{
			window.requestFocus();
		}
	}
}
