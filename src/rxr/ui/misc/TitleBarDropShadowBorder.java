package rxr.ui.misc;

import javax.swing.*;
import javax.swing.border.*;

/**
 * A border that combines a TitleBarBorder and a DropShadowBorder to make a
 * look-alike of eclipse interface elements.
 * 
 * @author Josh Vinson
 */
public class TitleBarDropShadowBorder extends CompoundBorder
{
	private static final long serialVersionUID = 4869619476421638844L;

	protected TitleBarBorder titleBarBorder;
	protected DropShadowBorder dropShadowBorder;

	public TitleBarDropShadowBorder(String text, ImageIcon icon)
	{
		titleBarBorder = new TitleBarBorder(text, icon, true);
		dropShadowBorder = new DropShadowBorder();
		this.insideBorder = titleBarBorder;
		this.outsideBorder = dropShadowBorder;
	}

	public DropShadowBorder getDropShadowBorder()
	{
		return dropShadowBorder;
	}

	public void setDropShadowBorder(DropShadowBorder dropShadowBorder)
	{
		this.dropShadowBorder = dropShadowBorder;
	}

	public TitleBarBorder getTitleBarBorder()
	{
		return titleBarBorder;
	}

	public void setTitleBarBorder(TitleBarBorder titleBarBorder)
	{
		this.titleBarBorder = titleBarBorder;
	}
}