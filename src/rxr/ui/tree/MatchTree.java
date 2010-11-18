package rxr.ui.tree;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.tree.*;

import rxr.ui.*;
import rxr.ui.tree.MatchTreeModel.GroupNode;
import rxr.ui.tree.MatchTreeModel.MatchNode;

/**
 * A Tree used to display match results.
 * 
 * @author Josh Vinson
 */
public class MatchTree extends JTree implements RegexEventListener
{
	private static final long serialVersionUID = 1L;

	RegexFieldListener listener;

	MatchTreeModel model;

	boolean autoExpand = true;

	public MatchTree(RegexFieldListener listener)
	{
		super();
		model = new MatchTreeModel(listener);
		setModel(model);
		this.listener = listener;
		listener.addListener(this);
		setRootVisible(false);
		setCellRenderer(new MatchTreeNodeRenderer());
		setShowsRootHandles(true);
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		addTreeSelectionListener(new TreeSelectionListener()
		{
			@Override
			public void valueChanged(TreeSelectionEvent e)
			{
				int textLoc = 0;
				int replaceLoc = 0;
				Object end = e.getPath().getLastPathComponent();
				if(end instanceof MatchNode)
				{
					MatchNode node = (MatchNode)end;
					//getListener().setOutlineRange(node.start, node.end);
					getListener().setOutlineGroup(node.index);
					textLoc = node.start;
					if(getListener().isDoReplace())
					{
						if(getListener().getReplaceGroups().size() > node.index)
						{
							ArrayList<int[]> temp = getListener().getReplaceGroups().get(node.index);
							for(int[] i : temp)
							{
								if(i[0] == 0)
								{
									replaceLoc = i[1];
									break;
								}
							}
						}
					}
				}
				else if(end instanceof GroupNode)
				{
					GroupNode node = (GroupNode)end;
					getListener().setOutlineGroup(node.parent.index, node.index);
					textLoc = node.start;
					if(getListener().isDoReplace())
					{
						if(getListener().getReplaceGroups().size() > node.index)
						{
							ArrayList<int[]> temp = getListener().getReplaceGroups().get(node.parent.index);
							for(int[] i : temp)
							{
								if(i[0] == node.index)
								{
									replaceLoc = i[1];
									break;
								}
							}
						}
					}

				}
				else
				{
					return;
				}

				try
				{
					JTextComponent jtc = getListener().getTarget();
					Rectangle pos = jtc.modelToView(textLoc);
					if(pos != null)
					{
						jtc.scrollRectToVisible(pos);
					}
				}
				catch(BadLocationException e2)
				{
					//e2.printStackTrace();
				}

				if(getListener().isDoReplace())
				{
					try
					{
						JTextComponent jtc = getListener().getReplaceTarget();
						Rectangle pos = jtc.modelToView(replaceLoc);
						if(pos != null)
						{
							getListener().getReplaceTarget().scrollRectToVisible(pos);
						}
					}
					catch(BadLocationException e2)
					{
						e2.printStackTrace();
					}
				}
			}
		});
	}

	private RegexFieldListener getListener()
	{
		return listener;
	}

	@Override
	public void regexEvent(Type t)
	{
		if(t == Type.BAD_PATTERN)
		{
			model.clear();
			model.recalc();
		}
		if(t == Type.BAD_REPLACE)
		{
			model.clear();
			model.recalc();
		}
		if(t == Type.RECALC_START)
		{
			model.clear();
			model.recalc();
		}
		if(t == Type.RECALC_COMPLETE)
		{
			synchronized(model)
			{
				model.matches = listener.getMatches();
				model.groups = listener.getGroups();
				model.groupColors = listener.getGroupColors();
			}
			model.recalc();
		}
		if(autoExpand)
		{
			expand();
		}
	}

	/**
	 * Completely expands the tree.
	 */
	public void expand()
	{
		for(int i = 0; i < this.getRowCount(); i++)
		{
			this.expandRow(i);
		}
	}

	/**
	 * Completely collapses the tree.
	 */
	public void collapse()
	{
		for(int i = 0; i < this.getRowCount(); i++)
		{
			this.collapseRow(i);
		}
	}

	/**
	 * Used to render group icons as numbers and colors in the match tree.
	 */
	static class MatchTreeNodeRenderer extends DefaultTreeCellRenderer
	{
		private static final long serialVersionUID = -3491427615572210396L;
		static ColorIcon groupIcon = new ColorIcon();

		public MatchTreeNodeRenderer()
		{
			setLeafIcon(groupIcon);
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
		{
			Component def = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

			if(value instanceof GroupNode)
			{
				synchronized(groupIcon)
				{
					groupIcon.color = ((GroupNode)value).color;
					groupIcon.index = ((GroupNode)value).index;
				}
				setLeafIcon(groupIcon);
				return this;
			}

			return def;
		}

		static class ColorIcon implements Icon
		{
			Color color = Color.BLACK;
			int index = 0;

			@Override
			public synchronized void paintIcon(Component c, Graphics g, int x, int y)
			{
				((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				Color gc = new Color(color.getRed(), color.getGreen(), color.getBlue(), 255 - color.getBlue() / 3);
				g.setColor(gc);
				//g.setColor(color);
				g.fillOval(x + 1, y + 1, 14, 14);

				g.setColor(new Color(0, 0, 0, 192));
				g.drawOval(x + 1, y + 1, 14, 14);

				g.setColor(Color.BLACK);
				String s = "" + (index + 1) % 100;
				Rectangle2D b = g.getFontMetrics().getStringBounds(s, g);

				Font tempf = g.getFont();

				g.setFont(new Font("Tahoma", Font.PLAIN, 11));

				g.drawString(s, (int)((getIconWidth() - b.getWidth()) / 2) + 1, (int)(b.getHeight() + (getIconHeight() - b.getHeight()) / 2) - 2);

				g.setFont(tempf);
			}

			@Override
			public int getIconHeight()
			{
				return 16;
			}

			@Override
			public int getIconWidth()
			{
				return 16;
			}
		}
	}

	public boolean isAutoExpand()
	{
		return autoExpand;
	}

	public void setAutoExpand(boolean autoExpand)
	{
		this.autoExpand = autoExpand;
	}
}
