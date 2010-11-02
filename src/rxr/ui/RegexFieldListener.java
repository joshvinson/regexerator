package rxr.ui;

import java.awt.*;
import java.util.*;
import java.util.regex.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.Highlighter.HighlightPainter;

import rxr.*;
import rxr.component.*;
import rxr.ui.RegexEventListener.Type;
import rxr.util.*;

/**
 * RegexFieldListener listens for changes in a Document, and reapplies a regular
 * expression whenever a change occurs.
 */
class RegexFieldListener implements DocumentListener
{
	protected JTextField source;
	protected JTextPane target;
	protected JTextField replaceSource;
	protected JTextPane replaceTarget;
	protected HashSet<RegexEventListener> listeners;

	protected Color highlightColor = ColorUtil.decode(RXR.get("rxr.ui.matchHighlightColor"));
	protected Color selectColor = ColorUtil.decode(RXR.get("rxr.ui.selectHighlightColor"));

	protected ArrayList<int[]> matches;
	protected ArrayList<int[][]> groups;
	Color[] groupColors;
	protected ArrayList<int[]> replaces;
	protected ArrayList<ArrayList<int[]>> replaceGroups;

	protected String sourceText;
	protected String replaceResult;

	protected boolean autoRecalc = true;

	boolean doReplace = false;

	protected Object selectHighlightHandle;
	protected HashSet<Object> replaceHighlightHandles;

	protected int progress;

	Thread thread;

	int maxMatches = Integer.parseInt(RXR.get("rxr.regex.maxMatches"));

	enum RecalcResult
	{
		SUCCESS, FAIL_REPLACE, FAIL_TOO_MANY_MATCHES
	}

	/**
	 * Creates a RegexFieldListener that takes a regex from source, and applies
	 * it to target.
	 * 
	 * @param source
	 *            the component to get the regex string from
	 * @param target
	 *            the component to apply the regex to
	 */
	public RegexFieldListener(JTextField source, JTextPane target, JTextField replaceSource, JTextPane replaceTarget)
	{
		this.source = source;
		this.target = target;
		this.replaceSource = replaceSource;
		this.replaceTarget = replaceTarget;
		listeners = new HashSet<RegexEventListener>();
		replaceHighlightHandles = new HashSet<Object>();
	}

	/**
	 * Grabs a regex string from the source component, attempts to compile it,
	 * and call recalcTarget(). At least regex events will be sent to listeners
	 * during the execution of this method.
	 */
	@SuppressWarnings("deprecation")
	public void regex()
	{
		if(thread != null && thread.isAlive())
		{
			//use of Thread.stop() is intended here, and (as far as I can tell)
			//won't cause any problems.
			thread.stop();
			reset();
			fireRegexEvent(Type.RECALC_START);
		}

		thread = new Thread()
		{
			@Override
			public void run()
			{
				setPriority(MIN_PRIORITY);
				fireRegexEvent(Type.RECALC_START);

				final String regex = source.getText();

				//don't bother with empty regex
				if(regex.equals(""))
				{
					reset();
					update();
					progress = 0;
					fireRegexEvent(Type.RECALC_PROGRESS);
					fireRegexEvent(Type.BAD_PATTERN);
					return;
				}

				Pattern p;
				Matcher m;

				//attempt to compile pattern
				try
				{
					p = Pattern.compile(regex);
				}
				catch(Exception e)
				{
					//pattern was invalid, so clear data, then update ui
					reset();
					update();
					progress = 0;
					fireRegexEvent(Type.RECALC_PROGRESS);
					fireRegexEvent(Type.BAD_PATTERN);
					return;
				}

				//pattern is good, so try the match
				try
				{
					sourceText = target.getDocument().getText(0, target.getDocument().getLength());
					m = p.matcher(sourceText);
				}
				catch(BadLocationException e)
				{
					//should never get here
					WindowUtil.error(e, "Error getting test text from component", false);
					reset();
					update();
					return;
				}

				//matcher is good, so grab the matching data and try the replace (if doReplace)
				RecalcResult result = recalc(m);

				if(result.equals(RecalcResult.SUCCESS))
				{
					//replace was successful
					update();
					fireRegexEvent(Type.RECALC_COMPLETE);
					return;
				}
				else if(result.equals(RecalcResult.FAIL_REPLACE))
				{
					//replace failed
					reset();
					update();
					progress = 0;
					fireRegexEvent(Type.RECALC_PROGRESS);
					fireRegexEvent(Type.BAD_REPLACE);
					return;
				}
				else if(result.equals(RecalcResult.FAIL_TOO_MANY_MATCHES))
				{
					//too many matches
					reset();
					update();
					progress = 0;
					fireRegexEvent(Type.RECALC_PROGRESS);
					fireRegexEvent(Type.TOO_MANY_MATCHES);
					return;
				}
			}
		};
		thread.start();
	}

	public void setOutlineGroup(int match)
	{
		setOutlineGroup(match, -1);
	}

	public void setOutlineGroup(int match, int group)
	{
		//do target
		int start;
		int end;

		if(matches == null || matches.size() == 0)
		{
			return;
		}

		if(group == -1)
		{
			start = matches.get(match)[0];
			end = matches.get(match)[1];
		}
		else
		{
			start = groups.get(match)[group][0];
			end = groups.get(match)[group][1];
		}

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
				//do nothing
				e.printStackTrace(RXR.log);
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
				//do nothing
				e.printStackTrace(RXR.log);
			}
		}

		//do replace
		if(doReplace)
		{
			group++;
			h = replaceTarget.getHighlighter();
			//remove replace highlights
			for(Object o : replaceHighlightHandles)
			{
				h.removeHighlight(o);
			}
			replaceHighlightHandles.clear();
			HighlightPainter hp = new DefaultHighlighter.DefaultHighlightPainter(selectColor);
			for(int i = 0; i < replaceGroups.get(match).size(); i++)
			{
				if(replaceGroups.get(match).get(i)[0] == group)
				{
					start = replaceGroups.get(match).get(i)[1];
					end = replaceGroups.get(match).get(i)[2];
					try
					{
						replaceHighlightHandles.add(h.addHighlight(start, end, hp));
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Changes the specific highlight on the target document. This will show up
	 * as a different color than the highlighting for each match.
	 * 
	 * @param start
	 *            the start index in the target document
	 * @param end
	 *            the end index in the target document
	 */
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

	/**
	 * Deletes any match data that has been collected.
	 */
	protected void reset()
	{
		matches = new ArrayList<int[]>();
		groups = new ArrayList<int[][]>();
		groupColors = null;
		replaces = new ArrayList<int[]>();
		replaceGroups = new ArrayList<ArrayList<int[]>>();
		replaceResult = "";
	}

	/**
	 * Recalculate the matches, groups, and groupColors lists based on the data
	 * in m. Matches and groups are calculated from m by removing group 0 and
	 * making that the match for that match number. The groups list is
	 * zero-indexed, starting with the 1st, not 0th group.
	 * 
	 * @param m
	 *            the Matcher object to get match and group data from
	 */
	protected RecalcResult recalc(Matcher m)
	{
		matches = new ArrayList<int[]>();
		groups = new ArrayList<int[][]>();
		replaces = new ArrayList<int[]>();
		replaceGroups = new ArrayList<ArrayList<int[]>>();

		groupColors = null;

		StringBuffer replaceSB = new StringBuffer();
		String replaceStr = replaceSource.getText();

		int prevProgress = 0;
		progress = 0;
		fireRegexEvent(Type.RECALC_PROGRESS);

		int matchCount = 0;

		while(m.find())
		{
			matchCount++;
			if(matchCount > maxMatches)
			{
				progress = 100;
				fireRegexEvent(Type.RECALC_PROGRESS);
				return RecalcResult.FAIL_TOO_MANY_MATCHES;
			}

			int groupCount = m.groupCount();

			//grab 0th group (whole match)
			matches.add(new int[] {m.start(), m.end()});

			int[][] current = new int[groupCount][];

			for(int i = 0; i < groupCount; i++)
			{
				current[i] = new int[] {m.start(i + 1), m.end(i + 1)};
			}

			groups.add(current);
			if(groupColors == null)
			{
				groupColors = new Color[groupCount];
				for(int i = 0; i < groupCount; i++)
				{
					groupColors[i] = Color.getHSBColor(i / (float)groupCount, 1f, 1f);
				}
			}

			if(doReplace)
			{
				try
				{
					int[] replace = new int[2];
					m.appendReplacement(replaceSB, replaceStr);
					int replaceLength = getReplaceString(m, replaceStr).length();
					replace[0] = replaceSB.length() - replaceLength;
					replace[1] = replaceSB.length();
					replaces.add(replace);
					replaceGroups.add(getReplaceGroups(m, replaceStr, replace[0]));
				}
				catch(Exception e)
				{
					return RecalcResult.FAIL_REPLACE;
				}
			}

			if(sourceText.length() != 0)
			{
				progress = 100 * m.end() / sourceText.length();
			}
			if(progress != prevProgress)
			{
				fireRegexEvent(Type.RECALC_PROGRESS);
				prevProgress = progress;
			}
		}

		m.appendTail(replaceSB);

		replaceResult = replaceSB.toString();

		progress = 100;
		fireRegexEvent(Type.RECALC_PROGRESS);

		return RecalcResult.SUCCESS;
	}

	/**
	 * Gets the result of a replacement based on the state in the supplied
	 * Matcher. The code is copied directly from the appendReplacement method in
	 * the java.util.regex.Matcher class.
	 * 
	 * @param m
	 *            The Matcher to replace from
	 * @param replacement
	 *            the replacement string, which can include group references
	 *            ($1, etc)
	 * @return the replacement string with group references replaces by their
	 *         values
	 */
	public String getReplaceString(Matcher m, String replacement)
	{
		// Process substitution string to replace group references with groups
		int cursor = 0;
		StringBuffer result = new StringBuffer();

		while(cursor < replacement.length())
		{
			char nextChar = replacement.charAt(cursor);
			if(nextChar == '\\')
			{
				cursor++;
				nextChar = replacement.charAt(cursor);
				result.append(nextChar);
				cursor++;
			}
			else if(nextChar == '$')
			{
				// Skip past $
				cursor++;

				// The first number is always a group
				int refNum = (int)replacement.charAt(cursor) - '0';
				if((refNum < 0) || (refNum > 9))
					throw new IllegalArgumentException("Illegal group reference");
				cursor++;

				// Capture the largest legal group string
				boolean done = false;
				while(!done)
				{
					if(cursor >= replacement.length())
					{
						break;
					}
					int nextDigit = replacement.charAt(cursor) - '0';
					if((nextDigit < 0) || (nextDigit > 9))
					{ // not a number
						break;
					}
					int newRefNum = (refNum * 10) + nextDigit;
					if(m.groupCount() < newRefNum)
					{
						done = true;
					}
					else
					{
						refNum = newRefNum;
						cursor++;
					}
				}

				// Append group
				if(m.group(refNum) != null)
					result.append(m.group(refNum));
			}
			else
			{
				result.append(nextChar);
				cursor++;
			}
		}
		return result.toString();
	}

	/**
	 * Gets the indices of groups in a replacement based on the state in the
	 * supplied Matcher. The code is copied directly from the appendReplacement
	 * method in the java.util.regex.Matcher class.
	 * 
	 * @param m
	 *            The Matcher to replace from
	 * @param replacement
	 *            the replacement string, which can include group references
	 *            ($1, etc)
	 * @param offset
	 *            the offset to add to indices. Usually this will be the
	 *            starting index of the match in a larger string.
	 * @return all the indices of the replacements made. Each entry in the top
	 *         array is a three entry array. The first entry is the group
	 *         number. The second and third are the start and end indices of the
	 *         replaced group in the result.
	 */
	public ArrayList<int[]> getReplaceGroups(Matcher m, String replacement, int offset)
	{
		ArrayList<int[]> index = new ArrayList<int[]>();

		// Process substitution string to replace group references with groups
		int cursor = 0;
		StringBuffer result = new StringBuffer();

		while(cursor < replacement.length())
		{
			char nextChar = replacement.charAt(cursor);
			if(nextChar == '\\')
			{
				cursor++;
				nextChar = replacement.charAt(cursor);
				result.append(nextChar);
				cursor++;
			}
			else if(nextChar == '$')
			{
				// Skip past $
				cursor++;

				// The first number is always a group
				int refNum = (int)replacement.charAt(cursor) - '0';
				if((refNum < 0) || (refNum > 9))
					throw new IllegalArgumentException("Illegal group reference");
				cursor++;

				// Capture the largest legal group string
				boolean done = false;
				while(!done)
				{
					if(cursor >= replacement.length())
					{
						break;
					}
					int nextDigit = replacement.charAt(cursor) - '0';
					if((nextDigit < 0) || (nextDigit > 9))
					{ // not a number
						break;
					}
					int newRefNum = (refNum * 10) + nextDigit;
					if(m.groupCount() < newRefNum)
					{
						done = true;
					}
					else
					{
						refNum = newRefNum;
						cursor++;
					}
				}

				// Append group
				if(m.group(refNum) != null)
				{
					int[] entry = new int[3];
					entry[0] = refNum;
					entry[1] = offset + result.length();
					result.append(m.group(refNum));
					entry[2] = offset + result.length();
					index.add(entry);
				}
			}
			else
			{
				result.append(nextChar);
				cursor++;
			}
		}
		index.add(new int[] {0, offset, offset + result.length()});
		return index;
	}

	/**
	 * Updates target and replaceTarget with the current match data. target is
	 * rehighlighted, and replaceTarget is filled with the result of the
	 * replacement.
	 */
	protected void update()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				//fill replaceTarget
				replaceTarget.setText(replaceResult);

				selectHighlightHandle = null;
				Highlighter h = target.getHighlighter();
				Highlighter h2 = replaceTarget.getHighlighter();

				//remove formatting
				try
				{
					h.removeAllHighlights();
					h2.removeAllHighlights();
				}
				catch(Exception e)
				{
					return;
				}

				doHighlight();
			}
		});
	}

	/**
	 * Applies the match and group data from matches, groups, and groupColors to
	 * the target document.
	 */
	private void doHighlight()
	{
		//highlight target
		Highlighter h = target.getHighlighter();
		HighlightPainter hp = new DefaultHighlighter.DefaultHighlightPainter(highlightColor);

		for(int i = 0; i < matches.size(); i++)
		{
			int[][] gs = groups.get(i);

			for(int j = 0; j < gs.length; j++)
			{
				try
				{
					HighlightPainter ghp = new UnderlineHighlightPainter(groupColors[j]);
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

		//highlight replaceTarget

		if(doReplace)
		{
			h = replaceTarget.getHighlighter();

			for(int i = 0; i < replaceGroups.size(); i++)
			{
				ArrayList<int[]> gs = replaceGroups.get(i);

				for(int j = 0; j < gs.size(); j++)
				{
					try
					{
						if(gs.get(j)[0] > 0) //ignore whole group reference
						{
							HighlightPainter ghp = new UnderlineHighlightPainter(groupColors[gs.get(j)[0] - 1]);
							h.addHighlight(gs.get(j)[1], gs.get(j)[2], ghp);
						}
					}
					catch(BadLocationException e)
					{
						WindowUtil.error(e, "Error highlighting replace result", false);
						return;
					}
				}
			}

			for(int i = 0; i < replaces.size(); i++)
			{
				try
				{
					h.addHighlight(replaces.get(i)[0], replaces.get(i)[1], hp);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Notifies all listeners of a regex event.
	 * 
	 * @param t
	 *            the type of regex event to send
	 */
	protected void fireRegexEvent(RegexEventListener.Type t)
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

	/**
	 * @return the source component
	 */
	public JTextField getSource()
	{
		return source;
	}

	public void setSource(JTextField source)
	{
		this.source = source;
	}

	/**
	 * @return the target component
	 */
	public JTextPane getTarget()
	{
		return target;
	}

	public void setTarget(JTextPane target)
	{
		this.target = target;
	}

	/**
	 * @return if the regex is automatically reapplied on changes to the regex
	 *         or target document
	 */
	public boolean isAutoRecalc()
	{
		return autoRecalc;
	}

	public void setAutoRecalc(boolean autoRecalc)
	{
		this.autoRecalc = autoRecalc;
	}

	/**
	 * @return a list of whole regex matches from the last calculated match
	 */
	public ArrayList<int[]> getMatches()
	{
		return matches;
	}

	/**
	 * @return a list of each set of groups from the last calculated match
	 */
	public ArrayList<int[][]> getGroups()
	{
		return groups;
	}

	/**
	 * @return a list of each set of colors used for each group
	 */
	public Color[] getGroupColors()
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

	public JTextPane getReplaceTarget()
	{
		return replaceTarget;
	}

	public void setReplaceTarget(JTextPane replaceTarget)
	{
		this.replaceTarget = replaceTarget;
	}

	public boolean isDoReplace()
	{
		return doReplace;
	}

	public void setDoReplace(boolean doReplace)
	{
		this.doReplace = doReplace;
	}
}