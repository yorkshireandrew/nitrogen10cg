package cg;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.bombheadgames.nitrogen2.NitrogenContext;
import com.bombheadgames.nitrogen2.RendererTriplet;
import com.bombheadgames.nitrogen2.Renderer_Outline;


class BacksideCulledWireFrameToolbarAction extends AbstractAction
{
	private static final long serialVersionUID = 1L;
	
	ContentGenerator cg;
	BacksideCulledWireFrameToolbarAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		cg.viewDetail = ContentGenerator.BACKSIDE_CULLED_WIREFRAME;
		NitrogenContext nc = cg.nc;
		nc.isPicking = true;
		RendererTriplet.setPickingRenderer( new Renderer_Outline());	
		nc.contentGeneratorForcesNoCulling = false;
		cg.renderEditArea();		
	}	
}
