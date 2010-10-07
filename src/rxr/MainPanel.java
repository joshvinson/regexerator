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
	private static final long serialVersionUID = -4238061796428182229L;

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
	JTextField replaceRegexField;
	JLabel replaceRegexLabel;
	JLabel replaceStatusLabel;
	JProgressBar progress = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);

	RegexFieldListener listener;

	public MainPanel()
	{
		//--icons init--
		final ImageIcon startIcon = new ImageIcon(RXR.load("res/media/19127.thread_view.gif"));
		final ImageIcon doneIcon = new ImageIcon(RXR.load("res/media/14073.complete_status.gif"));
		final ImageIcon errorIcon = new ImageIcon(RXR.load("res/media/10448.error_obj.gif"));
		final ImageIcon runIcon = new ImageIcon(RXR.load("res/media/17155.run_exc.png"));
		final ImageIcon waitingIcon = new ImageIcon(RXR.load("res/media/13437.elipses.gif"));

		//--components init--
		regexField = new JTextField();
		textField = new JTextPane();
		regexStatusLabel = new JLabel("Ready");
		autoRefreshCheck = new JCheckBox("Auto Refresh", true);
		runButton = new JButton("Run", runIcon);
		autoExpandCheck = new JCheckBox("Auto Expand Tree", true);
		expandButton = new JButton("Expand");
		collapseButton = new JButton("Collapse");
		statusLabel = new JLabel();
		replaceCheck = new JCheckBox("Replace", false);
		replaceField = new JTextPane();
		replaceRegexField = new JTextField();
		replaceRegexLabel = new JLabel("Replace with");
		replaceStatusLabel = new JLabel("Ready");

		//--components configure--
		listener = new RegexFieldListener(regexField, textField, replaceRegexField, replaceField);
		regexField.getDocument().addDocumentListener(listener);
		textField.getDocument().addDocumentListener(listener);
		replaceRegexField.getDocument().addDocumentListener(listener);
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

		textField.setBackground(Color.WHITE);
		replaceField.setBackground(Color.WHITE);

		matchTree = new MatchTree(listener);

		listener.addListener(new RegexEventListener()
		{
			@Override
			public void regexEvent(final Type t)
			{
				/*if(t == Type.RECALC_PROGRESS)
				{
					
				}*/
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						switch(t)
						{
						case RECALC_PROGRESS:
							progress.setValue(listener.progress);
							progress.setString(listener.matches.size() + " matches");
							progress.repaint();
							break;
						case RECALC_START:
							regexStatusLabel.setIcon(startIcon);
							regexStatusLabel.setText("Working");
							replaceStatusLabel.setIcon(startIcon);
							replaceStatusLabel.setText("Working");
							statusLabel.setText("");
							break;
						case RECALC_COMPLETE:
							regexStatusLabel.setIcon(doneIcon);
							regexStatusLabel.setText("Ready");
							replaceStatusLabel.setIcon(doneIcon);
							replaceStatusLabel.setText("Ready");
							if(listener.matches != null)
							{
								statusLabel.setText(listener.matches.size() + " matches found.");
							}
							break;
						case BAD_PATTERN:
							regexStatusLabel.setIcon(errorIcon);
							regexStatusLabel.setText("Error");
							replaceStatusLabel.setIcon(waitingIcon);
							replaceStatusLabel.setText("Waiting");
							statusLabel.setText("");
							break;
						case BAD_REPLACE:
							regexStatusLabel.setIcon(doneIcon);
							regexStatusLabel.setText("Ready");
							replaceStatusLabel.setIcon(errorIcon);
							replaceStatusLabel.setText("Error");
							statusLabel.setText("");
							break;
						}
					}
				});

			}
		});

		regexStatusLabel.setIcon(doneIcon);
		//regexStatusLabel.setFont(new Font("Courier", Font.PLAIN, 12));
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

		replaceRegexLabel.setOpaque(true);
		replaceRegexLabel.setBackground(Color.WHITE);
		replaceRegexLabel.setForeground(Color.GRAY);

		replaceStatusLabel.setIcon(doneIcon);
		replaceStatusLabel.setOpaque(true);
		replaceStatusLabel.setBackground(Color.WHITE);
		replaceStatusLabel.setHorizontalTextPosition(SwingConstants.LEFT);

		progress.setUI(new RXRProgressBarUI());
		progress.setStringPainted(true);

		//--layout--
		setLayout(new BorderLayout());

		//boxa = main north box
		final Box boxa = Box.createVerticalBox();

		//boxaa = regex field/status label
		final Box boxaa1 = Box.createVerticalBox();
		final Box boxaa = Box.createHorizontalBox();
		boxaa.add(regexField);
		//boxaa.add(replaceRegexField);
		//boxaa.add(replaceRegexLabel);
		boxaa.add(regexStatusLabel);

		final Box boxaa2 = Box.createHorizontalBox();
		boxaa2.add(replaceRegexLabel);
		boxaa2.add(replaceRegexField);
		boxaa2.add(replaceStatusLabel);

		boxaa1.add(boxaa);
		//boxaa1.add(boxaa2);

		//boxab = options
		Box boxab = Box.createHorizontalBox();
		boxab.add(runButton);
		boxab.add(autoRefreshCheck);
		boxab.add(replaceCheck);
		//boxab.add(Box.createHorizontalGlue());
		//boxab.add(statusLabel);
		boxab.add(Box.createHorizontalStrut(5));
		boxab.add(progress);
		boxab.add(Box.createHorizontalStrut(5));
		//boxab.add(Box.createHorizontalGlue());
		boxab.add(autoExpandCheck);
		boxab.add(expandButton);
		boxab.add(collapseButton);

		boxa.add(boxaa1);
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
		//splitPane.setLeftComponent(splitPane2);
		splitPane.setLeftComponent(jspa);

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

		progress.setBorder(new DropShadowBorder());

		replaceRegexField.setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, Color.GRAY), Util.Layout.getEmptyBorder(4)));
		replaceRegexLabel.setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 1, Color.GRAY), new EmptyBorder(5, 3, 5, 3)));

		replaceStatusLabel.setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, Color.GRAY), new EmptyBorder(4, 4, 4, 4)));

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
		boxaa1.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0), bordera));
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
				if(replaceCheck.isSelected() && splitPane.getLeftComponent() == jspa)
				{
					splitPane.remove(jspa);
					splitPane2.setTopComponent(jspa);
					splitPane.setLeftComponent(splitPane2);

					boxaa1.add(boxaa2);
					revalidate();

					listener.setDoReplace(true);
					listener.regex();
				}
				else
				{
					splitPane.remove(splitPane2);
					splitPane2.remove(jspa);
					splitPane.setLeftComponent(jspa);

					boolean focus = false;
					if(replaceRegexField.hasFocus())
					{
						focus = true;
					}

					boxaa1.remove(boxaa2);
					revalidate();

					if(focus)
					{
						regexField.grabFocus();
					}

					listener.setDoReplace(false);
				}
				if(autoRefreshCheck.isSelected())
				{
					listener.regex();
				}
			}
		});

		listener.regex();
	}
}
