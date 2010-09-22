package rxr.action;

import java.awt.event.*;

import javax.swing.*;

public class QuitAction extends AbstractAction
{
	public QuitAction()
	{
		super("Quit");
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		System.exit(0);
	}
}
