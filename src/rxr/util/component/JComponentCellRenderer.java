package rxr.util.component;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;

/**
 * Source: http://codeguru.earthweb.com/java/articles/162.shtml Slightly modified.
 */
public class JComponentCellRenderer implements TableCellRenderer
{
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		return (JComponent) value;
	}
}