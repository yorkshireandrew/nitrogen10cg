package cg;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.filechooser.FileFilter;

import modified_nitrogen1.*;


public class ContentGeneratorController extends AbstractAction implements ChangeListener
{
	ContentGenerator cg;
	
	ContentGeneratorController(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		ContentGenerator cgL = cg;
		if(source == cgL.newVertexButton)
		{
			System.out.println("newVertexButton press");
			addNewVertex();
		}
		
		if(source == cgL.moveVertexButton)
		{
			System.out.println("moveVertexButton pressed");
			moveVertex();
		}
		
		
		// handle view direction buttons
		if(source == cgL.frontViewButton)
		{
			System.out.println("frontViewButton pressed");
			frontView();
		}
		
		if(source == cgL.leftViewButton)
		{
			System.out.println("leftViewButton pressed");
			leftView();
		}
		
		if(source == cgL.backViewButton)
		{
			System.out.println("backViewButton pressed");
			backView();
		}
		
		if(source == cgL.rightViewButton)
		{
			System.out.println("rightViewButton pressed");
			rightView();
		}
		
		if(source == cgL.topViewButton)
		{
			System.out.println("topViewButton pressed");
			topView();
		}
		
		if(source == cgL.bottomViewButton)
		{
			System.out.println("bottomViewButton pressed");
			bottomView();
		}
		
		// handle pick buttons
		if(source == cgL.pickFrontVertexButton)
		{
			System.out.println("pickFrontVertexButton pressed");
			pickFrontVertex();
		}
		
		if(source == cgL.pickBackVertexButton)
		{
			System.out.println("pickBackVertexButton pressed");
			pickBackVertex();
		}
		
		if(source == cgL.pickXYZVertexButton)
		{
			System.out.println("pickXYZVertexButton pressed");
			pickXYZVertex();
		}
		
		if(source == cgL.orthogonalProjectionButton)
		{
			System.out.println("orthogonalProjectionButton pressed");
			handleOrthogonalViewButton();
		}
		
		if(source == cgL.perspectiveButton)
		{
			System.out.println("perspectiveButton pressed");
			handlePerspectiveViewButton();
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
		cgL.generatedSISI = cgL.contentGeneratorSISI.generateSISI(); 
		cgL.viewDirectionTransform.remove(cgL.generatedItem);
        cgL.generatedItem = Item.createItem(cgL.generatedSISI,cgL.viewDirectionTransform);
        cgL.generatedItem.setVisibility(true);
        cg.renderEditArea();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		ContentGenerator cgL = cg;
		
		if(cg.viewType == ContentGenerator.PERSPECTIVE)
		{
			// Listen to changes of perspective sliders
			if(e.getSource() == cgL.distSlider)
			{
				// Logarithmic slider
				int val = cgL.distSlider.getModel().getValue();
				double d_val = (double)val;
				d_val = d_val / 1000;
				float newdist = (float)Math.pow(10, d_val);
				newdist = newdist * 50;
				
				cg.distTransform.a34 = -newdist;
				cg.distTransform.setNeedsTranslationUpdating();
				cg.renderEditArea();
			}
			
			if(e.getSource() == cgL.distSlider)
			{
				// Logarithmic slider
				int val = cgL.distSlider.getModel().getValue();
				double d_val = (double)val;
				d_val = d_val / 1000;
				float newdist = (float)Math.pow(10, d_val);
				newdist = newdist * 50;
				
				cg.distTransform.a34 = -newdist;
				cg.distTransform.setNeedsTranslationUpdating();
				cg.renderEditArea();
			}
		}
	}
	
	void handleOrthogonalViewButton()
	{
		cg.rightHandControls.removeAll();
		cg.rightHandControls.add(cg.orthogonalViewTypeControls);
		cg.rightHandControls.revalidate();	
	}
	
	void handlePerspectiveViewButton()
	{
		cg.rightHandControls.removeAll();
		cg.rightHandControls.add(cg.perspectiveViewTypeControls);
		cg.rightHandControls.revalidate();	
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

class SaveMenuItemAction extends AbstractAction
{
	ContentGenerator cg;
	File initialFile = null;
	SaveMenuItemAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		final JFileChooser fileChooser = new JFileChooser(initialFile);
		fileChooser.setFileFilter(new GeneralFileFilter("ncg"));
		int retval = fileChooser.showSaveDialog(cg);

        if (retval == JFileChooser.APPROVE_OPTION) {
            File saveFile = fileChooser.getSelectedFile();
            
            // add file extension if it is not present
            if(new GeneralFileFilter("ncg").accept(saveFile) == false)
            {
            	// it needs an extension adding
            	String old = saveFile.getAbsolutePath();
            	saveFile = new File(old + ".ncg");
            }
            
            ObjectOutputStream output = null;
            try {
				output = new ObjectOutputStream(new FileOutputStream(saveFile));
				cg.writeToFile(output);
				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
            finally
            {
            	try {
					output.close();
				} catch (IOException e1) {e1.printStackTrace();}
            }        
        }	
	}
}
	



class LoadMenuItemAction extends AbstractAction
{
	ContentGenerator cg;
	File initialFile = null;
	LoadMenuItemAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		final JFileChooser fileChooser = new JFileChooser(initialFile);
		fileChooser.setFileFilter(new GeneralFileFilter("ncg"));

		int retval = fileChooser.showOpenDialog(cg);

        if (retval == JFileChooser.APPROVE_OPTION) {
            File loadFile = fileChooser.getSelectedFile();
            
            ObjectInputStream in = null;
            try {
				in = new ObjectInputStream(new FileInputStream(loadFile));
				cg.readFromFile(in);
				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
            finally
            {
            	try {
					in.close();
				} catch (IOException e1) {e1.printStackTrace();}
            }        
        }	
	}
}

class DeleteUnusedVertexesMenuItemAction extends AbstractAction
{
	ContentGenerator cg;
	DeleteUnusedVertexesMenuItemAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		cg.cgc.saveSISI();
		cg.contentGeneratorSISI.removeUnusedImmutableVertexes();
		cg.cgc.updateGeneratedItemAndEditArea();
	}	
}

class DeleteUnusedStuffMenuItemAction extends AbstractAction
{
	ContentGenerator cg;
	DeleteUnusedStuffMenuItemAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		cg.cgc.saveSISI();
		
		cg.contentGeneratorSISI.removeUnusedImmutableBacksides();
		cg.contentGeneratorSISI.removeUnusedPolygonData();
		cg.contentGeneratorSISI.removeUnusedTextureMaps();
		cg.contentGeneratorSISI.removeUnusedVertexData();
		cg.cgc.updateGeneratedItemAndEditArea();
	}	
}


class ExportMenuItemAction extends AbstractAction
{
	ContentGenerator cg;
	File initialFile = null;
	ExportMenuItemAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		final JFileChooser fileChooser = new JFileChooser(initialFile);
		fileChooser.setFileFilter(new GeneralFileFilter("nit"));

		int retval = fileChooser.showSaveDialog(cg);

        if (retval == JFileChooser.APPROVE_OPTION) {
            File saveFile = fileChooser.getSelectedFile();
            
            // add file extension if it is not present
            if(new GeneralFileFilter("nit").accept(saveFile) == false)
            {
            	// it needs an extension adding
            	String old = saveFile.getAbsolutePath();
            	saveFile = new File(old + ".nit");
            }
            
        
            ObjectOutputStream output = null;
            try {
				output = new ObjectOutputStream(new FileOutputStream(saveFile));
				output.writeObject(cg.generatedSISI);
				
			} catch (FileNotFoundException e1) {e1.printStackTrace();} 
            catch (IOException e1) {e1.printStackTrace();} 
            finally
            {
            	try {output.close();}
				catch (IOException e1) {e1.printStackTrace();}
            }
        }
	}

	}


