package cg;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

/** class to handle Template... menu item */
class PolygonToolbarAction extends AbstractAction
{
	ContentGenerator cg;
	PolygonToolbarAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {		
		PolygonDialog pd = new PolygonDialog(cg);
		pd.setVisible(true);
	}	
}
