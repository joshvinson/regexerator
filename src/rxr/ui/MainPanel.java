package rxr.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;

import rxr.*;
import rxr.ui.misc.*;
import rxr.ui.tree.*;

/**
 * @author Josh Vinson
 */
public class MainPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	//components

	/**
	 * The regular expression input field.
	 */
	JTextField regexField;

	/**
	 * The test text input field. The contents of this is what the regex is
	 * matched against.
	 */
	JTextPane textField;
	JCheckBox autoRefreshCheck;
	JCheckBox autoExpandCheck;
	JLabel regexStatusLabel;
	JButton runButton;
	MatchTree matchTree;
	JButton expandButton;
	JButton collapseButton;
	JCheckBox replaceCheck;
	JTextPane replaceField;
	JSplitPane splitPane2;
	JTextField replaceRegexField;
	JLabel replaceRegexLabel;
	JLabel replaceStatusLabel;
	JProgressBar progress;

	RegexFieldListener listener;

	public MainPanel()
	{
		//--icons init--
		final ImageIcon startIcon = new ImageIcon(RXR.load("res/media/19127.thread_view.gif"));
		final ImageIcon doneIcon = new ImageIcon(RXR.load("res/media/14073.complete_status.gif"));
		final ImageIcon errorIcon = new ImageIcon(RXR.load("res/media/10448.error_obj.gif"));
		final ImageIcon runIcon = new ImageIcon(RXR.load("res/media/17155.run_exc.png"));
		final ImageIcon waitingIcon = new ImageIcon(RXR.load("res/media/13437.elipses.gif"));
		final ImageIcon tooManyIcon = new ImageIcon(RXR.load("res/media/17597.showdesc_co.gif"));

		final ImageIcon regexIcon = new ImageIcon(RXR.load("res/media/10562.metharg_obj.gif"));
		final ImageIcon textIcon = new ImageIcon(RXR.load("res/media/10388.file_obj.gif"));
		final ImageIcon replaceIcon = new ImageIcon(RXR.load("res/media/10450.externalize.gif"));
		final ImageIcon treeIcon = new ImageIcon(RXR.load("res/media/14082.tree_explorer.gif"));

		//--components init--
		regexField = new JTextField();
		textField = new JTextPane();
		regexStatusLabel = new JLabel("Ready");
		autoRefreshCheck = new JCheckBox("Auto Refresh", true);
		runButton = new JButton("Run", runIcon);
		autoExpandCheck = new JCheckBox("Auto Expand Tree", true);
		expandButton = new JButton("Expand");
		collapseButton = new JButton("Collapse");
		replaceCheck = new JCheckBox("Replace", false);
		replaceField = new JTextPane();
		replaceRegexField = new JTextField();
		replaceRegexLabel = new JLabel("Replace with");
		replaceStatusLabel = new JLabel("Ready");
		progress = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);

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
				matchTree.setAutoExpand(auto);
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
		progress.setPreferredSize(new Dimension(80, 1));
		progress.setMinimumSize(new Dimension(80, 1));

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
		//boxab.add(wordWrapCheck);
		boxab.add(Box.createHorizontalStrut(5));
		boxab.add(progress);
		boxab.add(Box.createHorizontalStrut(5));
		boxab.add(autoExpandCheck);
		boxab.add(expandButton);
		boxab.add(collapseButton);

		boxa.add(boxaa1);
		boxa.add(boxab);

		//scrollpanes
		final JScrollPane jspTextField = new JScrollPane(textField);
		final JScrollPane jspReplaceField = new JScrollPane(replaceField);

		JScrollPane jspMatchTree = new JScrollPane(matchTree);

		//splitPane = text and tree
		final JSplitPane splitPane = new JSplitPane();

		splitPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		BasicSplitPaneUI ui = (BasicSplitPaneUI)splitPane.getUI();
		ui.getDivider().setBorder(new EmptyBorder(0, 0, 0, 0));
		ui.getDivider().setDividerSize(3);
		splitPane.setContinuousLayout(true);
		splitPane.resetToPreferredSizes();
		splitPane.setResizeWeight(.7);

		//splitPane2 = test and replace
		splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		splitPane2.setBorder(new EmptyBorder(0, 0, 0, 0));
		ui = (BasicSplitPaneUI)splitPane2.getUI();
		ui.getDivider().setBorder(new EmptyBorder(0, 0, 0, 0));
		ui.getDivider().setDividerSize(3);
		splitPane2.setContinuousLayout(true);
		splitPane2.resetToPreferredSizes();
		splitPane2.setResizeWeight(.5);

		//boxb = text pane
		//Box boxb = Box.createVerticalBox();
		//boxb.add(jspa);
		//boxb.add(jspaa);
		splitPane2.setTopComponent(jspTextField);
		splitPane2.setBottomComponent(jspReplaceField);

		//splitPane.setLeftComponent(boxb);
		//splitPane.setLeftComponent(splitPane2);
		splitPane.setLeftComponent(jspTextField);

		//boxc = tree
		Box boxc = Box.createVerticalBox();
		boxc.add(jspMatchTree);

		splitPane.setRightComponent(boxc);

		//borders
		regexField.setBorder(new EmptyBorder(4, 4, 4, 4));
		textField.setBorder(new EmptyBorder(4, 4, 4, 4));
		regexStatusLabel.setBorder(new EmptyBorder(4, 4, 4, 4));
		matchTree.setBorder(new EmptyBorder(4, 4, 4, 4));
		autoRefreshCheck.setBorder(new EmptyBorder(2, 2, 2, 2));
		runButton.setBorder(new EmptyBorder(3, 3, 3, 5));
		expandButton.setBorder(new EmptyBorder(3, 5, 3, 5));
		collapseButton.setBorder(new EmptyBorder(3, 5, 3, 5));

		progress.setBorder(new DropShadowBorder());

		replaceRegexField.setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, Color.GRAY), new EmptyBorder(4, 4, 4, 4)));
		replaceRegexLabel.setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 1, Color.GRAY), new EmptyBorder(5, 3, 5, 3)));

		replaceStatusLabel.setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, Color.GRAY), new EmptyBorder(4, 4, 4, 4)));

		//jspa.setBorder(LayoutUtil.getEmptyBorder(0));
		//jspb.setBorder(LayoutUtil.getEmptyBorder(0));

		TitleBarDropShadowBorder bordera = new TitleBarDropShadowBorder("Regular Expression", regexIcon);
		TitleBarDropShadowBorder borderb = new TitleBarDropShadowBorder("Test Text", textIcon);
		TitleBarDropShadowBorder borderc = new TitleBarDropShadowBorder("Match Tree", treeIcon);
		TitleBarDropShadowBorder borderd = new TitleBarDropShadowBorder("Replace Result", replaceIcon);

		bordera.getTitleBarBorder().setLeftColor(new Color(170, 220, 170));
		borderb.getTitleBarBorder().setLeftColor(new Color(170, 170, 220));
		borderc.getTitleBarBorder().setLeftColor(new Color(220, 170, 170));
		borderd.getTitleBarBorder().setLeftColor(new Color(170, 170, 220));

		boxa.setBorder(new EmptyBorder(0, 0, 0, 0));
		boxaa1.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0), bordera));
		boxab.setBorder(new EmptyBorder(0, 3, 0, 3));
		jspTextField.setBorder(borderb);
		jspMatchTree.setBorder(borderc);
		jspReplaceField.setBorder(borderd);

		setBorder(new EmptyBorder(1, 1, 1, 1));

		//add
		add(boxa, BorderLayout.NORTH);
		add(splitPane, BorderLayout.CENTER);

		//add complex listeners
		replaceCheck.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(replaceCheck.isSelected() && splitPane.getLeftComponent() == jspTextField)
				{
					splitPane.remove(jspTextField);
					splitPane2.setTopComponent(jspTextField);
					splitPane.setLeftComponent(splitPane2);

					boxaa1.add(boxaa2);
					revalidate();

					listener.setDoReplace(true);
					listener.regex();
				}
				else
				{
					splitPane.remove(splitPane2);
					splitPane2.remove(jspTextField);
					splitPane.setLeftComponent(jspTextField);

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

		listener.addListener(new RegexEventListener()
		{
			@Override
			public void regexEvent(final Type t)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						switch(t)
						{
						case RECALC_PROGRESS:
							synchronized(listener)
							{
								progress.setValue(listener.progress);
								progress.setString(listener.matches.size() + " matches");
								progress.repaint();
							}
							break;
						case RECALC_START:
							regexStatusLabel.setIcon(startIcon);
							regexStatusLabel.setText("Working");
							replaceStatusLabel.setIcon(startIcon);
							replaceStatusLabel.setText("Working");
							break;
						case RECALC_COMPLETE:
							regexStatusLabel.setIcon(doneIcon);
							regexStatusLabel.setText("Ready");
							replaceStatusLabel.setIcon(doneIcon);
							replaceStatusLabel.setText("Ready");
							break;
						case BAD_PATTERN:
							regexStatusLabel.setIcon(errorIcon);
							regexStatusLabel.setText("Error");
							replaceStatusLabel.setIcon(waitingIcon);
							replaceStatusLabel.setText("Waiting");
							break;
						case BAD_REPLACE:
							regexStatusLabel.setIcon(doneIcon);
							regexStatusLabel.setText("Ready");
							replaceStatusLabel.setIcon(errorIcon);
							replaceStatusLabel.setText("Error");
							break;
						case TOO_MANY_MATCHES:
							regexStatusLabel.setIcon(tooManyIcon);
							regexStatusLabel.setText("Too Many Matches");
							replaceStatusLabel.setIcon(waitingIcon);
							replaceStatusLabel.setText("Waiting");
							break;
						}
					}
				});

			}
		});

		//initialize ui by running the empty default regex
		listener.regex();
	}
}
