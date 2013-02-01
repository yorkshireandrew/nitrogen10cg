package cg;

import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class FixedSizeButton extends JButton{
	private static final long serialVersionUID = 3961019485692768034L;

	int h;
	int w;
	ImageIcon i;
	FixedSizeButton(String st)
	{
		super();
		ImageIcon i = new ImageIcon(getClass().getResource(st));
		this.i = i;
		h = i.getIconHeight();
		w = i.getIconWidth();
		this.setIcon(i);
	}

// allow an action to be set without altering the icon
@Override
public void setAction(Action a)
{
	super.setAction(a);
	// ensure setting the action doesn't change the icons
	this.setIcon(i);	
}
	
@Override
public Dimension getMinimumSize()
{
    return new Dimension(w,h);
}

@Override
public Dimension getPreferredSize()
{
    return new Dimension(w,h);
}

@Override
public Dimension getMaximumSize()
{
    return new Dimension(w,h);
}

public void setIcon(String st)
{
	ImageIcon i = new ImageIcon(getClass().getResource(st));
	h = i.getIconHeight();
	w = i.getIconWidth();
	this.setIcon(i);
}

}
