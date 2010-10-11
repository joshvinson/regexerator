package rxr.action;

import java.awt.event.*;

import javax.swing.*;

import rxr.util.*;

public class AboutAction extends AbstractAction
{
	private static final long serialVersionUID = -8753940638546839817L;

	public AboutAction()
	{
		super("About");
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		ActionUtil.popupWithReplace("res/txt/about");
	}
}
