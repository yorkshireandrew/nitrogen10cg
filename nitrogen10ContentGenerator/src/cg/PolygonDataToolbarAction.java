package cg;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

class PolygonDataToolbarAction extends AbstractAction
{
	ContentGenerator cg;
	PolygonDataToolbarAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		PolygonDataDialog pd = new PolygonDataDialog(cg);
		pd.setVisible(true);
	}	
}