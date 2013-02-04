package cg;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import modified_nitrogen1.NitrogenContext;

class FullyRenderedToolbarAction extends AbstractAction
{
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
