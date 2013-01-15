package cg;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class WorkingVertexController extends AbstractAction
{
	 ContentGenerator cg;
	 WorkingVertexModel workingVertexModel;
	 WorkingVertexView workingVertexView;
	 
	 WorkingVertexController(ContentGenerator cg, WorkingVertexView wvv, WorkingVertexModel wvm)
	 {
		 this.cg = cg;
		 this.workingVertexView = wvv;
		 this.workingVertexModel = wvm;
		 wvm.workingVertexController = this;
	 }

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
