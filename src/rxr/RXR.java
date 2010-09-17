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

	public static URL load(String path)
	{
		return RXR.class.getClassLoader().getResource(path);
	}
}
