package cg;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import com.bombheadgames.nitrogen1.NitrogenContext;

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
			int rawX = e.getX();
			int rawY = e.getY();
			
			if(cg.viewType == ContentGenerator.ORTHOGONAL_PROJECTION)
			{
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
					if(rawX >= cg.textureMapXMax)rawX = cg.textureMapXMax-1;
					if(rawY >= cg.textureMapYMax)rawY = cg.textureMapYMax-1;
				}
				
				cg.cursor_x = rawX;
				cg.cursor_y = rawY;	
				
				cg.textureMapX.setEditable(true);
				cg.textureMapY.setEditable(true);			
				cg.textureMapX.setText("" + cg.cursor_x);
				cg.textureMapY.setText("" + cg.cursor_y);
				cg.textureMapX.setEditable(false);
				cg.textureMapY.setEditable(false);
				
				int dx = cg.cursor_x - cg.textureRefX;
				int dy = cg.cursor_y - cg.textureRefY;
				
				cg.textureMapDX.setEditable(true);
				cg.textureMapDY.setEditable(true);
				cg.textureMapDX.setText("" + dx);
				cg.textureMapDY.setText("" + dy);
				cg.textureMapDX.setEditable(false);
				cg.textureMapDY.setEditable(false);				
			}
			cg.renderEditArea();
			cgc.updateWorkingVertexFromCursor();	
		}
	}
}
