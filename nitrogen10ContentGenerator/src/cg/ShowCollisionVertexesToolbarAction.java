package cg;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class ShowCollisionVertexesToolbarAction extends AbstractAction {

	ContentGenerator cg;
	
	ShowCollisionVertexesToolbarAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		FixedSizeIconToggleButton s = (FixedSizeIconToggleButton)e.getSource();
		
		cg.showCollisionVertexes = !cg.showCollisionVertexes;
		
		if(cg.showCollisionVertexes)
		{
			s.setSelected(true);
			cg.cgc.updateGeneratedItemAndEditArea();
		}
		else
		{
			s.setSelected(false);
			cg.cgc.updateGeneratedItemAndEditArea();
		}
		
	}

}