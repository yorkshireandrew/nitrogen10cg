package cg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CircleMenuItemAction implements ActionListener {

	ContentGenerator cg;
	
	CircleMenuItemAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		CircleDialog cd = new CircleDialog(cg);
		cd.setVisible(true);
	}

}