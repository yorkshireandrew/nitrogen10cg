package cg;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/** class to handle vertex data toolbar button */
class VertexDataToolbarAction extends AbstractAction
{
	ContentGenerator cg;
	VertexDataToolbarAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		VertexDataDialog td = new VertexDataDialog(cg);
		td.setVisible(true);
	}	
}