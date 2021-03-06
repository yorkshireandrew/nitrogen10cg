package cg;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class WorkingVertexController extends AbstractAction
{
	private static final long serialVersionUID = 1L;

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
		
		// Handle an edit to the x y z values
		if(
			(e.getSource() == workingVertexView.xTextField)
			||(e.getSource() == workingVertexView.yTextField)
			||(e.getSource() == workingVertexView.zTextField)
		)
		{
			// store current position in case we need to revert
			int original_x = workingVertexModel.x;
			int original_y = workingVertexModel.y;
			int original_z = workingVertexModel.z;
			
			int proposed_x;
			int proposed_y;
			int proposed_z;
			
			try
			{
				proposed_x = Integer.parseInt(workingVertexView.xTextField.getText());
				proposed_y = Integer.parseInt(workingVertexView.yTextField.getText());
				proposed_z = Integer.parseInt(workingVertexView.zTextField.getText());
			}
			catch(NumberFormatException nfe)
			{
				workingVertexModel.x = original_x;
				workingVertexModel.y = original_y;
				workingVertexModel.z = original_z;
				updateViewFromModel(); // overkill workingVertexView.updateFromModel() is sufficient
				return;
			}
			
			boolean valid = true;
			int midx = ContentGenerator.EDIT_SCREEN_MIDX;
			int midy = ContentGenerator.EDIT_SCREEN_MIDY;
			int border = ContentGenerator.CONSTRAINED_BORDER_WIDTH;		
			// front view validation
			if(-proposed_x > (midx - border))valid = false;
			if(-proposed_y > midy)valid = false; 
			if(proposed_x > midx)valid = false;
			if(proposed_y > (midy - border))valid = false;

			// left view validation ( y validation already done)
			if(proposed_z > (midx - border))valid = false;
			if(-proposed_z > midx)valid = false;

			// back view validation ( y validation already done)
			if(proposed_x > (midx - border))valid = false;
			if(-proposed_x > midx)valid = false;

			// right view validation ( y validation already done)
			if(proposed_z > midx)valid = false;
			if(-proposed_z > (midx - border))valid = false;

			// top view validation (z validation already done)
			if(-proposed_x > (midy - border))valid = false;
			if(proposed_x > midy)valid = false;
			
			// bottom view validation (z validation already done)
			if(proposed_x > (midy - border))valid = false;
			if(-proposed_x > midy)valid = false;
			
			if(valid)
			{
				workingVertexModel.x = proposed_x;
				workingVertexModel.y = proposed_y;
				workingVertexModel.z = proposed_z;
				updateViewFromModel();
				
			}
			else
			{
				workingVertexModel.x = original_x;
				workingVertexModel.y = original_y;
				workingVertexModel.z = original_z;
				updateViewFromModel(); // overkill workingVertexView.updateFromModel() is sufficient
				return;				
			}	
		}// end of edit to xyz values if	
		
		// Handle a moveToZeroXButton press
		if(e.getSource() == workingVertexView.moveToZeroXButton)
		{
			workingVertexModel.x = 0;
			updateViewFromModel();
			return;
		}
		
		// Handle a setRefButton press
		if(e.getSource() == workingVertexView.setRefButton)
		{
			workingVertexModel.setReference();
			workingVertexModel.computeDistances();
			workingVertexView.updateFromModel();	
			return;
		}
		
		
	}
	
	void updateViewFromModel()
	{
		workingVertexModel.computeDistances();
		workingVertexView.updateFromModel();
		cg.cgc.updateCursorFromWorkingVertex();		
	}
	
	
}// end of WorkingVertexController class
