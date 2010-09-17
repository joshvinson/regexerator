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
				switch (t)
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
		
		statusLabel.setFont(new Font("",Font.BOLD,12));

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
		boxab.add(Box.createHorizontalGlue());
		boxab.add(statusLabel);
		boxab.add(Box.createHorizontalGlue());
		boxab.add(autoExpandCheck);
		boxab.add(expandButton);
		boxab.add(collapseButton);

		boxa.add(boxaa);
		boxa.add(boxab);

		//boxb = text pane
		Box boxb = Box.createVerticalBox();

		//boxc = tree
		Box boxc = Box.createVerticalBox();

		JSplitPane splitPane = new JSplitPane();
		JScrollPane jspa = new JScrollPane(textField);
		JScrollPane jspb = new JScrollPane(matchTree);

		splitPane.setBorder(Util.Layout.getEmptyBorder());
		BasicSplitPaneUI ui = (BasicSplitPaneUI)splitPane.getUI();
		ui.getDivider().setBorder(Util.Layout.getEmptyBorder());
		ui.getDivider().setDividerSize(3);
		splitPane.setContinuousLayout(true);
		splitPane.resetToPreferredSizes();
		splitPane.setResizeWeight(.7);

		splitPane.setLeftComponent(boxb);
		splitPane.setRightComponent(boxc);

		boxb.add(jspa);
		boxc.add(jspb);

		//borders
		regexField.setBorder(Util.Layout.getEmptyBorder(4));
		textField.setBorder(Util.Layout.getEmptyBorder(4));
		regexStatusLabel.setBorder(Util.Layout.getEmptyBorder(4));
		matchTree.setBorder(Util.Layout.getEmptyBorder(4));
		autoRefreshCheck.setBorder(new EmptyBorder(2, 2, 2, 2));
		runButton.setBorder(new EmptyBorder(3, 3, 3, 5));

		jspa.setBorder(Util.Layout.getEmptyBorder(0));
		jspb.setBorder(Util.Layout.getEmptyBorder(0));
		TitleBarDropShadowBorder borderaa = new TitleBarDropShadowBorder("Regular Expression", null);
		TitleBarDropShadowBorder borderab = new TitleBarDropShadowBorder("Options", null);
		TitleBarDropShadowBorder borderb = new TitleBarDropShadowBorder("Test Text", null);
		TitleBarDropShadowBorder borderc = new TitleBarDropShadowBorder("Match Tree", null);
		borderaa.getTitleBarBorder().setLeftColor(new Color(170, 220, 170));
		borderb.getTitleBarBorder().setLeftColor(new Color(170, 170, 220));
		borderc.getTitleBarBorder().setLeftColor(new Color(220, 170, 170));

		boxa.setBorder(new EmptyBorder(0, 0, 0, 0));
		boxaa.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0), borderaa));
		//boxab.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0), borderab));
		boxab.setBorder(new EmptyBorder(0, 3, 0, 3));
		boxb.setBorder(borderb);
		boxc.setBorder(borderc);

		//box2.setBorder(new CompoundBorder(border2, Util.Layout.getEmptyBorder(3)));
		//setBorder(new CompoundBorder(new TitleBarBorder("RegeXeratoR"), Util.Layout.getEmptyBorder(2)));
		setBorder(Util.Layout.getEmptyBorder(1));

		//add
		add(boxa, BorderLayout.NORTH);
		add(splitPane, BorderLayout.CENTER);
	}
}
