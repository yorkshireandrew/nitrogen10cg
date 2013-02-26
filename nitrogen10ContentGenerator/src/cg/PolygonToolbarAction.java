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
		cg.workingPolygonExists = true;
		if((cg.workingPolygon != null)&&(!cg.contentGeneratorSISI.contentGeneratorPolygonMap.containsKey(cg.workingPolygon)))
		{
			// OK the cg.workingPolygon has been removed by undo, so go back to default
			JOptionPane.showMessageDialog(cg, "Warning the working polygon no longer exists.", "Error",JOptionPane.ERROR_MESSAGE);
			cg.workingPolygonExists = false;
		}
		
		PolygonDialog pd = new PolygonDialog(cg);
		pd.setVisible(true);
	}	
}
