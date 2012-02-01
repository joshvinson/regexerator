package rxr.util;

import java.text.*;

public class StringUtil
{
	public static String format(double number)
	{
		DecimalFormat vf = new DecimalFormat("#00.#E0");
		String str = vf.format(number);
		str = str.replace("E0", " ");
		str = str.replace("E3", "k");
		str = str.replace("E6", "M");
		str = str.replace("E9", "G");
		str = str.replace("E12", "T");
		str = str.replace("E-3", "m");
		str = str.replace("E-6", "u");
		str = str.replace("E-9", "n");
		str = str.replace("E-12", "p");
		return str;
	}
}
