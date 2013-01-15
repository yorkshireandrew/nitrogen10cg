package cg;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

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
		}
		
		if(e.getSource() == polygonVertexView.yTextField)
		{
			System.out.println("y text field event");
		}
		
		if(e.getSource() == polygonVertexView.zTextField)
		{
			System.out.println("z text field event");
		}
		
		if(e.getSource() == polygonVertexView.addButton)
		{
			System.out.println("add button event");
		}
		
		if(e.getSource() == polygonVertexView.moveWorkingToThisButton)
		{
			System.out.println("move working button event");
		}		
		
	}	
}
