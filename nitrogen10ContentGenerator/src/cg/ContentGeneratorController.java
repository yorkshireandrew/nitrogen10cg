package cg;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import com.bombheadgames.nitrogen2.*;



public class ContentGeneratorController extends AbstractAction implements ChangeListener
{
	private static final long serialVersionUID = 1L;
	
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
		
		if(source == cgL.textureButton)
		{
			System.out.println("textureButton pressed");
			handleTextureViewButton();
		}
		
		if(source == cgL.textureMapCombo)
		{
			System.out.println("textureMap combo changed");
			handleTextureMapCombo();
		}
		
		if(source == cgL.setNearRendererDistanceButton)
		{
			handleNearRendererDistanceButton();
		}
		if(source == cgL.setNearRendererOffButton)
		{
			handleNearRendererOffButton();
		}
		if(source == cgL.setNearRendererOnButton)
		{
			handleNearRendererOnButton();
		}
		
		if(source == cgL.setFarRendererDistanceButton)
		{
			handleFarRendererDistanceButton();
		}
		
		if(source == cgL.setImprovedDetailDistanceButton)
		{
			handleImprovedDetailDistanceButton();
		}
		
		if(source == cgL.setHLPBreakingDistanceButton)
		{
			handleHLPBreakingDistanceButton();
		}
		
		if(source == cgL.setHLPBreakingOnButton)
		{
			handleHLPBreakingOnButton();
		}
		
		if(source == cgL.setHLPBreakingOffButton)
		{
			handleHLPBreakingOffButton();
		}
		
		if(source == cgL.setBillboardOrientationDistanceButton)
		{
			handleBillboardOrientationDistanceButton();
		}
		
		if(source == cgL.setFarPlaneDistanceButton)
		{
			handleFarPlaneDistanceButton();
		}	
		
		if(source == cgL.pickPolygonButton)
		{
			handlePickPolygonButton();
		}	
		
		if(source == cgL.undoButton)
		{
			handleUndoButton();
		}	
		
		if(source == cgL.textureSetRef)
		{
			cgL.textureRefX = Integer.parseInt(cgL.textureMapX.getText());
			cgL.textureRefY = Integer.parseInt(cgL.textureMapY.getText());
			int dx = cg.cursor_x - cg.textureRefX;
			int dy = cg.cursor_y - cg.textureRefY;
			
			cg.textureMapDX.setEditable(true);
			cg.textureMapDY.setEditable(true);
			cg.textureMapDX.setText("" + dx);
			cg.textureMapDY.setText("" + dy);
			cg.textureMapDX.setEditable(false);
			cg.textureMapDY.setEditable(false);
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
		System.out.println("frontview called");
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
		int size = 1;
		if(cg.pickVertexSizeButton.isSelected())size = 3;
		int i = gi.findNearestVertexAt(cgL.cursor_x,cgL.cursor_y,nearClip, size);
		
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
		int size = 1;
		if(cg.pickVertexSizeButton.isSelected())size = 3;
		int i = gi.findFurthestVertexAt(cgL.cursor_x,cgL.cursor_y,nearClip, size);
		
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
		if(cg.undoStack != null)
		{
			try {
				cg.undoStack.push(DeepCloner.clone(cg.contentGeneratorSISI));
				cg.undoButton.setEnabled(true);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return;
	}
	
	void updateGeneratedItemAndEditArea()
	{
		ContentGenerator cgL = cg;
		cgL.viewDirectionTransform.remove(cgL.generatedItem);
		cgL.generatedSISI = cgL.contentGeneratorSISI.generateSISI(); 
        cgL.generatedItem = Item.createItem(cgL.generatedSISI,cgL.viewDirectionTransform);
        cgL.generatedItem.setVisibility(true);
        cg.renderEditArea();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		ContentGenerator cgL = cg;
		
		if(cg.viewType == ContentGenerator.PERSPECTIVE)
		{
			// Listen to changes of perspective sliders
			if(e.getSource() == cgL.distSlider)
			{
				// Logarithmic slider
				float newdist = distFromSlider();			
				cg.distTransform.a34 = -newdist;
				cg.distTransform.setNeedsTranslationUpdating();
				cg.renderEditArea();
			}
			
			if(e.getSource() == cgL.turnSlider)
			{
				int val = cgL.turnSlider.getModel().getValue();
				cgL.turnTransform.setTurn(val);
				cg.renderEditArea();
			}
			
			if(e.getSource() == cgL.climbSlider)
			{
				int val = cgL.climbSlider.getModel().getValue();
				cgL.climbTransform.setClimb(val);
				cg.renderEditArea();
			}
			
			if(e.getSource() == cgL.rollSlider)
			{
				int val = cgL.rollSlider.getModel().getValue();
				cgL.rollTransform.setRoll(val);
				cg.renderEditArea();
			}
		}
	}
	
	void handleOrthogonalViewButton()
	{
		ContentGenerator cgL = cg;
		cgL.viewType = ContentGenerator.ORTHOGONAL_PROJECTION;
		cgL.rightHandControls.removeAll();
		cgL.rightHandControls.add(cg.orthogonalViewTypeControls);
		cgL.rightHandControls.revalidate();
		
		NitrogenContext ncL = cgL.nc;		
		ncL.contentGeneratorForcesNoPerspective = true;
		
		// set transforms back to default front view
		cgL.rootTransform.setTransform(
				1f, 0f, 0f, 0f,
				0f, 1f, 0f, 0f,
				0f, 0f, 1f, 0f);
		
	    cgL.distTransform.setTransform(
				1f, 0f, 0f, 0f,
				0f, 1f, 0f, 0f,
				0f, 0f, 1f, -500f);
	    
	    cgL.turnTransform.setTransform(
				1f, 0f, 0f, 0f,
				0f, 1f, 0f, 0f,
				0f, 0f, 1f, 0f);
	    
	    cgL.climbTransform.setTransform(
				1f, 0f, 0f, 0f,
				0f, 1f, 0f, 0f,
				0f, 0f, 1f, 0f);
	    
	    cgL.rollTransform.setTransform(
				1f, 0f, 0f, 0f,
				0f, 1f, 0f, 0f,
				0f, 0f, 1f, 0f);
	    
	    cgL.viewDirectionTransform.setTransform(
				1f, 0f, 0f, 0f,
				0f, 1f, 0f, 0f,
				0f, 0f, 1f, 0f);
	    
	    // ensure perspective sliders are aligned for next visit
		// initialise the distance slider
		cgL.distSlider.getModel().setValue(1000);
		cgL.turnSlider.getModel().setValue(0);
		cgL.climbSlider.getModel().setValue(0);
		cgL.rollSlider.getModel().setValue(0);
		
		cgL.renderEditArea();
		cgL.cgc.updateWorkingVertexFromCursor();
	}
	
	void handlePerspectiveViewButton()
	{		
		cg.viewType = ContentGenerator.ORTHOGONAL_PROJECTION;
		System.out.println("perspective button pressed");
		cg.frontViewButton.setSelected(true);
		frontView();
		cg.viewType = ContentGenerator.PERSPECTIVE;

		cg.rightHandControls.removeAll();
		cg.rightHandControls.add(cg.perspectiveViewTypeControls);
		cg.rightHandControls.revalidate();	
		
		NitrogenContext ncL = cg.nc;
		ncL.contentGeneratorForcesNoPerspective = false;
		cg.renderEditArea();	
		System.out.println("--------------------------");
	}
	
	void handleTextureViewButton()
	{		
		cg.viewType = ContentGenerator.TEXTURE_MAP;
		
		Map<String,ContentGeneratorTextureMap> texMapMap = cg.contentGeneratorSISI.textureMapMap;
		if(texMapMap == null)return;
		
			// get available textures
			String[] availableTextures;
			availableTextures = texMapMap.keySet().toArray(new String[0]);
			
			// create combo box
			JComboBox combo = new JComboBox(availableTextures);
			combo.setEditable(true);
			combo.getEditor().setItem(cg.selectedTextureMap);
			combo.setMaximumSize(combo.getPreferredSize());
			combo.addActionListener(this); // listen so we can edit existing polygons
			cg.textureMapCombo = combo;
			
			// rebuild textureMapViewTypeControls
			cg.textureMapViewTypeControls.removeAll();
			cg.textureMapViewTypeControls.add(combo);
			
			cg.textureMapX = new JTextField(6);
			cg.textureMapY = new JTextField(6);
			cg.textureMapX.setMaximumSize(cg.textureMapX.getPreferredSize());
			cg.textureMapY.setMaximumSize(cg.textureMapY.getPreferredSize());
			
			cg.textureMapDX = new JTextField(6);
			cg.textureMapDY = new JTextField(6);
			cg.textureMapDX.setMaximumSize(cg.textureMapDX.getPreferredSize());
			cg.textureMapDY.setMaximumSize(cg.textureMapDY.getPreferredSize());
			cg.textureMapDX.setEditable(false);
			cg.textureMapDY.setEditable(false);
			
			cg.textureSetRef = new FixedSizeButton("/res/setRefButton.PNG");
			cg.textureSetRef.setAction(cg.cgc);
			
			Box refBox = Box.createHorizontalBox();
			refBox.add(cg.textureMapDX);
			refBox.add(cg.textureMapDY);
			refBox.add(cg.textureSetRef);
			refBox.add(Box.createHorizontalGlue());
			
			Box whereBox = Box.createHorizontalBox();
			whereBox.add(cg.textureMapX);
			whereBox.add(cg.textureMapY);
			whereBox.add(Box.createHorizontalGlue());
			
			cg.textureMapViewTypeControls = Box.createVerticalBox();
			cg.textureMapViewTypeControls.add(cg.textureMapCombo);
			cg.textureMapViewTypeControls.add(whereBox);
			cg.textureMapViewTypeControls.add(refBox);
			cg.textureMapViewTypeControls.add(Box.createVerticalGlue());
		
			// add controls to rightHandControls
		cg.rightHandControls.removeAll();
		cg.rightHandControls.add(cg.textureMapViewTypeControls);
		cg.rightHandControls.revalidate();	
		
		// TO DO 
		cg.renderEditArea();	
	}
	
	void handleTextureMapCombo()
	{
		System.out.println("texture map combo changed");
		
		String name = (String) cg.textureMapCombo.getEditor().getItem();
		if(name == null)return;
		ContentGeneratorTextureMap texMap = cg.contentGeneratorSISI.textureMapMap.get(name);
		if(texMap == null)return;
		
		
		// create texture only if it fits on the screen
		int h = cg.nc.h;
		int w = cg.nc.w;
		
		int th = texMap.textureMap.h;
		int tw = texMap.textureMap.w;
		int[] tp = texMap.textureMap.tex;
		
		if(th > h)return;
		if(tw > w)return;
			
		int[] p = new int[cg.nc.s];
		
		// create background
		for(int y = 0; y < h; y++)
		{
			int off = y * w;
			for(int x = 0; x < w; x++)
			{
				p[off+x] = 0xFF000000;
			}
		}
		
		// add texture 
		for(int y = 0; y < th; y++)
		{
			int off = y * w;
			int toff = y * tw;
			for(int x = 0; x < tw; x++)
			{
				p[off+x] = tp[toff + x];
			}
		}
		
		cg.textureMapPixels = p;
		cg.textureMapXMax = tw;
		cg.textureMapYMax = th;
		cg.selectedTextureMap = name;
		cg.renderEditArea();
	}
	
	private float distFromSlider()
	{
		int val = cg.distSlider.getModel().getValue();
		double d_val = (double)val;
		d_val = d_val / 1000;
		float newdist = (float)Math.pow(10, d_val);
		newdist = newdist * 50;
		System.out.println("dist=" + newdist);
		return newdist;
	}
	
	private void handleNearRendererDistanceButton()
	{
		cg.contentGeneratorSISI.nearRendererDist = distFromSlider();
		cg.cgc.updateGeneratedItemAndEditArea();
	}
	
	private void handleNearRendererOffButton()
	{
		cg.contentGeneratorSISI.nearRendererDist = 1f;
		cg.cgc.updateGeneratedItemAndEditArea();
	}
	private void handleNearRendererOnButton()
	{
		cg.contentGeneratorSISI.nearRendererDist = 1000000f;
		cg.cgc.updateGeneratedItemAndEditArea();
	}
	
	private void handleFarRendererDistanceButton()
	{
		cg.contentGeneratorSISI.farRendererDist = distFromSlider();
		System.out.println("Far Renderer Dist " + cg.contentGeneratorSISI.farRendererDist);
		cg.cgc.updateGeneratedItemAndEditArea();
	}
	
	private void handleImprovedDetailDistanceButton()
	{
		cg.contentGeneratorSISI.improvedDetailDist = distFromSlider();
		cg.cgc.updateGeneratedItemAndEditArea();
	}
	
	private void handleHLPBreakingDistanceButton()
	{
		cg.contentGeneratorSISI.hlpBreakingDist = distFromSlider();
		cg.cgc.updateGeneratedItemAndEditArea();
	}
	
	private void handleHLPBreakingOffButton()
	{
		cg.contentGeneratorSISI.hlpBreakingDist = 1f;
		cg.cgc.updateGeneratedItemAndEditArea();
	}
	
	private void handleHLPBreakingOnButton()
	{
		cg.contentGeneratorSISI.hlpBreakingDist = 1000000f;
		cg.cgc.updateGeneratedItemAndEditArea();
	}
	
	private void handleBillboardOrientationDistanceButton()
	{
		cg.contentGeneratorSISI.billboardOrientationDist = distFromSlider();
		cg.cgc.updateGeneratedItemAndEditArea();
	}
	
	private void handleFarPlaneDistanceButton()
	{
		cg.contentGeneratorSISI.farPlane = distFromSlider();
		cg.cgc.updateGeneratedItemAndEditArea();
	}
	
	private void handlePickPolygonButton()
	{
		// save state
		Renderer originalPickingRenderer = RendererTriplet.getPickingRenderer();
		NitrogenContext cgnc = cg.nc;
		boolean originalIsPicking = cgnc.isPicking;
		// do picking
		
		System.out.println("******************************");
		System.out.println("******************************");
		System.out.println("******************************");
		System.out.println("picking a polygon");
		System.out.println("******************************");
		System.out.println("******************************");
		System.out.println("******************************");
		
		cgnc.pickDetected = false;
		cgnc.pickX = cg.cursor_x;
		cgnc.pickY = cg.cursor_y;
		System.out.println("pick point " + cgnc.pickX + "," +cgnc.pickY);
		cgnc.isPicking = true;
		cgnc.pickedPolygon = -1;
		RendererTriplet.setPickingRenderer( new Renderer_Picking());
		cgnc.clearZBuffer();
		cg.rootTransform.render(cgnc);
		boolean pickDetected = cgnc.pickDetected;
		int pickedPolygon = cgnc.pickedPolygon; 
		
		// return state
		RendererTriplet.setPickingRenderer(originalPickingRenderer);
		cgnc.isPicking = originalIsPicking;
		cgnc.clearZBuffer();
		cg.rootTransform.render(cgnc);
		
		if(pickDetected)
		{
			// we found a polygon
			System.out.println("******************************");
			System.out.println("found polygon "+ pickedPolygon);
			System.out.println("******************************");

			PolygonDialog.pickPolygon(cg,pickedPolygon);
			PolygonDialog pd = new PolygonDialog(cg);
			pd.setVisible(true);		
		}
		else
		{
			System.out.println("******************************");
			System.out.println("***** FAILED TO DETECT A PICK ******");
			System.out.println("******************************");
		}
	}
	
	private void handleUndoButton()
	{
		if(cg.undoStack == null)return;
		if(cg.undoStack.empty())
		{
			cg.undoButton.setEnabled(false);
			return;
		}
		cg.contentGeneratorSISI = cg.undoStack.pop();
		
		try {
			cg.contentGeneratorSISI.setUpTransientFields(cg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cg.cgc.updateGeneratedItemAndEditArea();
		cg.workingVertexModel.pickedVertex = null;
		cg.workingVertexView.updateFromModel();
		cg.polygonVertexViews[0].updateFromModel();
		cg.polygonVertexViews[1].updateFromModel();
		cg.polygonVertexViews[2].updateFromModel();
		cg.polygonVertexViews[3].updateFromModel();
		
		if(cg.undoStack.empty())
		{
			cg.undoButton.setEnabled(false);
			return;
		}
	}
	
	void clearPolygonVertexes()
	{
		PolygonVertexView[] pvv = cg.polygonVertexViews;
		int len  = pvv.length;
		for(int x = 0; x < len; x++)
		{
			pvv[x].pvm = null;
			pvv[x].updateFromModel();
		}
	}

}

























	










