package cg;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.event.MouseInputAdapter;

import modified_nitrogen1.*;


public class ContentGeneratorController extends AbstractAction
{
	ContentGenerator cg;
	
	ContentGeneratorController(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == cg.newVertexButton)
		{
			System.out.println("newVertexButton press");
			addNewVertex();
		}
		
		if(source == cg.moveVertexButton)
		{
			System.out.println("moveVertexButton pressed");
			moveVertex();
		}
		
		if(source == cg.frontViewButton)
		{
			System.out.println("frontViewButton pressed");
			frontView();
		}
		
		if(source == cg.leftViewButton)
		{
			System.out.println("leftViewButton pressed");
			leftView();
		}
		
		if(source == cg.backViewButton)
		{
			System.out.println("backViewButton pressed");
			backView();
		}
		
		if(source == cg.rightViewButton)
		{
			System.out.println("rightViewButton pressed");
			rightView();
		}
		
		if(source == cg.topViewButton)
		{
			System.out.println("topViewButton pressed");
			topView();
		}
		
		if(source == cg.bottomViewButton)
		{
			System.out.println("bottomViewButton pressed");
			bottomView();
		}
		
		if(source == cg.pickFrontVertexButton)
		{
			System.out.println("pickFrontVertexButton pressed");
			pickFrontVertex();
		}
		
		if(source == cg.pickBackVertexButton)
		{
			System.out.println("pickBackVertexButton pressed");
			pickBackVertex();
		}
		
		if(source == cg.pickXYZVertexButton)
		{
			System.out.println("pickXYZVertexButton pressed");
			pickXYZVertex();
		}
	}		
	
	void updateWorkingVertexFromCursor()
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
	
	/** adds a ImmutableVertex to the generatedSISI */
	void addNewVertex()
	{
		// get where we want to add the vertex
		WorkingVertexModel wvm = cg.workingVertexModel;
		int wvmx = wvm.x;
		int wvmy = wvm.y;
		int wvmz = wvm.z;
		
		// get the generatedSISI
		SharedImmutableSubItem gs = cg.generatedSISI;
		
		// return if an existing vertex is at same location
		ImmutableVertex[] iv = gs.getImmutableVertexes();	
		int ivl = iv.length;
		for(int index = 0; index < ivl ; index++)
		{
			ImmutableVertex checkVertex = iv[index];
			if(
					(((int)checkVertex.is_x) == ((int)wvmx))
					&&(((int)checkVertex.is_y) == ((int)wvmy))
					&&(((int)checkVertex.is_z) == ((int)wvmz))
				)
			{
				JOptionPane.showMessageDialog(cg, "A vertex is already there");
				return;
			}
		}
		
		saveSISI();
		
		ImmutableVertex[] newVertexArray = Arrays.copyOf(iv, (ivl+1));
		newVertexArray[ivl]= new ImmutableVertex(wvmx,wvmy,wvmz);
		gs.setImmutableVertexes(newVertexArray);
		
		// tell working vertex about it
		wvm.index = ivl;
		wvm.picked = true;
		cg.workingVertexView.updateFromModel();
		
		// create a new generatedItem with the vertex
		createNewGeneratedItem();
		
		// ensure we make it visible in edit area
		cg.renderEditArea();
	}
	
	/** moves the working vertex to cursors current position */
	void moveVertex()
	{
		// get where we want to add the vertex
		WorkingVertexModel wvm = cg.workingVertexModel;
		if(wvm.picked == false)return;
		
		// also check current cursor position has nothing under it
		int wvmx = wvm.x;
		int wvmy = wvm.y;
		int wvmz = wvm.z;
		
		// get the generatedSISI
		SharedImmutableSubItem gs = cg.generatedSISI;
		
		// return if an existing vertex is at same location
		ImmutableVertex[] iv = gs.getImmutableVertexes();	
		int ivl = iv.length;
		for(int index = 0; index < ivl ; index++)
		{
			ImmutableVertex checkVertex = iv[index];
			if(
					(((int)checkVertex.is_x) == ((int)wvmx))
					&&(((int)checkVertex.is_y) == ((int)wvmy))
					&&(((int)checkVertex.is_z) == ((int)wvmz))
				)
			{
				JOptionPane.showMessageDialog(cg, "A vertex is already there");
				return;
			}
		}
		
		saveSISI();
		
		ImmutableVertex vertexToMove = iv[wvm.index];
		
		vertexToMove.is_x = wvmx;
		vertexToMove.is_y = wvmy;
		vertexToMove.is_z = wvmz;
		
		// create a new generatedItem with the vertex
		createNewGeneratedItem();
		
		// ensure we make it visible in edit area
		cg.renderEditArea();
		
		
		
	}
	
	/** removes existing generatedItem and constructs a 
	 * new one form the generatedSISI
	 */
	void createNewGeneratedItem()
	{
		cg.viewDirectionTransform.remove(cg.generatedItem);
        cg.generatedItem = Item.createItem(cg.generatedSISI,cg.viewDirectionTransform);
        cg.generatedItem.setVisibility(true);
	}
	
	void frontView()
	{
		ContentGenerator cgL = cg;
		// this button only responds in orthogonal view
		if(cgL.viewType != ContentGenerator.ORTHOGONAL_PROJECTION)return;
		
		cgL.viewDirectionTransform.setTransform
		(
				1.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 1.0f, 0.0f, 0.0f,
				0.0f, 0.0f, 1.0f, 0.0f
		 );
		cgL.viewDirection = ContentGenerator.FRONT;
		updateCursorFromWorkingVertex();
		// ensure we make it visible in edit area
		cgL.renderEditArea();	
	}
	
	void leftView()
	{
		ContentGenerator cgL = cg;
		// this button only responds in orthogonal view
		if(cgL.viewType != ContentGenerator.ORTHOGONAL_PROJECTION)return;
		
		cgL.viewDirectionTransform.setTransform
		(
				0.0f, 0.0f, -1.0f, 0.0f,
				0.0f, 1.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f, 0.0f
		 );
		cgL.viewDirection = ContentGenerator.LEFT;
		updateCursorFromWorkingVertex();
		// ensure we make it visible in edit area
		cgL.renderEditArea();	
	}
	
	void backView()
	{
		ContentGenerator cgL = cg;
		// this button only responds in orthogonal view
		if(cgL.viewType != ContentGenerator.ORTHOGONAL_PROJECTION)return;
		
		cgL.viewDirectionTransform.setTransform
		(
				-1.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 1.0f, 0.0f, 0.0f,
				0.0f, 0.0f, -1.0f, 0.0f
		 );
		cgL.viewDirection = ContentGenerator.BACK;
		updateCursorFromWorkingVertex();
		// ensure we make it visible in edit area
		cgL.renderEditArea();	
	}
	
	void rightView()
	{
		ContentGenerator cgL = cg;
		// this button only responds in orthogonal view
		if(cgL.viewType != ContentGenerator.ORTHOGONAL_PROJECTION)return;
		
		cgL.viewDirectionTransform.setTransform
		(
				0.0f, 0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f, 0.0f,
				-1.0f, 0.0f, 0.0f, 0.0f
		 );
		cgL.viewDirection = ContentGenerator.RIGHT;
		updateCursorFromWorkingVertex();
		// ensure we make it visible in edit area
		cgL.renderEditArea();	
	}
	
	void topView()
	{
		ContentGenerator cgL = cg;
		// this button only responds in orthogonal view
		if(cgL.viewType != ContentGenerator.ORTHOGONAL_PROJECTION)return;
		
		cgL.viewDirectionTransform.setTransform
		(
				0.0f, 0.0f, -1.0f, 0.0f,
				-1.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 1.0f, 0.0f, 0.0f
		 );
		cgL.viewDirection = ContentGenerator.TOP;
		updateCursorFromWorkingVertex();
		// ensure we make it visible in edit area
		cgL.renderEditArea();	
	}
	
	void bottomView()
	{
		ContentGenerator cgL = cg;
		// this button only responds in orthogonal view
		if(cgL.viewType != ContentGenerator.ORTHOGONAL_PROJECTION)return;
		
		cgL.viewDirectionTransform.setTransform
		(
				0.0f, 0.0f, -1.0f, 0.0f,
				1.0f, 0.0f, 0.0f, 0.0f,
				0.0f, -1.0f, 0.0f, 0.0f
		 );
		cgL.viewDirection = ContentGenerator.BOTTOM;
		updateCursorFromWorkingVertex();
		// ensure we make it visible in edit area
		cgL.renderEditArea();	
	}
	
	void pickFrontVertex()
	{
		ContentGenerator cgL = cg;
		// this button only responds in orthogonal view
		if(cgL.viewType != ContentGenerator.ORTHOGONAL_PROJECTION)return;

		// force the Item to recalculate its 
		// vertexes view screen coordinates
		// it renders them as a side effect
		Item gi = cg.generatedItem;
		gi.calculateVertexes();
		gi.renderVertexes(cgL.nc);
		float nearClip = cgL.nc.nearClip;
		int i = gi.findNearestVertexAt(cgL.cursor_x,cgL.cursor_y,nearClip);
		
		// return if we did not find a vertex
		if(i == -1)return;
		Vertex v = gi.getVertex(i);
		WorkingVertexModel wvm = cgL.workingVertexModel;
	
		wvm.picked = true;
		wvm.index = i;
		wvm.x = (int)v.getX();
		wvm.y = (int)v.getY();
		wvm.z = (int)v.getZ();
		wvm.computeDistances();
		cgL.workingVertexView.updateFromModel();	
		updateCursorFromWorkingVertex();
	}
	
	void pickBackVertex()
	{
		ContentGenerator cgL = cg;
		// this button only responds in orthogonal view
		if(cgL.viewType != ContentGenerator.ORTHOGONAL_PROJECTION)return;

		// force the Item to recalculate its 
		// vertexes view screen coordinates
		// it renders them as a side effect
		Item gi = cg.generatedItem;
		gi.calculateVertexes();
		gi.renderVertexes(cgL.nc);
		float nearClip = cgL.nc.nearClip;
		int i = gi.findFurthestVertexAt(cgL.cursor_x,cgL.cursor_y,nearClip);
		
		// return if we did not find a vertex
		if(i == -1)return;
		Vertex v = gi.getVertex(i);
		WorkingVertexModel wvm = cgL.workingVertexModel;
	
		wvm.picked = true;
		wvm.index = i;
		wvm.x = (int)v.getX();
		wvm.y = (int)v.getY();
		wvm.z = (int)v.getZ();
		wvm.computeDistances();
		cgL.workingVertexView.updateFromModel();	
		updateCursorFromWorkingVertex();
	}
	
	void pickXYZVertex()
	{
		ContentGenerator cgL = cg;
		
		// this button only responds in orthogonal view
		if(cgL.viewType != ContentGenerator.ORTHOGONAL_PROJECTION)return;

		Item gi = cg.generatedItem;
		WorkingVertexModel wvm = cgL.workingVertexModel;
		
		int i = gi.findNearestVertexTo(wvm.x,wvm.y,wvm.z);
		
		// return if we did not find a vertex
		if(i == -1)return;
		Vertex v = gi.getVertex(i);
	
		wvm.picked = true;
		wvm.index = i;
		wvm.x = (int)v.getX();
		wvm.y = (int)v.getY();
		wvm.z = (int)v.getZ();
		wvm.computeDistances();
		cgL.workingVertexView.updateFromModel();	
		updateCursorFromWorkingVertex();
	}
	
	/** Hook for UNDO*/
	void saveSISI()
	{
		// TO DO
		return;
	}
	
}







// ************************************************
// ************************************************
// *********** Other controller classes ***********
//************************************************
//************************************************




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
		cgc.updateWorkingVertexFromCursor();	
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

/** class to handle Template... menu item */
class VertexDataToolbarAction extends AbstractAction
{
	ContentGenerator cg;
	VertexDataToolbarAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		VertexDataDialog td = new VertexDataDialog(cg);
		td.setVisible(true);
	}	
}
