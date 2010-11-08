package rxr.ui.tree;

import java.awt.*;
import java.util.*;

import javax.swing.event.*;
import javax.swing.tree.*;

import rxr.*;
import rxr.ui.*;

class MatchTreeModel implements TreeModel
{
	HashSet<TreeModelListener> listeners = new HashSet<TreeModelListener>();

	ArrayList<int[]> matches = new ArrayList<int[]>();
	ArrayList<int[][]> groups = new ArrayList<int[][]>();
	ArrayList<Color> groupColors = null;

	ArrayList<MatchNode> matchNodes = new ArrayList<MatchNode>();

	RegexFieldListener lis;

	public MatchTreeModel(RegexFieldListener lis)
	{
		this.lis = lis;
	}

	public synchronized void clear()
	{
		matches = new ArrayList<int[]>();
		groups = new ArrayList<int[][]>();
		groupColors = null;
	}

	/**
	 * Recalculates matchNodes from the matches, groups, and groupColors lists.
	 */
	public synchronized void recalc()
	{

		matchNodes.clear();

		try
		{
			for(int i = 0; matches != null && i < matches.size(); i++)
			{
				String text = "";
				try
				{
					text = lis.getTarget().getDocument().getText(matches.get(i)[0], matches.get(i)[1] - matches.get(i)[0]);
				}
				catch(Exception e)
				{
					//do nothing
					e.printStackTrace(RXR.log);
				}

				MatchNode mn = new MatchNode(matches.get(i)[0], matches.get(i)[1], i, text);

				int[][] g = groups.get(i);

				for(int j = 0; j < g.length; j++)
				{
					text = "";
					try
					{
						text = lis.getTarget().getDocument().getText(g[j][0], g[j][1] - g[j][0]);
					}
					catch(Exception e)
					{
						//do nothing
						e.printStackTrace(RXR.log);
					}
					mn.groups.add(new GroupNode(mn, g[j][0], g[j][1], j, groupColors.get(j), text));
				}

				matchNodes.add(mn);
			}

		}
		catch(Exception e)
		{
			matchNodes.clear();
		}

		fireModelChanged();
	}

	public void fireModelChanged()
	{
		for(TreeModelListener l : listeners)
		{
			l.treeStructureChanged(new TreeModelEvent(this, new Object[] {this}));
		}
	}

	@Override
	public synchronized Object getChild(Object parent, int index)
	{

		if(parent == this)
		{
			if(matchNodes.size() > index)
				return matchNodes.get(index);
		}
		else if(parent instanceof MatchNode)
		{
			return ((MatchNode)parent).groups.get(index);
		}
		return null;

	}

	@Override
	public synchronized int getChildCount(Object parent)
	{

		if(parent == this)
		{
			return matchNodes.size();
		}
		else if(parent instanceof MatchNode)
		{
			return ((MatchNode)parent).groups.size();
		}
		else
		{
			return 0;
		}

	}

	@Override
	public synchronized int getIndexOfChild(Object parent, Object child)
	{

		if(parent == this)
		{
			return matchNodes.indexOf(child);
		}
		else if(parent instanceof MatchNode)
		{
			return ((MatchNode)parent).groups.indexOf(child);
		}
		else
		{
			return -1;
		}

	}

	@Override
	public Object getRoot()
	{
		return this;
	}

	@Override
	public boolean isLeaf(Object node)
	{
		if(node == this)
		{
			return false;
		}
		else if(node instanceof MatchNode)
		{
			return false;
		}
		else if(node instanceof GroupNode)
		{
			return true;
		}
		else
		{
			return true;
		}
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue)
	{
		//do nothing
	}

	@Override
	public void addTreeModelListener(TreeModelListener l)
	{
		listeners.add(l);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l)
	{
		listeners.remove(l);
	}

	/**
	 * Represents a single match for a regex, in the target document. Contains
	 * information on the whole match, as well as information for each capture
	 * group within the match.
	 */
	class MatchNode
	{
		int start;
		int end;
		ArrayList<GroupNode> groups;
		int index;
		String text;

		public MatchNode(int start, int end, int index, String text)
		{
			super();
			this.start = start;
			this.end = end;
			this.index = index;
			groups = new ArrayList<GroupNode>();
			this.text = text;
		}

		@Override
		public String toString()
		{
			return (index + 1) + ": " + text.substring(0, Math.min(text.length(), 50));
		}
	}

	/**
	 * Represents one group in a single match.
	 */
	class GroupNode
	{
		public GroupNode(MatchNode parent, int start, int end, int index, Color color, String text)
		{
			super();
			this.start = start;
			this.end = end;
			this.index = index;
			this.color = color;
			this.text = text;
			this.parent = parent;
		}

		int start;
		int end;
		int index;
		Color color;
		String text;
		MatchNode parent;

		@Override
		public String toString()
		{
			return text;
		}
	}
}