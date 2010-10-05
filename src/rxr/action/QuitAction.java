package rxr.action;

import java.awt.event.*;

import javax.swing.*;

public class QuitAction extends AbstractAction
{
	private static final long serialVersionUID = 1557244056588708726L;

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
