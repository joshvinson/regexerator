package rxr;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;

import rxr.util.*;
import rxr.util.component.*;

public class MainPanel extends JPanel
{
	//components
	JTextField regexField;
	JTextPane textField;
	JCheckBox autoRefreshCheck;
	JCheckBox autoExpandCheck;
	JLabel regexStatusLabel;
	JButton runButton;
	MatchTree matchTree;
	JButton expandButton;
	JButton collapseButton;
	JLabel statusLabel;
	JCheckBox replaceCheck;
	JTextPane replaceField;
	JSplitPane splitPane2;

	RegexFieldListener listener;

	public MainPanel()
	{
		//--icons init--
		final ImageIcon startIcon = new ImageIcon(RXR.load("media/19127.thread_view.gif"));
		final ImageIcon doneIcon = new ImageIcon(RXR.load("media/14073.complete_status.gif"));
		final ImageIcon errorIcon = new ImageIcon(RXR.load("media/10448.error_obj.gif"));
		final ImageIcon runIcon = new ImageIcon(RXR.load("media/17155.run_exc.png"));

		//--components init--
		regexField = new JTextField();
		textField = new JTextPane();
		regexStatusLabel = new JLabel("Ready");
		autoRefreshCheck = new JCheckBox("Auto Refresh", true);
		runButton = new JButton("Run", runIcon);
		autoExpandCheck = new JCheckBox("Auto Expand Tree", false);
		expandButton = new JButton("Expand");
		collapseButton = new JButton("Collapse");
		statusLabel = new JLabel();
		replaceCheck = new JCheckBox("Replace", false);
		replaceField = new JTextPane();

		//--components configure--
		listener = new RegexFieldListener(regexField, textField);
		regexField.getDocument().addDocumentListener(listener);
		textField.getDocument().addDocumentListener(listener);
		textField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_TAB)
				{
					e.consume();
					regexField.grabFocus();
				}
			}
		});

		matchTree = new MatchTree(listener);

		listener.addListener(new RegexEventListener()
		{
			@Override
			public void regexEvent(Type t)
			{
				switch(t)
				{
				case RECALC_START:
					regexStatusLabel.setIcon(startIcon);
					regexStatusLabel.setText("Working");
					break;
				case RECALC_COMPLETE:
					regexStatusLabel.setIcon(doneIcon);
					regexStatusLabel.setText("Ready");
					if(listener.matches != null)
					{
						statusLabel.setText(listener.matches.size() + " matches found.");
					}
					break;
				case BAD_PATTERN:
					regexStatusLabel.setIcon(errorIcon);
					regexStatusLabel.setText("Error");
					break;
				default:
					regexStatusLabel.setIcon(null);
					break;
				}
			}
		});

		regexStatusLabel.setIcon(doneIcon);
		regexStatusLabel.setOpaque(true);
		regexStatusLabel.setBackground(Color.WHITE);
		regexStatusLabel.setHorizontalTextPosition(SwingConstants.LEFT);

		autoRefreshCheck.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				boolean auto = autoRefreshCheck.isSelected();
				listener.setAutoRecalc(auto);
				runButton.setEnabled(!auto);
				if(auto)
				{
					listener.regex();
				}
			}
		});

		autoRefreshCheck.setFocusable(false);

		autoExpandCheck.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				boolean auto = autoExpandCheck.isSelected();
				matchTree.autoExpand = auto;
				if(auto)
				{
					matchTree.expand();
				}
			}
		});
		autoExpandCheck.setFocusable(false);

		runButton.setFocusable(false);
		runButton.setEnabled(false);
		runButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				listener.regex();
			}
		});

		expandButton.setFocusable(false);
		expandButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				matchTree.expand();
			}
		});

		collapseButton.setFocusable(false);
		collapseButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				matchTree.collapse();
			}
		});

		statusLabel.setFont(new Font("", Font.BOLD, 12));

		replaceCheck.setFocusable(false);
		replaceField.setEditable(false);

		//--layout--
		setLayout(new BorderLayout());

		//boxa = main north box
		Box boxa = Box.createVerticalBox();

		//boxaa = regex field/status label
		Box boxaa = Box.createHorizontalBox();
		boxaa.add(regexField);
		boxaa.add(regexStatusLabel);

		//boxab = options
		Box boxab = Box.createHorizontalBox();
		boxab.add(runButton);
		boxab.add(autoRefreshCheck);
		boxab.add(replaceCheck);
		boxab.add(Box.createHorizontalGlue());
		boxab.add(statusLabel);
		boxab.add(Box.createHorizontalGlue());
		boxab.add(autoExpandCheck);
		boxab.add(expandButton);
		boxab.add(collapseButton);

		boxa.add(boxaa);
		boxa.add(boxab);

		//scrollpanes
		final JScrollPane jspa = new JScrollPane(textField);
		final JScrollPane jspaa = new JScrollPane(replaceField);
		JScrollPane jspb = new JScrollPane(matchTree);

		//splitPane = text and tree
		final JSplitPane splitPane = new JSplitPane();

		splitPane.setBorder(Util.Layout.getEmptyBorder());
		BasicSplitPaneUI ui = (BasicSplitPaneUI)splitPane.getUI();
		ui.getDivider().setBorder(Util.Layout.getEmptyBorder());
		ui.getDivider().setDividerSize(3);
		splitPane.setContinuousLayout(true);
		splitPane.resetToPreferredSizes();
		splitPane.setResizeWeight(.7);

		//splitPane2 = test and replace
		splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		splitPane2.setBorder(Util.Layout.getEmptyBorder());
		ui = (BasicSplitPaneUI)splitPane2.getUI();
		ui.getDivider().setBorder(Util.Layout.getEmptyBorder());
		ui.getDivider().setDividerSize(3);
		splitPane2.setContinuousLayout(true);
		splitPane2.resetToPreferredSizes();
		splitPane2.setResizeWeight(.5);

		//boxb = text pane
		//Box boxb = Box.createVerticalBox();
		//boxb.add(jspa);
		//boxb.add(jspaa);
		splitPane2.setTopComponent(jspa);
		splitPane2.setBottomComponent(jspaa);

		//splitPane.setLeftComponent(boxb);
		splitPane.setLeftComponent(splitPane2);

		//boxc = tree
		Box boxc = Box.createVerticalBox();
		boxc.add(jspb);

		splitPane.setRightComponent(boxc);

		//borders
		regexField.setBorder(Util.Layout.getEmptyBorder(4));
		textField.setBorder(Util.Layout.getEmptyBorder(4));
		regexStatusLabel.setBorder(Util.Layout.getEmptyBorder(4));
		matchTree.setBorder(Util.Layout.getEmptyBorder(4));
		autoRefreshCheck.setBorder(new EmptyBorder(2, 2, 2, 2));
		runButton.setBorder(new EmptyBorder(3, 3, 3, 5));
		expandButton.setBorder(new EmptyBorder(3, 5, 3, 5));
		collapseButton.setBorder(new EmptyBorder(3, 5, 3, 5));

		//jspa.setBorder(Util.Layout.getEmptyBorder(0));
		//jspb.setBorder(Util.Layout.getEmptyBorder(0));

		TitleBarDropShadowBorder bordera = new TitleBarDropShadowBorder("Regular Expression", null);
		TitleBarDropShadowBorder borderb = new TitleBarDropShadowBorder("Test Text", null);
		TitleBarDropShadowBorder borderc = new TitleBarDropShadowBorder("Match Tree", null);
		TitleBarDropShadowBorder borderd = new TitleBarDropShadowBorder("Replace Result", null);

		bordera.getTitleBarBorder().setLeftColor(new Color(170, 220, 170));
		borderb.getTitleBarBorder().setLeftColor(new Color(170, 170, 220));
		borderc.getTitleBarBorder().setLeftColor(new Color(220, 170, 170));
		borderd.getTitleBarBorder().setLeftColor(new Color(170, 170, 220));

		boxa.setBorder(new EmptyBorder(0, 0, 0, 0));
		boxaa.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0), bordera));
		boxab.setBorder(new EmptyBorder(0, 3, 0, 3));
		jspa.setBorder(borderb);
		jspb.setBorder(borderc);
		jspaa.setBorder(borderd);

		setBorder(Util.Layout.getEmptyBorder(1));

		//add
		add(boxa, BorderLayout.NORTH);
		add(splitPane, BorderLayout.CENTER);

		replaceCheck.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(replaceCheck.isSelected() && splitPane.getLeftComponent() == splitPane2)
				{
					splitPane.remove(splitPane2);
					splitPane2.remove(jspa);
					splitPane.setLeftComponent(jspa);
					listener.setDoReplace(true);
				}
				else
				{
					splitPane.remove(jspa);
					splitPane2.setTopComponent(jspa);
					splitPane.setLeftComponent(splitPane2);
					listener.setDoReplace(false);
				}
			}
		});
	}
}
