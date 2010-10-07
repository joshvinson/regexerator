package rxr.util;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import rxr.util.component.*;

public class LayoutUtil
{
	final static Border emptyBorder = new EmptyBorder(0, 0, 0, 0);

	public static JPanel getInPanel(JComponent[] c, LayoutManager lm, boolean addInPanel)
	{
		JPanel temp = new JPanel();
		if(lm != null)
		{
			temp.setLayout(lm);
		}
		for(int i = 0; i < c.length; i++)
		{
			if(addInPanel)
			{
				temp.add(getInPanel(c[i]));
			}
			else
			{
				temp.add(c[i]);
			}
		}
		return temp;
	}

	public static JPanel getInPanel(JComponent[] c, LayoutManager lm, boolean addInPanel, String title, ImageIcon icon)
	{
		JPanel temp = new JPanel();
		if(lm != null)
		{
			temp.setLayout(lm);
		}
		if(title != null)
		{
			temp.setBorder(new TitleBarDropShadowBorder(title, icon));
		}
		else
		{
			temp.setBorder(new DropShadowBorder());
		}

		for(int i = 0; i < c.length; i++)
		{
			if(addInPanel)
			{
				temp.add(getInPanel(c[i]));
			}
			else
			{
				temp.add(c[i]);
			}
		}
		return temp;
	}

	public static JPanel getInPanel(JComponent[] c)
	{
		JPanel temp = new JPanel();
		for(int i = 0; i < c.length; i++)
		{
			temp.add(c[i]);
		}
		return temp;
	}

	public static JPanel getInPanel(JComponent c)
	{
		JPanel temp = new JPanel();
		temp.add(c);
		return temp;
	}

	public static Border getEmptyBorder()
	{
		return LayoutUtil.emptyBorder;
	}

	public static Border getEmptyBorder(int size)
	{
		return new EmptyBorder(size, size, size, size);
	}

	public static JMenu makeMenu(String title, ArrayList<String> names, ArrayList<ActionListener> listeners, ArrayList<KeyStroke> accelerators, ArrayList<Icon> icons)
	{
		JMenu menu = new JMenu(title, true);

		//are all the ArrayLists the same size?
		int size = names.size();
		if(listeners.size() != size || accelerators.size() != size || icons.size() != size)
		{
			return null;
		}

		for(int i = 0; i < names.size(); i++)
		{
			if(names.get(i) == null || names.get(i).equals(""))
			{
				menu.addSeparator();
			}
			else
			{
				JMenuItem item = new JMenuItem(names.get(i));
				if(icons.get(i) != null)
				{
					item.setIcon(icons.get(i));
				}
				if(accelerators.get(i) != null)
				{
					item.setAccelerator(accelerators.get(i));
				}
				item.addActionListener(listeners.get(i));

				menu.add(item);
			}
		}

		return menu;
	}
}