package cg;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

public class FixedSizeIconToggleButton extends JToggleButton{

	int h;
	int w;
	Icon notSelected;
	Icon selected;
	
FixedSizeIconToggleButton( Icon notSelected, Icon selected) 
{
	super();
	this.notSelected = notSelected;
	this.selected = selected;
	
	h = notSelected.getIconHeight();
	w = notSelected.getIconWidth();
	
	if (h != selected.getIconHeight())throw new RuntimeException("Selected Icon for FixedSizeIconToggleButton differs in height");
	if (w != selected.getIconWidth())throw new RuntimeException("Selected Icon for FixedSizeIconToggleButton differs in width");
	
	this.setIcon(notSelected);
	
	this.setIcon(this.notSelected);
	this.setSelectedIcon(this.selected);
}

/** creates a FixedSizeIconToggleButton from the callers resources */
FixedSizeIconToggleButton(Object caller, String notSelected_st, String selected_st) 
{
	super();
	Icon notSelected = new ImageIcon(caller.getClass().getResource(notSelected_st));
	Icon selected = new ImageIcon(caller.getClass().getResource(selected_st));
	
	h = notSelected.getIconHeight();
	w = notSelected.getIconWidth();
	
	if (h != selected.getIconHeight())throw new RuntimeException("Selected Icon for FixedSizeIconToggleButton differs in height");
	if (w != selected.getIconWidth())throw new RuntimeException("Selected Icon for FixedSizeIconToggleButton differs in width");
	
	this.setIcon(notSelected);
	
	this.setIcon(notSelected);
	this.setSelectedIcon(selected);
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
