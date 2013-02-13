package cg;

import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.bombheadgames.nitrogen2.ImmutablePolygon;
import com.bombheadgames.nitrogen2.ImmutableVertex;
import com.bombheadgames.nitrogen2.SharedImmutableSubItem;


class PolygonVertexController extends AbstractAction
{
	ContentGenerator cg;
	PolygonVertexView polygonVertexView;
	
	PolygonVertexController(ContentGenerator cg, PolygonVertexView polygonVertexView)
	{
		super();
		this.cg 	= cg;
		this.polygonVertexView 	= polygonVertexView;
		// note model is initially null
		
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
		
		createOrMoveToVertexAt(x,y,z);
	}
	
	void handleAddButtonEvent()
	{
		WorkingVertexModel wvm = cg.workingVertexModel;
		int x = wvm.x;
		int y = wvm.y; 
		int z = wvm.z; 
		
		createOrMoveToVertexAt(x, y, z);
	}
	
	void createOrMoveToVertexAt(int x, int y, int z)
	{
		ContentGeneratorController cgcL = cg.cgc;
		ImmutableVertex existing = cgcL.vertexAlreadyThere(x,y,z);
		
		if(existing == null)
		{
			cgcL.saveSISI();
			cgcL.addImmutableVertex(x, y, z);
			existing = cgcL.vertexAlreadyThere(x,y,z);
			polygonVertexView.pvm = existing;
			polygonVertexView.updateFromModel();
		}
		else
		{
			polygonVertexView.pvm = existing; // update the views copy of the model
			polygonVertexView.updateFromModel();		
		}
	}
	
	void handleMoveWorkingButtonEvent()
	{
		WorkingVertexModel wvm = cg.workingVertexModel;
		ImmutableVertex pvm = polygonVertexView.pvm;
		if(pvm == null)return;
		wvm.x = (int)pvm.is_x;
		wvm.y = (int)pvm.is_y;
		wvm.z = (int)pvm.is_z;
		wvm.pickedVertex = pvm;
		wvm.workingVertexController.updateViewFromModel();
//		cg.renderEditArea();
	}
	

}
