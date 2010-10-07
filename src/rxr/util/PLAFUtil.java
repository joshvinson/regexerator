package rxr.util;

import java.awt.event.*;

import javax.swing.*;

public class PLAFUtil
{
	public static String[] getInstalledLAFNames()
	{
		UIManager.LookAndFeelInfo[] installed = UIManager.getInstalledLookAndFeels();
		String[] result = new String[installed.length];
		for(int i = 0; i < installed.length; i++)
		{
			result[i] = installed[i].getName();
		}
		return result;
	}

	public static String getLAFClass(String name)
	{
		UIManager.LookAndFeelInfo[] installed = UIManager.getInstalledLookAndFeels();
		for(int i = 0; i < installed.length; i++)
		{
			if(installed[i].getName().equals(name))
			{
				return installed[i].getClassName();
			}
		}
		return null;
	}

	public static boolean setCurrentLAFName(JFrame window, String name)
	{
		try
		{
			UIManager.setLookAndFeel(getLAFClass(name));
			//reloadLAF();
		}
		catch(Exception exception)
		{
			//exception.printStackTrace();
			return false;
		}

		SwingUtilities.updateComponentTreeUI(window);
		//window.pack(); //resizes components according to new LnF
		window.getContentPane().validate();
		return true;
	}

	public static boolean setCurrentLAFClass(JFrame window, String className)
	{
		try
		{
			UIManager.setLookAndFeel(className);
			//reloadLAF();
		}
		catch(Exception exception)
		{
			//exception.printStackTrace();
			return false;
		}

		SwingUtilities.updateComponentTreeUI(window);
		//window.pack(); //resizes components according to new LnF
		window.getContentPane().validate();
		return true;
	}

	public static boolean setCurrentLAFName(String name)
	{
		try
		{
			UIManager.setLookAndFeel(getLAFClass(name));
			//reloadLAF();
		}
		catch(Exception exception)
		{
			//exception.printStackTrace();
			return false;
		}

		return true;
	}

	public static boolean setCurrentLAFClass(String className)
	{
		try
		{
			UIManager.setLookAndFeel(className);
			//reloadLAF();
		}
		catch(Exception exception)
		{
			//exception.printStackTrace();
			return false;
		}

		return true;
	}

	public static String getCurrentLAFName()
	{
		return UIManager.getLookAndFeel().getName();
	}

	public static String getCurrentLAFClassName()
	{
		return UIManager.getLookAndFeel().getClass().getName();
	}

	public static boolean isInstalledLAFName(String name)
	{
		String[] installed = getInstalledLAFNames();
		for(int i = 0; i < installed.length; i++)
		{
			if(installed[i].equals(name))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean installLAF(String name)
	{
		try
		{
			UIManager.installLookAndFeel(name, getLAFClass(name));
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	public static JMenu makeLAFMenu(final JFrame window)
	{
		return makeLAFMenu(window, "Look & Feel", false);
	}

	public static JMenu makeLAFMenu(final JFrame window, String title, boolean showDisabled)
	{
		JMenu lnf = new JMenu(title, true);

		ButtonGroup buttonGroup = new ButtonGroup();

		UIManager.LookAndFeelInfo[] installed = UIManager.getInstalledLookAndFeels();

		for(int i = 0; i < installed.length; i++)
		{
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(installed[i].getName(), getCurrentLAFClassName().equals(installed[i].getClassName()));
			if(showDisabled)
			{
				item.setEnabled(isInstalledLAFName(installed[i].getName()));
			}
			else if(!isInstalledLAFName(installed[i].getName()))
			{
				continue;
			}
			final String className = installed[i].getClassName();

			item.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					setCurrentLAFClass(window, className);
				}
			});

			buttonGroup.add(item);
			lnf.add(item);
		}

		return lnf;
	}

	public static void reloadLAF()
	{
		setCurrentLAFName(getCurrentLAFName());
	}
}