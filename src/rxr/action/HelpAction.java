package rxr.action;

import java.awt.event.*;

import javax.swing.*;

import rxr.util.*;

public class HelpAction extends AbstractAction
{
	private static final long serialVersionUID = 5277795480958338358L;

	public HelpAction()
	{
		super("Help");
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		ActionUtil.popupWithReplace("res/txt/help");
	}
}
