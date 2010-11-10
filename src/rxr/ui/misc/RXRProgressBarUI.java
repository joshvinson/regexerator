package rxr.ui.misc;

import java.awt.*;
import java.awt.geom.*;

import javax.swing.*;
import javax.swing.plaf.*;

/**
 * A Simple and compact ProgressBarUI.
 */
public class RXRProgressBarUI extends ProgressBarUI
{
	@Override
	public void paint(Graphics g, JComponent c)
	{
		Graphics2D window = (Graphics2D)g;

		Rectangle bo = c.getBounds();
		Insets ins = c.getInsets();

		int x = ins.left;
		int y = ins.top;
		int w = bo.width - ins.right - ins.left;
		int h = bo.height - ins.top - ins.bottom;

		JProgressBar jpb = (JProgressBar)c;

		double p = (jpb.getValue() - jpb.getMinimum()) / (double)(jpb.getMaximum() - jpb.getMinimum());

		window.setColor(new Color(255, 255, 235));
		window.fillRect(x, y, w, h);

		window.setColor(new Color(255, 255, 190));
		window.fillRect(x, y, (int)(p * w), h - 1);

		window.setColor(new Color(132, 130, 132));
		if(p > 0)
		{
			window.drawLine(x + (int)(p * w), y, x + (int)(p * w), y + h - 1);
		}

		if(jpb.isStringPainted())
		{
			String str = jpb.getString();
			Rectangle2D r = g.getFontMetrics().getStringBounds(str, window);

			window.setColor(Color.BLACK);
			window.drawString(str, (int)(x + w / 2 - r.getWidth() / 2), (int)(y + r.getHeight()) - 1);
		}
	}
}
