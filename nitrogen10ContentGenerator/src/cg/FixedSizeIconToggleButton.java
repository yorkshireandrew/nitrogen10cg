package cg;

import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

public class FixedSizeIconToggleButton extends JToggleButton{

	int h;
	int w;
	Icon notSelected;
	Icon selected;

/** creates a FixedSizeIconToggleButton from the callers resources */
FixedSizeIconToggleButton(Object caller, String notSelected_st, String selected_st) 
{
	super();
	Icon notSelected = new ImageIcon(caller.getClass().getResource(notSelected_st));
	Icon selected = new ImageIcon(caller.getClass().getResource(selected_st));
	
	this.notSelected = notSelected;
	this.selected = selected;
	
	h = notSelected.getIconHeight();
	w = notSelected.getIconWidth();
	
	if (h != selected.getIconHeight())throw new RuntimeException("Selected Icon for FixedSizeIconToggleButton differs in height");
	if (w != selected.getIconWidth())throw new RuntimeException("Selected Icon for FixedSizeIconToggleButton differs in width");
	
	
	this.setIcon(notSelected);
	this.setSelectedIcon(selected);
}

// allows an action to be set without altering any icons
@Override
public void setAction(Action a)
{
	super.setAction(a);
	// ensure setting the action doesn't change the icons
	this.setIcon(this.notSelected);
	this.setSelectedIcon(this.selected);		
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
