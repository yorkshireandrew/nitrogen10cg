package cg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NearFarMenuItemAction implements ActionListener {

	ContentGenerator cg;
	
	NearFarMenuItemAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		NearFarDialog nfd = new NearFarDialog(cg);
		nfd.setVisible(true);
	}

}