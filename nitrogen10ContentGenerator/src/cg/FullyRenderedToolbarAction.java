package cg;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.bombheadgames.nitrogen2.NitrogenContext;


class FullyRenderedToolbarAction extends AbstractAction
{
	private static final long serialVersionUID = 1L;

	ContentGenerator cg;
	FullyRenderedToolbarAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		cg.viewDetail = ContentGenerator.FULLY_RENDERED;
		NitrogenContext nc = cg.nc;
		nc.isPicking = false;	
		nc.contentGeneratorForcesNoCulling = false;
		cg.renderEditArea();		
	}	
}
