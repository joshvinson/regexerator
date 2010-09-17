package rxr.util.component;

import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.*;

/**
 * @author Josh Vinson
 */
public class TitleBarTabbedPane extends TabbedPaneUI
{
	public int getTabRunCount(JTabbedPane pane)
	{
		return 0;
	}

	public int tabForCoordinate(JTabbedPane pane, int x, int y)
	{
		return 0;
	}

	public Rectangle getTabBounds(JTabbedPane pane, int index)
	{
		return null;
	}

}