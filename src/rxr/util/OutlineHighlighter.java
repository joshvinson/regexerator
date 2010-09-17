package rxr.util;

import java.awt.*;

import javax.swing.text.*;

public class OutlineHighlighter extends DefaultHighlighter
{
	protected Highlighter.HighlightPainter painter;

	public OutlineHighlighter(Color c)
	{
		painter = new OutlineHighlightPainter(c);
	}

	// Convenience method to add a highlight with
	// the default painter.
	public Object addHighlight(int p0, int p1) throws BadLocationException
	{
		return addHighlight(p0, p1, painter);
	}

	public void setDrawsLayeredHighlights(boolean newValue)
	{
		// Illegal if false - we only support layered highlights
		if(newValue == false)
		{
			throw new IllegalArgumentException("OutlineHighlighter only draws layered highlights");
		}
		super.setDrawsLayeredHighlights(true);
	}

	// Painter for underlined highlights
	public class OutlineHighlightPainter extends LayeredHighlighter.LayerPainter
	{
		public OutlineHighlightPainter(Color c)
		{
			color = c;
		}

		public void paint(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c)
		{
			// Do nothing: this method will never be called
		}

		public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c, View view)
		{
			g.setColor(color == null ? c.getSelectionColor() : color);

			Rectangle alloc = null;
			if(offs0 == view.getStartOffset() && offs1 == view.getEndOffset())
			{
				if(bounds instanceof Rectangle)
				{
					alloc = (Rectangle)bounds;
				}
				else
				{
					alloc = bounds.getBounds();
				}
			}
			else
			{
				try
				{
					Shape shape = view.modelToView(offs0, Position.Bias.Forward, offs1, Position.Bias.Backward, bounds);
					alloc = (shape instanceof Rectangle) ? (Rectangle)shape : shape.getBounds();
				}
				catch(BadLocationException e)
				{
					return null;
				}
			}

			FontMetrics fm = c.getFontMetrics(c.getFont());
			int baseline = alloc.y + alloc.height - fm.getDescent() + 1;
			g.drawLine(alloc.x, alloc.y, alloc.x + alloc.width, alloc.y);
			g.drawLine(alloc.x, alloc.y - 1, alloc.x + alloc.width, alloc.y - 1);
			//g.drawRect(alloc.x, alloc.y, alloc.width, alloc.height);

			return alloc;
		}

		protected Color color; // The color for the underline
	}

	public Highlighter.HighlightPainter getPainter()
	{
		return painter;
	}
}
