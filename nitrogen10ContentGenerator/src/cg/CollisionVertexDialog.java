
package cg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.bombheadgames.nitrogen2.ImmutableCollisionVertex;


public class CollisionVertexDialog  extends JDialog implements ChangeListener{
	private static final long serialVersionUID = 1L;

	private ContentGenerator contentGenerator;
	
	boolean initialHasCollisionVertexes;
	List<ImmutableCollisionVertex> initialCollisionVertexList;
	
	JSpinner			lengthSpinner;
	
	List<JSpinner> 	collisionVertexX;
	List<JSpinner> 	collisionVertexY;
	List<JSpinner> 	collisionVertexZ;
	List<JSpinner> 	collisionVertexRadius;
	List<JLabel> 	dataLabels;
	
	JLabel lengthLabel 		= new JLabel("Length ");
	
	JButton cancelButton;
	JButton okButton;
	
	int length = 0;
		
	CollisionVertexDialog(ContentGenerator cg)
	{
		super(cg);
		setTitle("Collision Vertexes");
		contentGenerator = cg;		
		ContentGeneratorSISI cgSISI = cg.contentGeneratorSISI;
		
		cg.cgc.saveSISI();
		
		// Should not need to deep clone anymore as we are using undo stack
		
		/*
		// deep copy the present collision vertex state in case we cancel
		// this dialog makes immediately copies changes in the dialog to  
		// the content generators contentGeneratorSISI.collisonVertexList
		initialHasCollisionVertexes = cgSISI.hasCollisionVertexes;
		if(cgSISI.collisionVertexList != null)
		{
			int size = cgSISI.collisionVertexList.size();
			initialCollisionVertexList = new ArrayList<ImmutableCollisionVertex>();
			for (int x = 0; x < size; x++)
			{
				initialCollisionVertexList.add(new ImmutableCollisionVertex(cgSISI.collisionVertexList.get(x)));
			}
		}
			
			*/
		// calculate size of existing collision vertexes
		length  = 0;
		if(cgSISI.hasCollisionVertexes)
			{
			  if(cgSISI.collisionVertexList != null)
				  {
				  	length = cgSISI.collisionVertexList.size();
				  }
			}
			
		// initialise the lengthSpinner
		lengthSpinner = new JSpinner();
		lengthSpinner.setModel(new SpinnerNumberModel(length,0,100,1));
		lengthSpinner.addChangeListener(this);
		lengthSpinner.setMaximumSize(lengthSpinner.getPreferredSize());
		
		// instantiate the other spinner lists
		collisionVertexX = new ArrayList<JSpinner>();
		collisionVertexY = new ArrayList<JSpinner>();
		collisionVertexZ = new ArrayList<JSpinner>();
		collisionVertexRadius = new ArrayList<JSpinner>();		
		dataLabels = new ArrayList<JLabel>();
		
		// initialize the other spinner lists
		initializeSpinners();

		// create control buttons
		cancelButton = new JButton("CANCEL");
		cancelButton.addActionListener(
				new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) {
				//		contentGenerator.contentGeneratorSISI.hasCollisionVertexes = initialHasCollisionVertexes;
				//		contentGenerator.contentGeneratorSISI.collisionVertexList = initialCollisionVertexList;
				
						contentGenerator.contentGeneratorSISI = contentGenerator.undoStack.pop();
						CollisionVertexDialog.this.setVisible(false);
						CollisionVertexDialog.this.dispose();
						contentGenerator.cgc.updateGeneratedItemAndEditArea();
					}			
				});	
		
		okButton = new JButton("OK");
		
		okButton.addActionListener(
				new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) {
						CollisionVertexDialog.this.handleOK();	
					}			
				});	
		
		this.setSize(500,300);
		this.setModal(false); // do not set modal as we need to change view directions
		this.generateContent();
		this.validate();
		this.setLocationRelativeTo(cg);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}

	@Override
	public void stateChanged(ChangeEvent e) {
		if(e.getSource() == lengthSpinner)
		{
			handleLengthSpinnerChange();
			return;
		}		
	}
	
	void handleLengthSpinnerChange()
	{
		int proposedLength = (Integer)lengthSpinner.getModel().getValue();
		
		if(proposedLength < length)
		{
			List<JSpinner> newCollisionVertexX = new ArrayList<JSpinner>();
			List<JSpinner> newCollisionVertexY = new ArrayList<JSpinner>();
			List<JSpinner> newCollisionVertexZ = new ArrayList<JSpinner>();
			List<JSpinner> newCollisionVertexRadius = new ArrayList<JSpinner>();		
			List<JLabel> newDataLabels = new ArrayList<JLabel>();
			
			// copy across existing data
			for(int x = 0; x < proposedLength; x++)
			{
				newCollisionVertexX.add(collisionVertexX.get(x));
				newCollisionVertexY.add(collisionVertexY.get(x));
				newCollisionVertexZ.add(collisionVertexZ.get(x));
				newCollisionVertexRadius.add(collisionVertexRadius.get(x));
				newDataLabels.add(dataLabels.get(x));
			}
			
			collisionVertexX = newCollisionVertexX;
			collisionVertexY = newCollisionVertexY;
			collisionVertexZ = newCollisionVertexZ;
			collisionVertexRadius = newCollisionVertexRadius;
			length = proposedLength;
			updateContentGeneratorSISI();
			generateContent();
			this.validate();
			this.repaint();
		}
		
		if(proposedLength > length)
		{
			for(int x = length; x < proposedLength; x++)
			{
				addNewCollisionVertex(0,0,0,0,x);
			}
			length = proposedLength;
			updateContentGeneratorSISI();
			generateContent();
			this.validate();
			this.repaint();
		}	
	}
	
	private void addNewCollisionVertex(int x, int y, int z, int radius, final int index)
	{
		JSpinner newCollisionVertexX = new JSpinner();				
		newCollisionVertexX.setModel(new SpinnerNumberModel(x,-1000,1000,1));
		newCollisionVertexX.addChangeListener(
				new ChangeListener(){
					@Override
					public void stateChanged(ChangeEvent e) {
						JSpinner s = (JSpinner)e.getSource();
						float v = (float)(Integer)s.getModel().getValue();
						ContentGeneratorSISI cgSISI = contentGenerator.contentGeneratorSISI;
						ImmutableCollisionVertex cv = cgSISI.collisionVertexList.get(index);
						cv.is_x = v;
						contentGenerator.cgc.updateGeneratedItemAndEditArea();		
					}});
		newCollisionVertexX.setMaximumSize(newCollisionVertexX.getPreferredSize());
		
		JSpinner newCollisionVertexY = new JSpinner();				
		newCollisionVertexY.setModel(new SpinnerNumberModel(y,-1000,1000,1));
		newCollisionVertexY.addChangeListener(
				new ChangeListener(){
					@Override
					public void stateChanged(ChangeEvent e) {
						JSpinner s = (JSpinner)e.getSource();
						float v = (float)(Integer)s.getModel().getValue();
						ContentGeneratorSISI cgSISI = contentGenerator.contentGeneratorSISI;
						ImmutableCollisionVertex cv = cgSISI.collisionVertexList.get(index);
						cv.is_y = v;
						contentGenerator.cgc.updateGeneratedItemAndEditArea();		
					}});
		newCollisionVertexY.setMaximumSize(newCollisionVertexY.getPreferredSize());

		JSpinner newCollisionVertexZ = new JSpinner();				
		newCollisionVertexZ.setModel(new SpinnerNumberModel(z,-1000,1000,1));
		newCollisionVertexZ.addChangeListener(
				new ChangeListener(){
					@Override
					public void stateChanged(ChangeEvent e) {
						JSpinner s = (JSpinner)e.getSource();
						float v = (float)(Integer)s.getModel().getValue();
						ContentGeneratorSISI cgSISI = contentGenerator.contentGeneratorSISI;
						ImmutableCollisionVertex cv = cgSISI.collisionVertexList.get(index);
						cv.is_z = v;
						contentGenerator.cgc.updateGeneratedItemAndEditArea();		
					}});
		newCollisionVertexZ.setMaximumSize(newCollisionVertexZ.getPreferredSize());

		JSpinner newCollisionVertexRadius = new JSpinner();				
		newCollisionVertexRadius.setModel(new SpinnerNumberModel(radius,0,1000,1));

		newCollisionVertexRadius.setMaximumSize(newCollisionVertexRadius.getPreferredSize());
		newCollisionVertexRadius.addChangeListener(
				new ChangeListener(){
					@Override
					public void stateChanged(ChangeEvent e) {
						JSpinner s = (JSpinner)e.getSource();
						float v = (float)(Integer)s.getModel().getValue();
						ContentGeneratorSISI cgSISI = contentGenerator.contentGeneratorSISI;
						ImmutableCollisionVertex cv = cgSISI.collisionVertexList.get(index);
						cv.radius = v;
						contentGenerator.cgc.updateGeneratedItemAndEditArea();		
					}});		
		collisionVertexX.add(newCollisionVertexX);
		collisionVertexY.add(newCollisionVertexY);
		collisionVertexZ.add(newCollisionVertexZ);
		collisionVertexRadius.add(newCollisionVertexRadius);
		dataLabels.add(new JLabel(Integer.toString(index+1)));	
	}
	
	void initializeSpinners()
	{
		ContentGeneratorSISI cgSISI = contentGenerator.contentGeneratorSISI;
		if(!cgSISI.hasCollisionVertexes)return;
		if(cgSISI.collisionVertexList == null)return;
		List<ImmutableCollisionVertex> cvlL = cgSISI.collisionVertexList;
		int size = cvlL.size();
		
		for(int x = 0; x < size; x++)
		{
			ImmutableCollisionVertex cv = cvlL.get(x);
			addNewCollisionVertex(
					(int)cv.is_x,
					(int)cv.is_y,
					(int)cv.is_z,
					(int)cv.radius,
					x);
		}
		length = size;
	}
	
	private void generateContent()
	{	
		Box lengthBox = new Box(BoxLayout.X_AXIS);
		lengthBox.add(lengthLabel);
		lengthBox.add(lengthSpinner);
		
		Box buttonBox = new Box(BoxLayout.X_AXIS);
		buttonBox.add(okButton);
		buttonBox.add(cancelButton);
		
		Box dataBox = new Box(BoxLayout.Y_AXIS);	
		for( int x = 0; x < length; x++)
		{
			Box dataElementBox = new Box(BoxLayout.X_AXIS);
			dataElementBox.add(dataLabels.get(x));
			dataElementBox.add(collisionVertexX.get(x));
			dataElementBox.add(collisionVertexY.get(x));
			dataElementBox.add(collisionVertexZ.get(x));
			dataElementBox.add(collisionVertexRadius.get(x));
			dataBox.add(dataElementBox);
		}	

		Box dialog = new Box(BoxLayout.Y_AXIS);
		dialog.add(lengthBox);
		dialog.add(dataBox);
		dialog.add(buttonBox);
		this.getContentPane().removeAll();
		this.getContentPane().add(dialog);
	}

	private void handleOK() {
		// simply close
		this.setVisible(false);
		this.dispose();		
	}	

	void updateContentGeneratorSISI()
	{
		ContentGeneratorSISI cgSISI = contentGenerator.contentGeneratorSISI;
		
		if(length == 0)
		{
			cgSISI.hasCollisionVertexes = false;
			cgSISI.collisionVertexList = null;
			contentGenerator.cgc.updateGeneratedItemAndEditArea();
			return;
		}
		
		cgSISI.hasCollisionVertexes = true;
		List<ImmutableCollisionVertex> newList = new ArrayList<ImmutableCollisionVertex>();
			for(int x = 0; x < length; x++)
			{
				newList.add(new ImmutableCollisionVertex(
						(float)(Integer)collisionVertexX.get(x).getModel().getValue(),
						(float)(Integer)collisionVertexY.get(x).getModel().getValue(),
						(float)(Integer)collisionVertexZ.get(x).getModel().getValue(),
						(float)(Integer)collisionVertexRadius.get(x).getModel().getValue()
				));				
			}
		cgSISI.collisionVertexList = newList;
		contentGenerator.cgc.updateGeneratedItemAndEditArea();
	}
}
