package cg;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import modified_nitrogen1.NitrogenContext;
import modified_nitrogen1.RendererTriplet;
import modified_nitrogen1.Renderer_Null;

class VertexesOnlyToolbarAction extends AbstractAction
{
	ContentGenerator cg;
	VertexesOnlyToolbarAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		cg.viewDetail = ContentGenerator.VERTEXES_ONLY;
		NitrogenContext nc = cg.nc;
		nc.isPicking = true;
		RendererTriplet.setPickingRenderer( new Renderer_Null());	
		nc.contentGeneratorForcesNoCulling = true;
		cg.renderEditArea();		
	}	
}
