package rxr;

import java.net.*;

import javax.swing.*;

import rxr.util.*;

public class RXR
{
	public static void main(String[] args)
	{
		try
		{
			Util.PLAF.setCurrentLAFName("Windows");
		}
		catch(Exception e)
		{
			//don't bother with PLAF then
		}

		MainPanel main = new MainPanel();

		JFrame window = new JFrame("Regexerator");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		window.add(main);

		window.setSize(600, 400);
		Util.Window.center(window);
		window.setVisible(true);
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
