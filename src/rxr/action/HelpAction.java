package rxr.action;

import java.awt.event.*;

import javax.swing.*;

import rxr.*;
import rxr.ui.doc.*;
import rxr.util.*;

/**
 * Displays the help window. The help root directory is res/html/help. Window is
 * non-modal, and will only open one copy of itself. Attempted re-opening will
 * focus the open window.
 */
public class HelpAction extends AbstractAction
{
	private static final long serialVersionUID = 1L;

	/**
	 * Indicates if there is currently a help window open.
	 */
	private static boolean windowOpen = false;

	/**
	 * Handle for the potentially open window.
	 */
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
			window = new JFrame("Regexerator - Help");

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
