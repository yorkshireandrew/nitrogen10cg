package cg;

import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import modified_nitrogen1.ImmutableVertex;
import modified_nitrogen1.SharedImmutableSubItem;

class PolygonVertexController extends AbstractAction
{
	ContentGenerator cg;
	PolygonVertexView polygonVertexView;
	PolygonVertexModel polygonVertexModel;
	
	PolygonVertexController(ContentGenerator cg, PolygonVertexView polygonVertexView, PolygonVertexModel polygonVertexModel)
	{
		super();
		this.cg 	= cg;
		this.polygonVertexView 	= polygonVertexView;
		this.polygonVertexModel	= polygonVertexModel;
		
		// tell the model its controller
		polygonVertexModel.polygonVertexController = this;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == polygonVertexView.xTextField)
		{
			System.out.println("x text field event");
			handleTextFieldChangeEvent();
		}
		
		if(e.getSource() == polygonVertexView.yTextField)
		{
			System.out.println("y text field event");
			handleTextFieldChangeEvent();
		}
		
		if(e.getSource() == polygonVertexView.zTextField)
		{
			System.out.println("z text field event");
			handleTextFieldChangeEvent();
		}
		
		if(e.getSource() == polygonVertexView.addButton)
		{
			System.out.println("add button event");
			handleAddButtonEvent();
		}
		
		if(e.getSource() == polygonVertexView.moveWorkingToThisButton)
		{
			System.out.println("move working button event");
			handleMoveWorkingButtonEvent();
		}		
		
	}	
	
	void handleTextFieldChangeEvent()
	{
		int x,y,z;
		String xString,yString,zString;
		
		PolygonVertexView pvv = polygonVertexView;
		PolygonVertexModel pvm = polygonVertexModel;
		
		
		xString = pvv.xTextField.getText().trim();
		yString = pvv.yTextField.getText().trim();
		zString = pvv.zTextField.getText().trim();
		
		try
		{
			x = Integer.parseInt(xString);
			y = Integer.parseInt(yString);
			z = Integer.parseInt(zString);
		}
		catch(NumberFormatException nfe)
		{
			JOptionPane.showMessageDialog(cg, "must be an integer numerical value", "Error",JOptionPane.ERROR_MESSAGE);
			return;	
		}
		
		pvm.x = x;
		pvm.y = y;
		pvm.z = z;
		
		ContentGeneratorController cgcL = cg.cgc;
		
		int index = cgcL.isVertexAlreadyThere(x,y,z);
		
		if(index == -1)
		{
			cgcL.saveSISI();
			cgcL.addImmutableVertex(x, y, z);
			pvm.index = cgcL.isVertexAlreadyThere(x,y,z);
			pvv.updateFromModel();
		}
		else
		{
			pvm.index = index;
			pvv.updateFromModel();		
		}
	}
	
	void handleAddButtonEvent()
	{
		WorkingVertexModel wvm = cg.workingVertexModel;
		int x = wvm.x;
		int y = wvm.y; 
		int z = wvm.z; 
		
		PolygonVertexView pvv = polygonVertexView;
		PolygonVertexModel pvm = polygonVertexModel;
		
		pvm.x = x;
		pvm.y = y;
		pvm.z = z;
		
		ContentGeneratorController cgcL = cg.cgc;
		
		int index = cgcL.isVertexAlreadyThere(x,y,z);
		
		if(index == -1)
		{
			cgcL.saveSISI();
			cgcL.addImmutableVertex(x, y, z);
			pvm.index = cgcL.isVertexAlreadyThere(x,y,z);
			pvv.updateFromModel();
		}
		else
		{
			pvm.index = index;
			pvv.updateFromModel();		
		}		
	}
	
	void handleMoveWorkingButtonEvent()
	{
		WorkingVertexModel wvm = cg.workingVertexModel;
		PolygonVertexModel pvm = polygonVertexModel;
		wvm.x = pvm.x;
		wvm.y = pvm.y;
		wvm.z = pvm.z;
		wvm.picked = true;
		wvm.index = cg.cgc.isVertexAlreadyThere(wvm.x, wvm.y, wvm.z);
		wvm.workingVertexController.updateViewFromModel();
//		cg.renderEditArea();
	}
	

}
