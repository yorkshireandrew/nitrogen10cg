package cg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CollisionVertexesMenuItemAction implements ActionListener {

	ContentGenerator cg;
	
	CollisionVertexesMenuItemAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		CollisionVertexDialog cvd = new CollisionVertexDialog(cg);
		cvd.setVisible(true);
	}

}