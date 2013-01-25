package cg;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class PolygonVertexView {
	JTextField indexTextField;
	JTextField xTextField;
	JTextField yTextField;
	JTextField zTextField;
	FixedSizeButton addButton;
	FixedSizeButton moveWorkingToThisButton;
	
	ContentGenerator cg;
	PolygonVertexModel pvm;
	PolygonVertexController polygonVertexController;	
	
	PolygonVertexView(ContentGenerator cg, PolygonVertexModel pvm)
	{
		polygonVertexController = new PolygonVertexController(cg,this,pvm);
		this.pvm = pvm;
		
		indexTextField = new JTextField();
		indexTextField.setColumns(4);
		indexTextField.setText("none");
		indexTextField.setAction(polygonVertexController);
		indexTextField.setEnabled(false);
		indexTextField.setMaximumSize(new Dimension(40,20));

		xTextField = new JTextField();
		xTextField.setColumns(4);
		xTextField.setText("0");
		xTextField.setAction(polygonVertexController);
		xTextField.setMaximumSize(new Dimension(40,20));
		
		yTextField = new JTextField();
		yTextField.setColumns(4);
		yTextField.setText("0");
		yTextField.setAction(polygonVertexController);
		yTextField.setMaximumSize(new Dimension(40,20));

		zTextField = new JTextField();
		zTextField.setColumns(4);
		zTextField.setText("0");
		zTextField.setAction(polygonVertexController);
		zTextField.setMaximumSize(new Dimension(40,20));
		
		addButton = new FixedSizeButton("/res/fullRenderButton.PNG");
		addButton.setAction(polygonVertexController);
		addButton.setIcon("/res/addButton.PNG");
		
		moveWorkingToThisButton = new FixedSizeButton("/res/moveToVertexButton.PNG");
		moveWorkingToThisButton.setAction(polygonVertexController);	
		moveWorkingToThisButton.setIcon("/res/moveToVertexButton.PNG");

	}
	
	void createPolygonGUI(Container container)
	{
		Box outerbox = new Box(BoxLayout.X_AXIS);
		outerbox.add(indexTextField);
		outerbox.add(xTextField);
		outerbox.add(yTextField);
		outerbox.add(zTextField);
		outerbox.add(addButton);
		outerbox.add(moveWorkingToThisButton);
		outerbox.add(Box.createHorizontalGlue());
		container.add(outerbox);
	}
	
	void updateFromModel()
	{
		SwingUtilities.invokeLater(
				new Runnable()
				{
					@Override
					public void run() {
						System.out.println("pvv.updateFromModel ran");
						xTextField.setText(Integer.toString(pvm.x));
						yTextField.setText(Integer.toString(pvm.y));
						zTextField.setText(Integer.toString(pvm.z));
						indexTextField.setEditable(true);
						indexTextField.setText(Integer.toString(pvm.index));
						indexTextField.setEditable(false);
					}
				}
		);
	}
}

/** handles action events created by a PolygonVertexGUI */
