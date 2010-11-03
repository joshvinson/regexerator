package rxr.component.doc;

import java.awt.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.*;
import javax.swing.tree.*;

import rxr.*;
import rxr.component.*;
import rxr.util.*;

public class DocBrowser extends JPanel
{
	private static final long serialVersionUID = 1L;

	public static final Pattern urlPattern = Pattern.compile("^(?:jar:)?file:(.*?)(?:!/(.*))?$");

	JTextPane pane;
	JTree nav;

	DocBrowserTreeModel model;

	TitleBarDropShadowBorder paneBorder;

	public DocBrowser(URL root)
	{
		final ImageIcon contentsIcon = new ImageIcon(RXR.load("res/media/13165.help.gif"));

		setLayout(new GridLayout(1, 0));

		model = new DocBrowserTreeModel(root);

		nav = new JTree(model);

		nav.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		model.setRoot(root);

		pane = new JTextPane();

		pane.setEditable(false);

		pane.setBackground(Color.WHITE);

		pane.addHyperlinkListener(new HyperlinkListener()
		{
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e)
			{
				if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
				{
					setSelection(model.getNodeForURL(e.getURL()));
				}
			}
		});

		JScrollPane navJsp = new JScrollPane(nav);
		final JScrollPane paneJsp = new JScrollPane(pane);

		TitleBarDropShadowBorder navBorder = new TitleBarDropShadowBorder("Help Contents", contentsIcon);
		navBorder.getTitleBarBorder().setLeftColor(new Color(240, 180, 110));
		navJsp.setBorder(navBorder);

		paneBorder = new TitleBarDropShadowBorder("", null);
		paneJsp.setBorder(paneBorder);

		navJsp.setMinimumSize(new Dimension(140, 100));

		nav.setBorder(new EmptyBorder(2, 2, 2, 2));
		pane.setBorder(new EmptyBorder(2, 2, 2, 2));

		setBorder(new EmptyBorder(1, 1, 1, 1));

		JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, navJsp, paneJsp);

		jsp.setBorder(LayoutUtil.getEmptyBorder());
		BasicSplitPaneUI ui = (BasicSplitPaneUI)jsp.getUI();
		ui.getDivider().setBorder(LayoutUtil.getEmptyBorder());
		ui.getDivider().setDividerSize(3);
		jsp.setContinuousLayout(true);
		jsp.resetToPreferredSizes();
		jsp.setResizeWeight(0);

		nav.addTreeSelectionListener(new TreeSelectionListener()
		{
			@Override
			public void valueChanged(TreeSelectionEvent e)
			{
				URLNode n = (URLNode)e.getPath().getLastPathComponent();
				if(!n.isDir())
				{
					setPage(n.url);
					paneBorder.getTitleBarBorder().setLabel(n.title);
					paneJsp.repaint();
				}
				else
				{
					if(n.redirect != null)
					{
						setPage(n.redirect.url);
						paneBorder.getTitleBarBorder().setLabel(n.redirect.title);
						paneJsp.repaint();
					}
				}
			}
		});

		add(jsp);

		setSelection((URLNode)model.getRoot());
	}

	public void setSelection(URLNode n)
	{
		ArrayList<URLNode> path = new ArrayList<URLNode>();
		while(n != null)
		{
			path.add(0, n);
			n = n.parent;
		}
		nav.setSelectionPath(new TreePath(path.toArray()));
	}

	public void setPage(String s)
	{
		try
		{
			setPage(new URL(s));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void setPage(URL url)
	{
		try
		{
			pane.setPage(url);

			/*System.out.println(pane.getText());
			System.out.println("-----------------");
			System.out.println(pane.getStyledDocument().getText(0, pane.getDocument().getLength()));

			Pattern p = Pattern.compile("%([^%]+)%");
			StringBuffer sb = new StringBuffer();

			Matcher m = p.matcher(pane.getText());
			while(m.find())
			{
				String rep = RXR.get(m.group(1));
				m.appendReplacement(sb, rep == null ? m.group(1) : rep);
			}
			m.appendTail(sb);

			//pane.setText(sb.toString());*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
