package rxr.util;

import java.awt.*;

/**
 * @author Josh Vinson
 */
public class ColorUtil
{
	/**
	 * Bounds a color parameter to between 0 and 255 (both inclusive).
	 * 
	 * @param color
	 *            the parameter to bound.
	 * @return color, if 0<=color<=255, 255 if color>255, 0 if color<0
	 */
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

	/**
	 * Inverts a color (for each i in RGB, changes i to 255-i). Keeps alpha the
	 * same.
	 * 
	 * @param color
	 *            the color to invert.
	 * @return the inverted color.
	 */
	public static Color invert(Color color)
	{
		int r = 255 - color.getRed();
		int g = 255 - color.getGreen();
		int b = 255 - color.getBlue();

		return new Color(r, g, b, color.getAlpha());
	}

	public static Color averageColor(Color c1, Color c2, double balance)
	{
		return averageColor(c1, c2, balance, c1.getAlpha());
	}

	public static Color averageColor(Color c1, Color c2, double balance, int alpha)
	{

		int r = (int)(c1.getRed() * balance + c2.getRed() * (1.0 - balance));
		int g = (int)(c1.getGreen() * balance + c2.getGreen() * (1.0 - balance));
		int b = (int)(c1.getBlue() * balance + c2.getBlue() * (1.0 - balance));

		return new Color(r, g, b, alpha);
	}

	/**
	 * Decodes a color of the form #hhhhhhhh or #hhhhhh, (for example,
	 * #39F80D8C, or #9A67E9), where each two digit hexadecimal part is the red,
	 * green, blue, and alpha, respectively. The alpha component is optional,
	 * and will be assumed to be FF if not present.
	 * 
	 * @param s
	 *            the string to decode
	 * @return a color described by the string parameter
	 */
	public static Color decode(String s)
	{
		if(s.startsWith("#"))
		{
			//hex string
			if(s.length() == 9)
			{
				//includes alpha
				int alpha = Integer.parseInt(s.substring(7), 16);
				Color temp = Color.decode(s.substring(0, 7));
				return new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), alpha);
			}
			else if(s.length() == 7)
			{
				//does not include alpha
				return Color.decode(s);
			}
		}
		return Color.BLACK;
	}
}