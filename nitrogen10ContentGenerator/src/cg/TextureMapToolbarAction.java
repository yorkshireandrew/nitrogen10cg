package cg;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/** class to handle Template... menu item */
class TextureMapToolbarAction extends AbstractAction
{
	ContentGenerator cg;
	TextureMapToolbarAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		TextureMapDialog tmd = new TextureMapDialog(cg);
		tmd.setVisible(true);
	}	
}
