package rxr;

import java.net.*;
import java.util.*;

import javax.swing.*;

import rxr.action.*;
import rxr.ui.*;
import rxr.util.*;

public class RXR
{
	public static final Properties props = new Properties();

	public static JFrame window;

	/**
	 * Entry point into regexerator.
	 * 
	 * @param args
	 *            not used
	 */
	public static void main(String[] args)
	{
		ArrayList<String> propUrls = new ArrayList<String>();

		propUrls.add("res/props/rxr.properties");
		propUrls.add("res/props/rxr.settings.properties");

		for(String s : propUrls)
		{
			try
			{
				props.load(load(s).openStream());
			}
			catch(Exception e)
			{
				WindowUtil.error(e, "Cannot load properties file: " + s, true);
			}
		}

		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e)
		{
			//don't bother with PLAF then
		}

		MainPanel main = new MainPanel();

		window = new JFrame("Regexerator");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		window.setJMenuBar(createMenuBar());

		window.add(main);

		window.setSize(600, 400);
		WindowUtil.center(window);
		window.setVisible(true);
	}

	public static JMenuBar createMenuBar()
	{
		JMenuBar menu = new JMenuBar();

		//top level
		JMenu file = new JMenu("File");
		JMenu help = new JMenu("Help");

		//file
		JMenuItem quit = new JMenuItem(new QuitAction());

		//file - add
		file.add(quit);

		//help
		JMenuItem helpItem = new JMenuItem(new HelpAction());
		JMenuItem about = new JMenuItem(new AboutAction());

		//help - add
		help.add(helpItem);
		help.add(new JSeparator());
		help.add(about);

		//add top
		menu.add(file);
		menu.add(help);

		return menu;
	}

	/**
	 * Performs a call to getResouce() that occurs in a predictable manner. In a
	 * jar, the root will be the jar root. Outside of a jar, it will be the root
	 * of the class/package hierarchy.
	 * 
	 * @param path
	 *            the resource path
	 * @return a URL to the requested resource
	 */
	public static URL load(String path)
	{
		return RXR.class.getClassLoader().getResource(path);
	}

	public static String get(String prop)
	{
		return props.getProperty(prop);
	}
}
