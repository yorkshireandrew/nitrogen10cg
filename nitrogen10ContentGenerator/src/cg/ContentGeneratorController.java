package cg;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.event.MouseInputAdapter;

public class ContentGeneratorController
{
	ContentGenerator cg;
	
	ContentGeneratorController(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	void updateWorkingVertex()
	{
		int screenX = cg.cursor_x - ContentGenerator.EDIT_SCREEN_MIDX;
		int screenY = ContentGenerator.EDIT_SCREEN_MIDY - cg.cursor_y;
		
		switch(cg.viewDirection)
		{
			case ContentGenerator.FRONT:
				cg.workingVertexModel.x = screenX;
				cg.workingVertexModel.y = screenY;
				break;
			case ContentGenerator.LEFT:
				cg.workingVertexModel.z = -screenX;
				cg.workingVertexModel.y = screenY;
				break;
			case ContentGenerator.BACK:
				cg.workingVertexModel.x = -screenX;
				cg.workingVertexModel.y = screenY;
				break;
			case ContentGenerator.RIGHT:
				cg.workingVertexModel.z = screenX;
				cg.workingVertexModel.y = screenY;
				break;
			case ContentGenerator.TOP: 
				cg.workingVertexModel.z = -screenX;
				cg.workingVertexModel.x = -screenY;
				break;
			case ContentGenerator.BOTTOM:
				cg.workingVertexModel.z = -screenX;
				cg.workingVertexModel.x = screenY;
				break;
		}
		cg.workingVertexModel.computeDistances();
		cg.workingVertexView.updateFromModel();	
	}
	
	void updateCursorFromWorkingVertex()
	{
		int screenX;
		int screenY;
		
		switch(cg.viewDirection)
		{
			case ContentGenerator.FRONT:
				screenX = cg.workingVertexModel.x;
				screenY = cg.workingVertexModel.y;
				break;
			case ContentGenerator.LEFT:
				screenX = -cg.workingVertexModel.z;
				screenY = cg.workingVertexModel.y;
				break;
			case ContentGenerator.BACK:
				screenX = -cg.workingVertexModel.x;
				screenY = cg.workingVertexModel.y;
				break;
			case ContentGenerator.RIGHT:
				screenX = cg.workingVertexModel.z;
				screenY = cg.workingVertexModel.y;
				break;
			case ContentGenerator.TOP: 
				screenX = -cg.workingVertexModel.z;
				screenY = -cg.workingVertexModel.x;
				break;
			case ContentGenerator.BOTTOM:
				screenX = -cg.workingVertexModel.z;
				screenY = cg.workingVertexModel.x;
				break;				
			default:
				screenX = 0;
				screenY = 0;
		}
		
		screenX = screenX + ContentGenerator.EDIT_SCREEN_MIDX;
		screenY = ContentGenerator.EDIT_SCREEN_MIDY - screenY;

		if(screenX < 0)screenX = 0;
		if(screenY < 0)screenY = 0;
		if(screenX >= ContentGenerator.EDIT_SCREEN_WIDTH)screenX = ContentGenerator.EDIT_SCREEN_WIDTH - 1;
		if(screenY >= ContentGenerator.EDIT_SCREEN_HEIGHT)screenY = ContentGenerator.EDIT_SCREEN_HEIGHT - 1;
		
		cg.cursor_x = screenX;
		cg.cursor_y = screenY;
		cg.renderEditArea();
	}
}

/** class to handle mouse clicks on the edit area */
class ContentGeneratorMouseListener extends MouseInputAdapter
{
	ContentGenerator cg;
	ContentGeneratorController cgc;
	
	ContentGeneratorMouseListener(ContentGenerator cg, ContentGeneratorController cgc)
	{
		this.cg = cg;
		this.cgc = cgc;
	}
	
	public void mouseClicked(MouseEvent e)
	{
		super.mouseMoved(e);
		if(e.getComponent() == cg.nc)
		{
			System.out.println("mouseClicked over nc");
			System.out.println("raw: x=" + e.getX() + ", y=" + e.getY());
			int rawX = e.getX();
			int rawY = e.getY();
			if(rawY < ContentGenerator.CONSTRAINED_BORDER_WIDTH)
			{
				cg.cursor_x = rawX;
			}
			else if(rawX < ContentGenerator.CONSTRAINED_BORDER_WIDTH)
			{
				cg.cursor_y = rawY;
			}
			else
			{
				cg.cursor_x = rawX;
				cg.cursor_y = rawY;					
			}
		}
		cg.renderEditArea();
		cgc.updateWorkingVertex();	
	}
}

/** class to handle Template... menu item */
class TemplateMenuItemAction extends AbstractAction
{
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
