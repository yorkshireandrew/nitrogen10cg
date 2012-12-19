package cg;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class FixedSizeButton extends JButton{
	private static final long serialVersionUID = 3961019485692768034L;

	int h;
	int w;
	FixedSizeButton(String st)
	{
		super();
		ImageIcon i = new ImageIcon(getClass().getResource(st));
		h = i.getIconHeight();
		w = i.getIconWidth();
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

}
