package cg;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/** class to handle Template... menu item */
class TemplateMenuItemAction extends AbstractAction
{
	private static final long serialVersionUID = 1L;
	
	ContentGenerator cg;
	TemplateMenuItemAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		TemplateDialog td = new TemplateDialog(cg,cg.templateModels[cg.viewDirection]);
		td.setVisible(true);
	}	
}
