package cg;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JTextField;

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
		
		setRefButton = new FixedSizeButton("/res/fullRenderButton.PNG");
		setRefButton.setAction(workingVertexController);
		setRefButton.setIcon("/res/setRefButton.PNG");
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
		topbox.add(setRefButton);
		topbox.add(Box.createHorizontalGlue());
		
		botbox.add(distTextField);
		botbox.add(dxTextField);
		botbox.add(dyTextField);
		botbox.add(dzTextField);	
		botbox.add(Box.createHorizontalGlue());
		
		outerbox.add(topbox);
		outerbox.add(botbox);
		outerbox.setBorder(BorderFactory.createLineBorder(Color.black));
		
		container.add(outerbox);
	}
}
