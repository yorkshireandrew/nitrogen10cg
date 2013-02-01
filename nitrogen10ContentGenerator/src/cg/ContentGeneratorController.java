package cg;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

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
		
		
		// handle view direction buttons
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
		
		// handle pick buttons
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
		
		if(vertexAlreadyThere(wvmx,wvmy,wvmz) != null)
		{
			JOptionPane.showMessageDialog(cg, "A vertex is already there");
			return;			
		}

		saveSISI();	
		addImmutableVertex(wvmx,wvmy,wvmz);
	}
	
	/** moves the working vertex to cursors current position */
	void moveVertex()
	{
		// get where we want to add the vertex
		WorkingVertexModel wvm = cg.workingVertexModel;
		if(wvm.pickedVertex == null)return;
		
		// also check current cursor position has nothing under it
		int wvmx = wvm.x;
		int wvmy = wvm.y;
		int wvmz = wvm.z;
		
		ContentGeneratorSISI cgsisi = cg.contentGeneratorSISI;
		
		if(vertexAlreadyThere(wvmx,wvmy,wvmz) != null)
		{
			JOptionPane.showMessageDialog(cg, "A vertex is already there");
			return;
		}
		
		saveSISI();
		
		ImmutableVertex vertexToMove = wvm.pickedVertex;
		
		vertexToMove.is_x = wvmx;
		vertexToMove.is_y = wvmy;
		vertexToMove.is_z = wvmz;
		
		updateGeneratedSISIVertexes();
		
		// create a new generatedItem with the vertex
		updateGeneratedItemAndEditArea();
		
		// ensure we make it visible in edit area
		cg.renderEditArea();		
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
		wvm.pickedVertex = cg.contentGeneratorSISI.immutableVertexList.get(i);

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
		wvm.pickedVertex = cg.contentGeneratorSISI.immutableVertexList.get(i);

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
	
		wvm.pickedVertex = cg.contentGeneratorSISI.immutableVertexList.get(i);

		wvm.x = (int)v.getX();
		wvm.y = (int)v.getY();
		wvm.z = (int)v.getZ();
		wvm.computeDistances();
		cgL.workingVertexView.updateFromModel();	
		updateCursorFromWorkingVertex();
	}
	
	/** returns the index into immutableVertexes if the vertex already exists, else -1 */
	ImmutableVertex vertexAlreadyThere(int x, int y, int z)
	{
		ContentGeneratorSISI cgsisi = cg.contentGeneratorSISI;

		
		// return if an existing vertex is at same location
		List<ImmutableVertex> iv = cgsisi.immutableVertexList;	
		int ivl = iv.size();
		for(int index = 0; index < ivl ; index++)
		{
			ImmutableVertex checkVertex = iv.get(index);
			if(
					(((int)checkVertex.is_x) == x)
					&&(((int)checkVertex.is_y) == y)
					&&(((int)checkVertex.is_z) == z)
				)
			{
				return(checkVertex);
			}
		}
		return null;
	}
	
	void addImmutableVertex(int x, int y, int z)
	{
		ContentGeneratorSISI cgsisi = cg.contentGeneratorSISI;
		List<ImmutableVertex> ivl = cgsisi.immutableVertexList;
		ImmutableVertex newImmutableVertex = new ImmutableVertex(x,y,z);
		ivl.add(newImmutableVertex);

		updateGeneratedSISIVertexes();
		
		// if the added vertex is at the working vertex then update the working vertex
		WorkingVertexModel wvm = cg.workingVertexModel;
		
		if((x == wvm.x) && (y == wvm.y) && (z == wvm.z))
		{
			wvm.pickedVertex = newImmutableVertex;
			cg.workingVertexView.updateFromModel();				
		}
		
		// create a new generatedItem including the new vertex
		updateGeneratedItemAndEditArea();
		
		// ensure we make it visible in edit area
		cg.renderEditArea();
	}
	
	private void updateGeneratedSISIVertexes()
	{
		ContentGeneratorSISI cgsisi = cg.contentGeneratorSISI;
		List<ImmutableVertex> ivl = cgsisi.immutableVertexList;
		cg.generatedSISI.immutableVertexes = ivl.toArray(new ImmutableVertex[0]);
	}
	
	/** Hook for UNDO*/
	void saveSISI()
	{
		// TO DO
		return;
	}
	
	void updateGeneratedItemAndEditArea()
	{
		ContentGenerator cgL = cg;
		cgL.viewDirectionTransform.remove(cgL.generatedItem);
        cgL.generatedItem = Item.createItem(cgL.generatedSISI,cgL.viewDirectionTransform);
        cgL.generatedItem.setVisibility(true);
        cg.renderEditArea();
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

/** class to handle vertex data toolbar button */
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

/** class to handle Template... menu item */
class BacksideToolbarAction extends AbstractAction
{
	ContentGenerator cg;
	BacksideToolbarAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		// check a backside can be created using static method
		if(BacksideDialog.polygonVertexesAreOK(cg))
		{
			// A backside can be created so open the dialog
			BacksideDialog bd = new BacksideDialog(cg);
			bd.setVisible(true);
		}	
	}
}

/** class to handle Template... menu item */
class polygonToolbarAction extends AbstractAction
{
	ContentGenerator cg;
	polygonToolbarAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		PolygonDialog pd = new PolygonDialog(cg);
		pd.setVisible(true);
	}	
}

class VertexesOnlyToolbarAction extends AbstractAction
{
	ContentGenerator cg;
	VertexesOnlyToolbarAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		cg.viewDetail = ContentGenerator.VERTEXES_ONLY;
		NitrogenContext nc = cg.nc;
		nc.isPicking = true;
		RendererTriplet.setPickingRenderer( new Renderer_Null());	
		nc.contentGeneratorForcesNoCulling = true;
		cg.renderEditArea();		
	}	
}

class FullWireFrameToolbarAction extends AbstractAction
{
	ContentGenerator cg;
	FullWireFrameToolbarAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		cg.viewDetail = ContentGenerator.WIREFRAME;
		NitrogenContext nc = cg.nc;
		nc.isPicking = true;
		RendererTriplet.setPickingRenderer( new Renderer_Outline());	
		nc.contentGeneratorForcesNoCulling = true;
		cg.renderEditArea();		
	}	
}

class BacksideCulledWireFrameToolbarAction extends AbstractAction
{
	ContentGenerator cg;
	BacksideCulledWireFrameToolbarAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		cg.viewDetail = ContentGenerator.BACKSIDE_CULLED_WIREFRAME;
		NitrogenContext nc = cg.nc;
		nc.isPicking = true;
		RendererTriplet.setPickingRenderer( new Renderer_Outline());	
		nc.contentGeneratorForcesNoCulling = false;
		cg.renderEditArea();		
	}	
}

class FullyRenderedToolbarAction extends AbstractAction
{
	ContentGenerator cg;
	FullyRenderedToolbarAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		cg.viewDetail = ContentGenerator.FULLY_RENDERED;
		NitrogenContext nc = cg.nc;
		nc.isPicking = false;	
		nc.contentGeneratorForcesNoCulling = false;
		cg.renderEditArea();		
	}	
}


