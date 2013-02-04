package cg;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

class DeleteUnusedStuffMenuItemAction extends AbstractAction
{
	ContentGenerator cg;
	DeleteUnusedStuffMenuItemAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		cg.cgc.saveSISI();
		
		cg.contentGeneratorSISI.removeUnusedImmutableBacksides();
		cg.contentGeneratorSISI.removeUnusedPolygonData();
		cg.contentGeneratorSISI.removeUnusedTextureMaps();
		cg.contentGeneratorSISI.removeUnusedVertexData();
		cg.cgc.updateGeneratedItemAndEditArea();
	}	
}
