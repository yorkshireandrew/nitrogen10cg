package cg;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.bombheadgames.nitrogen1.NitrogenContext;
import com.bombheadgames.nitrogen1.RendererTriplet;
import com.bombheadgames.nitrogen1.Renderer_Outline;


class FullWireFrameToolbarAction extends AbstractAction
{
	ContentGenerator cg;
	FullWireFrameToolbarAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		cg.viewDetail = ContentGenerator.WIREFRAME;
		NitrogenContext nc = cg.nc;
		nc.isPicking = true;
		RendererTriplet.setPickingRenderer( new Renderer_Outline());	
		nc.contentGeneratorForcesNoCulling = true;
		cg.renderEditArea();		
	}	
}
