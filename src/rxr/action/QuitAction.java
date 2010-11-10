package rxr.action;

import java.awt.event.*;

import javax.swing.*;

import rxr.*;

/**
 * Exits the application.
 */
public class QuitAction extends AbstractAction
{
	private static final long serialVersionUID = 1L;

	public QuitAction()
	{
		super("Quit");
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		RXR.exit();
	}
}
