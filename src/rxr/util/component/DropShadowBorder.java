package rxr.util.component;

import java.awt.*;

import javax.swing.border.*;

/**
 * A border that adds an eclipse2-style drop shadow.
 */
public class DropShadowBorder extends AbstractBorder
{
	private static final long serialVersionUID = -6856077294038944134L;
	
	private static final Color DARK_COLOR = new Color(132, 130, 132);
	private static final Color MED_COLOR = new Color(143, 141, 138);
	private static final Color LIGHT_COLOR = new Color(171, 168, 165);

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
	{
		g.translate(x + 1, y + 1);
		int w = width - 3;
		int h = height - 3;

		g.setColor(DARK_COLOR);
		g.drawRect(0, 0, w - 2, h - 2);

		g.setColor(MED_COLOR);
		g.drawLine(w - 1, 1, w - 1, h - 1);
		g.drawLine(1, h - 1, w - 1, h - 1);

		g.setColor(LIGHT_COLOR);
		g.drawLine(w, 2, w, h - 1);
		g.drawLine(2, h, w - 1, h);

		g.translate(-(x + 1), -(y + 1));
	}

	/**
	 * Returns the insets of the border.
	 * 
	 * @param c
	 *            the component for which this border insets value applies
	 */
	public Insets getBorderInsets(Component c)
	{
		return new Insets(2, 2, 4, 4);
	}

	/**
	 * Reinitialize the insets parameter with this Border's current Insets.
	 * 
	 * @param c
	 *            the component for which this border insets value applies
	 * @param insets
	 *            the object to be reinitialized
	 */
	public Insets getBorderInsets(Component c, Insets insets)
	{
		insets.left = 2;
		insets.top = 2;
		insets.right = 4;
		insets.bottom = 4;
		return insets;
	}

	public boolean isBorderOpaque()
	{
		return true;
	}
}