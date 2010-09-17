package rxr;

import java.awt.*;
import java.util.*;
import java.util.regex.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.Highlighter.HighlightPainter;

import rxr.RegexEventListener.Type;
import rxr.ext.*;

class RegexFieldListener implements DocumentListener
{
	JTextField source;
	JTextPane target;
	HashSet<RegexEventListener> listeners;

	Color highlightColor = new Color(255, 230, 0, 128);
	Color selectColor = new Color(128, 0, 255, 128);

	ArrayList<int[]> matches;
	ArrayList<int[][]> groups;
	ArrayList<Color[]> groupColors;

	boolean autoRecalc = true;

	Object selectHighlightHandle;

	public RegexFieldListener(JTextField source, JTextPane target)
	{
		this.source = source;
		this.target = target;
		listeners = new HashSet<RegexEventListener>();
	}

	public void regex()
	{
		fireRegexEvent(Type.RECALC_START);

		final String regex = source.getText();

		if(regex.equals(""))
		{
			recalcTarget(null);
			return;
		}

		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Pattern p = Pattern.compile(regex);
					Matcher m = p.matcher(target.getDocument().getText(0, target.getDocument().getLength()));
					recalcTarget(m);
				}
				catch(Exception e)
				{
					recalcTarget(null);
					fireRegexEvent(Type.BAD_PATTERN);
					return;
				}
			}
		});
	}

	public void setOutlineRange(int start, int end)
	{
		Highlighter h = target.getHighlighter();
		if(selectHighlightHandle == null)
		{
			//HighlightPainter ohp = new OutlineHighlighter(Color.BLACK).getPainter();
			HighlightPainter hp = new DefaultHighlighter.DefaultHighlightPainter(selectColor);
			try
			{
				selectHighlightHandle = h.addHighlight(start, end, hp);
			}
			catch(Exception e)
			{
				//
			}
		}
		else
		{
			try
			{
				h.changeHighlight(selectHighlightHandle, start, end);
			}
			catch(Exception e)
			{
				//
			}
		}
	}

	protected void recalcTarget(Matcher m)
	{
		selectHighlightHandle = null;
		Highlighter h = target.getHighlighter();

		//remove formatting
		try
		{
			h.removeAllHighlights();
		}
		catch(Exception e)
		{
			return;
		}

		if(m == null)
		{
			fireRegexEvent(Type.RECALC_COMPLETE);
			return;
		}

		matches = new ArrayList<int[]>();
		groups = new ArrayList<int[][]>();
		groupColors = new ArrayList<Color[]>();

		while(m.find())
		{
			int groupCount = m.groupCount();

			//grab 0th group (whole match)
			matches.add(new int[] {m.start(), m.end()});

			int[][] current = new int[groupCount][];
			Color[] ccolors = new Color[groupCount];

			for(int i = 0; i < groupCount; i++)
			{
				ccolors[i] = Color.getHSBColor(i / (float)groupCount, 1f, 1f);
				current[i] = new int[] {m.start(i + 1), m.end(i + 1)};
			}

			groups.add(current);
			groupColors.add(ccolors);
		}

		HighlightPainter hp = new DefaultHighlighter.DefaultHighlightPainter(highlightColor);

		for(int i = 0; i < matches.size(); i++)
		{
			int[][] gs = groups.get(i);
			Color[] cs = groupColors.get(i);

			for(int j = 0; j < gs.length; j++)
			{
				try
				{
					HighlightPainter ghp = new UnderlineHighlighter(cs[j]).getPainter();
					h.addHighlight(gs[j][0], gs[j][1], ghp);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					return;
				}
			}

			try
			{
				h.addHighlight(matches.get(i)[0], matches.get(i)[1], hp);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return;
			}
		}

		fireRegexEvent(Type.RECALC_COMPLETE);
	}

	private void fireRegexEvent(RegexEventListener.Type t)
	{
		for(RegexEventListener rel : listeners)
		{
			rel.regexEvent(t);
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e)
	{
		//do nothing
	}

	@Override
	public void insertUpdate(DocumentEvent e)
	{
		if(autoRecalc)
		{
			regex();
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e)
	{
		if(autoRecalc)
		{
			regex();
		}
	}

	public JTextField getSource()
	{
		return source;
	}

	public void setSource(JTextField source)
	{
		this.source = source;
	}

	public JTextPane getTarget()
	{
		return target;
	}

	public void setTarget(JTextPane target)
	{
		this.target = target;
	}

	public boolean isAutoRecalc()
	{
		return autoRecalc;
	}

	public void setAutoRecalc(boolean autoRecalc)
	{
		this.autoRecalc = autoRecalc;
	}

	public ArrayList<int[]> getMatches()
	{
		return matches;
	}

	public ArrayList<int[][]> getGroups()
	{
		return groups;
	}

	public ArrayList<Color[]> getGroupColors()
	{
		return groupColors;
	}

	public boolean addListener(RegexEventListener e)
	{
		return listeners.add(e);
	}

	public boolean removeListener(RegexEventListener e)
	{
		return listeners.remove(e);
	}
}