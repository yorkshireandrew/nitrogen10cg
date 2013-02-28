package cg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScaleMenuItemAction implements ActionListener {

	ContentGenerator cg;
	
	ScaleMenuItemAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		ScaleDialog sd = new ScaleDialog(cg);
		sd.setVisible(true);
	}

}

