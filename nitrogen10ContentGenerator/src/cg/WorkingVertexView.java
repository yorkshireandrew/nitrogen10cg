package cg;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class WorkingVertexView {
	ContentGenerator cg;
	WorkingVertexModel workingVertexModel;
	WorkingVertexController workingVertexController;
	
	JTextField indexTextField;
	JTextField xTextField;
	JTextField yTextField;
	JTextField zTextField;
	
	JTextField dxTextField;
	JTextField dyTextField;
	JTextField dzTextField;
	JTextField distTextField;
	
	FixedSizeButton setRefButton;
	FixedSizeButton moveToZeroXButton;
	
	WorkingVertexView(ContentGenerator cg, WorkingVertexModel workingVertexModel)
	{
		this.cg = cg;
		this.workingVertexModel = workingVertexModel;
		this.workingVertexController = new WorkingVertexController(cg, this, workingVertexModel);
		
		// now create the UI
		indexTextField = new JTextField();
		indexTextField.setColumns(4);
		indexTextField.setText("none");
		indexTextField.setEnabled(false);
		indexTextField.setMaximumSize(new Dimension(40,20));

		xTextField = new JTextField();
		xTextField.setColumns(4);
		xTextField.setText("0");
		xTextField.setAction(workingVertexController);
		xTextField.setMaximumSize(new Dimension(40,20));
		
		yTextField = new JTextField();
		yTextField.setColumns(4);
		yTextField.setText("0");
		yTextField.setAction(workingVertexController);
		yTextField.setMaximumSize(new Dimension(40,20));

		zTextField = new JTextField();
		zTextField.setColumns(4);
		zTextField.setText("0");
		zTextField.setAction(workingVertexController);
		zTextField.setMaximumSize(new Dimension(40,20));
		
		dxTextField = new JTextField();
		dxTextField.setColumns(4);
		dxTextField.setText("0");
		dxTextField.setEnabled(false);
		dxTextField.setMaximumSize(new Dimension(40,20));
		
		dyTextField = new JTextField();
		dyTextField.setColumns(4);
		dyTextField.setText("0");
		dyTextField.setEnabled(false);
		dyTextField.setMaximumSize(new Dimension(40,20));

		dzTextField = new JTextField();
		dzTextField.setColumns(4);
		dzTextField.setText("0");
		dzTextField.setEnabled(false);
		dzTextField.setMaximumSize(new Dimension(40,20));
		
		distTextField = new JTextField();
		distTextField.setColumns(4);
		distTextField.setText("0");
		distTextField.setEnabled(false);
		distTextField.setMaximumSize(new Dimension(40,20));
		
		setRefButton = new FixedSizeButton("/res/setRefButton.PNG");
		setRefButton.setAction(workingVertexController);
		setRefButton.setIcon("/res/setRefButton.PNG");

		moveToZeroXButton = new FixedSizeButton("/res/moveToZeroXButton.PNG");
		moveToZeroXButton.setAction(workingVertexController);
		moveToZeroXButton.setIcon("/res/moveToZeroXButton.PNG");
	}
	
	void createWorkingVertexGUI(Container container)
	{
		Box outerbox = new Box(BoxLayout.Y_AXIS);
		Box topbox = new Box(BoxLayout.X_AXIS);
		Box botbox = new Box(BoxLayout.X_AXIS);
		
		topbox.add(indexTextField);
		topbox.add(xTextField);
		topbox.add(yTextField);
		topbox.add(zTextField);
		topbox.add(moveToZeroXButton);
		topbox.add(Box.createHorizontalGlue());
		
		botbox.add(distTextField);
		botbox.add(dxTextField);
		botbox.add(dyTextField);
		botbox.add(dzTextField);
		botbox.add(setRefButton);
		botbox.add(Box.createHorizontalGlue());
		
		outerbox.add(topbox);
		outerbox.add(botbox);
		outerbox.setBorder(BorderFactory.createLineBorder(Color.black));
		
		container.add(outerbox);
	}
	
	void updateFromModel()
	{
		SwingUtilities.invokeLater(
				new Runnable()
				{
					@Override
					public void run() {
						WorkingVertexModel wvm = workingVertexModel;
						
						wvm.pickedVertex = WorkingVertexView.this.cg.cgc.vertexAlreadyThere(wvm.x, wvm.y, wvm.z);
						
						if(wvm.pickedVertex != null)
						{
							int index = cg.contentGeneratorSISI.immutableVertexList.indexOf(wvm.pickedVertex);
							indexTextField.setText(Integer.toString(index));
						}
						else
						{
							indexTextField.setText("none");
						}
						
						
						
						xTextField.setText(Integer.toString(wvm.x));
						yTextField.setText(Integer.toString(wvm.y));
						zTextField.setText(Integer.toString(wvm.z));
						
						dxTextField.setText(Integer.toString(wvm.dx));
						dyTextField.setText(Integer.toString(wvm.dy));
						dzTextField.setText(Integer.toString(wvm.dz));
						distTextField.setText(Integer.toString(wvm.dist));						
						
					}				
				});
	}
}
