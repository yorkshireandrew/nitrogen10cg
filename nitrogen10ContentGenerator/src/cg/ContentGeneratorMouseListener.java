package cg;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

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
		
		if(cg.viewType == ContentGenerator.TEXTURE_MAP)
		{
			if (cg.textureMapPixels != null)
			{
				if(cg.cursor_x >= cg.textureMapXMax)cg.cursor_x = cg.textureMapXMax-1;
				if(cg.cursor_y >= cg.textureMapYMax)cg.cursor_y = cg.textureMapYMax-1;
			}
			
			cg.textureMapX.setEditable(true);
			cg.textureMapX.setText("" + cg.cursor_x);
			cg.textureMapY.setText("" + cg.cursor_y);
			
		}
		cg.renderEditArea();
		cgc.updateWorkingVertexFromCursor();	
	}
}