package rxr.action;

import java.awt.event.*;

import javax.swing.*;

import rxr.*;

/**
 * Exits the application.
 * 
 * @author Josh Vinson
 */
public class ExitAction extends AbstractAction
{
	private static final long serialVersionUID = 1L;

	public ExitAction()
	{
		super("Exit");
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		RXR.exit();
	}
}
