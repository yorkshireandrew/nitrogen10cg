package cg;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

class DeleteUnusedVertexesMenuItemAction extends AbstractAction
{
	ContentGenerator cg;
	DeleteUnusedVertexesMenuItemAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		cg.cgc.saveSISI();
		cg.contentGeneratorSISI.removeUnusedImmutableVertexes();
		cg.cgc.updateGeneratedItemAndEditArea();
	}	
}
