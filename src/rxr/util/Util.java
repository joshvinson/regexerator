package rxr.util;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import rxr.util.component.*;

public class Util
{
	public static class Color
	{
		public static int checkColor(int color)
		{
			if(color < 0)
			{
				return 0;
			}
			if(color > 255)
			{
				return 255;
			}
			return color;
		}

		public static java.awt.Color invert(java.awt.Color color)
		{
			int r = 255 - color.getRed();
			int g = 255 - color.getGreen();
			int b = 255 - color.getBlue();

			return new java.awt.Color(r, g, b, color.getAlpha());
		}

		public static java.awt.Color averageColor(java.awt.Color c1, java.awt.Color c2, double balance)
		{
			return averageColor(c1, c2, balance, c1.getAlpha());
		}

		public static java.awt.Color averageColor(java.awt.Color c1, java.awt.Color c2, double balance, int alpha)
		{

			int r = (int)(c1.getRed() * balance + c2.getRed() * (1.0 - balance));
			int g = (int)(c1.getGreen() * balance + c2.getGreen() * (1.0 - balance));
			int b = (int)(c1.getBlue() * balance + c2.getBlue() * (1.0 - balance));

			return new java.awt.Color(r, g, b, alpha);
		}
	}

	public static class Layout
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
			return Util.Layout.emptyBorder;
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

	public static class PLAF
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

	public static class System
	{
		public static ClipboardOwner owner;

		/**
		 * Place a String on the clipboard, and make this class the owner of the
		 * Clipboard's contents.
		 */
		public static void setClipboardContents(String str)
		{
			StringSelection stringSelection = new StringSelection(str);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, owner);
		}

		/**
		 * Get the String residing on the clipboard.
		 * 
		 * @return any text found on the Clipboard; if none found, return an
		 *         empty String.
		 */
		public static String getClipboardContents()
		{
			String result = "";
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			//odd: the Object param of getContents is not currently used
			Transferable contents = clipboard.getContents(null);
			boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
			if(hasTransferableText)
			{
				try
				{
					result = (String)contents.getTransferData(DataFlavor.stringFlavor);
				}
				catch(UnsupportedFlavorException ex)
				{
					//highly unlikely since we are using a standard DataFlavor
					java.lang.System.out.println(ex);
				}
				catch(IOException ex)
				{
					java.lang.System.out.println(ex);
				}
			}
			return result;
		}

		public static void sleep(int m)
		{
			try
			{
				Thread.sleep(m);
			}
			catch(Exception e)
			{
				//
			}
		}
	}

	public static class Window
	{
		public static void warning(String warning)
		{
			JOptionPane.showMessageDialog(null, warning, "Warning", JOptionPane.WARNING_MESSAGE);
		}

		public static void error(Exception exception, String error, boolean fatal)
		{
			JOptionPane.showMessageDialog(null, error + "\n\nExeption text: " + exception, "Error", JOptionPane.ERROR_MESSAGE);
			if(fatal)
			{
				java.lang.System.exit(0);
			}
		}

		public static boolean confirm(String message)
		{
			return confirm(null, message);
		}

		public static boolean confirm(Component parentComponent, String message)
		{
			return JOptionPane.showConfirmDialog(parentComponent, message, "Confirm Action", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
		}

		public static void center(Component c)
		{
			center(c, null);
		}

		public static void center(Component c, Component rel)
		{
			if(rel != null)
			{
				c.setLocation(rel.getLocation().x + rel.getSize().width / 2 - c.getSize().width / 2, rel.getLocation().y + rel.getSize().height / 2 - c.getSize().height / 2);
			}
			else
			{
				c.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - c.getSize().width / 2, +Toolkit.getDefaultToolkit().getScreenSize().height / 2 - c.getSize().height / 2);
			}
		}
	}
}