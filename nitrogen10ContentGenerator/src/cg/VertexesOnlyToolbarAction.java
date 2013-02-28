package cg;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.bombheadgames.nitrogen2.NitrogenContext;
import com.bombheadgames.nitrogen2.RendererTriplet;
import com.bombheadgames.nitrogen2.Renderer_Null;


class VertexesOnlyToolbarAction extends AbstractAction
{
	private static final long serialVersionUID = 1L;

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
