package rxr.ui.doc;

import java.net.*;
import java.util.*;

import javax.swing.event.*;
import javax.swing.tree.*;

/**
 * TreeModel used by DocBrowser.
 * 
 * @author Josh Vinson
 */
public class DocBrowserTreeModel implements TreeModel
{
	HashSet<TreeModelListener> listeners;

	URLNode root;

	public DocBrowserTreeModel(URL root)
	{
		listeners = new HashSet<TreeModelListener>();
	}

	public void setRoot(URL url)
	{
		root = URLNode.makeRoot(url);
		if(root == null)
		{
			throw new RuntimeException("Invalid URL: " + url);
		}
		root.fillChildren();
		root.init();

		fireModelChanged();
	}

	public URLNode getNodeForURL(URL url)
	{
		return root.getNodeForURL(url);
	}

	public Object getChild(Object parent, int index)
	{
		URLNode n = (URLNode)parent;
		return n.getChildren().get(index);
	}

	@Override
	public int getChildCount(Object parent)
	{
		URLNode n = (URLNode)parent;
		return n.getChildren().size();
	}

	@Override
	public int getIndexOfChild(Object parent, Object child)
	{
		URLNode n = (URLNode)parent;
		return n.getChildren().indexOf(child);
	}

	@Override
	public Object getRoot()
	{
		return root;
	}

	@Override
	public boolean isLeaf(Object node)
	{
		URLNode n = (URLNode)node;
		return !n.isDir();
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

	public void fireModelChanged()
	{
		for(TreeModelListener l : listeners)
		{
			l.treeStructureChanged(new TreeModelEvent(this, new Object[] {this}));
		}
	}
}
