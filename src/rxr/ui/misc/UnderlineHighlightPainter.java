package rxr.ui.misc;

import java.awt.*;

import javax.swing.text.*;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;

/**
 * A simple text highlighter that underlines characters in a selectable color.
 * 
 * @author Josh Vinson
 */
public class UnderlineHighlightPainter extends DefaultHighlightPainter
{
	public UnderlineHighlightPainter(Color color)
	{
		super(color);
	}

	@Override
	public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c, View view)
	{
		Graphics2D window = (Graphics2D)g;

		Rectangle rect;
		try
		{
			Shape shape = view.modelToView(offs0, Position.Bias.Forward, offs1, Position.Bias.Backward, bounds);
			rect = shape.getBounds();
		}
		catch(BadLocationException e)
		{
			return null;
		}

		FontMetrics fm = c.getFontMetrics(c.getFont());

		int x0 = rect.x;
		int x1 = rect.x + rect.width - 1;
		int y = rect.y + rect.height - fm.getDescent() + 1;

		window.setColor(getColor());
		window.drawLine(x0, y, x1, y);
		window.drawLine(x0, y + 1, x1, y + 1);

		return new Rectangle(x0, y, x1 - x0 + 1, 2);
	}
}