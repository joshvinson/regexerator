package rxr.util;

public class ColorUtil
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