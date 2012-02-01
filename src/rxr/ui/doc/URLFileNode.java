package rxr.ui.doc;

import java.io.*;
import java.net.*;

/**
 * @author Josh Vinson
 */
public class URLFileNode extends URLNode
{
	public URLFileNode(URL url, URLNode root, URLNode parent)
	{
		super(url, root, parent);
	}

	public void fillChildren()
	{
		if(!isDir())
		{
			//System.out.println("[" + this + "]: I'm a file - returning");
			return;
		}
		try
		{
			File[] list = new File(url.toURI()).listFiles();
			if(list == null)
			{
				System.out.println("wtf? " + url.toURI());
				return;
			}
			for(File f : list)
			{
				if(f.isFile() && !f.getName().endsWith(".html"))
				{
					continue;
				}
				URLFileNode n = new URLFileNode(new URL(url.toExternalForm() + f.getName().replace(" ", "%20") + (f.isDirectory() ? "/" : "")), root, this);
				//System.out.println("[" + this + "]: making child: " + n);
				children.add(n);
				n.fillChildren();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
