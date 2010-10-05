package rxr;

import java.net.*;
import java.util.*;

import javax.swing.*;

import rxr.action.*;
import rxr.util.*;

/*
 * TODO: Custom progress bar UI DONE
 * TODO: interrupt match thread on edit DONE
 * TODO: highlight in replace pane
 * TODO: reset results while matching?
 */

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
		try
		{
			props.load(load("res/props/rxr.properties").openStream());
		}
		catch(Exception e)
		{
			Util.Window.error(e, "Cannot load properties file", true);
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
		Util.Window.center(window);
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
		//menu.add(Util.PLAF.makeLAFMenu(window));
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
}
