package rxr.util.component;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.plaf.*;

/**
 * Source:
 * http://forum.java.sun.com/thread.jsp?thread=263180&forum=57&message=2281801
 */
public class TabMoveHandler extends MouseAdapter implements MouseMotionListener
{
	int startIndex = -1;
	private int currentIndex = -1;

	public void mousePressed(MouseEvent e)
	{
		if(!e.isPopupTrigger())
		{
			JTabbedPane tabbedPane = (JTabbedPane)e.getSource();
			startIndex = tabbedPane.indexAtLocation(e.getX(), e.getY());
		}
		currentIndex = -1;
	}

	public void mouseReleased(MouseEvent e)
	{
		JTabbedPane tabbedPane = (JTabbedPane)e.getSource();
		if(!e.isPopupTrigger())
		{
			int endIndex = tabbedPane.indexAtLocation(e.getX(), e.getY());

			if(startIndex != -1 && endIndex != -1 && startIndex != endIndex)
			{
				moveTab(tabbedPane, startIndex, endIndex);
				tabbedPane.setSelectedIndex(endIndex);

				// ONLY OMS SPECIFIC LINE
				//OMSBasePanel.getInstance().saveUISettings();
			}
		}
		startIndex = -1;
		clearRectangle(tabbedPane);
		currentIndex = -1;
	}

	/**
	 * @param tabbedPane
	 * @param startIndex
	 * @param endIndex
	 */
	private void moveTab(JTabbedPane pane, int src, int dst)
	{
		// Get all the properties
		Component comp = pane.getComponentAt(src);
		String label = pane.getTitleAt(src);
		Icon icon = pane.getIconAt(src);
		Icon iconDis = pane.getDisabledIconAt(src);
		String tooltip = pane.getToolTipTextAt(src);
		boolean enabled = pane.isEnabledAt(src);
		int keycode = pane.getMnemonicAt(src);
		int mnemonicLoc = pane.getDisplayedMnemonicIndexAt(src);
		Color fg = pane.getForegroundAt(src);
		Color bg = pane.getBackgroundAt(src);

		// Remove the tab
		pane.remove(src);

		// Add a new tab
		pane.insertTab(label, icon, comp, tooltip, dst);

		// Restore all properties
		pane.setDisabledIconAt(dst, iconDis);
		pane.setEnabledAt(dst, enabled);
		pane.setMnemonicAt(dst, keycode);
		pane.setDisplayedMnemonicIndexAt(dst, mnemonicLoc);
		pane.setForegroundAt(dst, fg);
		pane.setBackgroundAt(dst, bg);
	}

	public void mouseDragged(MouseEvent e)
	{
		if(startIndex != -1)
		{
			JTabbedPane tabbedPane = (JTabbedPane)e.getSource();
			int index = tabbedPane.indexAtLocation(e.getX(), e.getY());

			if(index != -1 && index != currentIndex)
			{ // moved over another tab
				clearRectangle(tabbedPane);
				currentIndex = index;
			}

			if(currentIndex != -1 && currentIndex != startIndex)
			{
				drawRectangle(tabbedPane);
			}
		}
	}

	private void clearRectangle(JTabbedPane tabbedPane)
	{
		if(currentIndex == -1)
		{
			return;
		}
		TabbedPaneUI ui = tabbedPane.getUI();
		Rectangle rect = ui.getTabBounds(tabbedPane, currentIndex);
		tabbedPane.repaint(rect);
	}

	private void drawRectangle(JTabbedPane tabbedPane)
	{
		TabbedPaneUI ui = tabbedPane.getUI();
		Rectangle rect = ui.getTabBounds(tabbedPane, currentIndex);
		Graphics graphics = tabbedPane.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
	}

	public void mouseMoved(MouseEvent e)
	{
		//
	}

	public void mouseExited(MouseEvent e)
	{
		clearRectangle((JTabbedPane)e.getSource());
		currentIndex = -1;
	}
}