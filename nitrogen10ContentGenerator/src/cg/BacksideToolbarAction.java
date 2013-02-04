package cg;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/** class to handle Template... menu item */
class BacksideToolbarAction extends AbstractAction
{
	ContentGenerator cg;
	BacksideToolbarAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		// check a backside can be created using static method
		if(BacksideDialog.polygonVertexesAreOK(cg))
		{
			// A backside can be created so open the dialog
			BacksideDialog bd = new BacksideDialog(cg);
			bd.setVisible(true);
		}	
	}
}
