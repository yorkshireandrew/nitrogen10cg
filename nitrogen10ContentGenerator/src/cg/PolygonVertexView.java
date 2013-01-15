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

public class PolygonVertexView {
	JTextField indexTextField;
	JTextField xTextField;
	JTextField yTextField;
	JTextField zTextField;
	FixedSizeButton addButton;
	FixedSizeButton moveWorkingToThisButton;
	
	ContentGenerator cg;
	PolygonVertexModel pvgm;
	PolygonVertexController pvgh;	
	
	PolygonVertexView(ContentGenerator cg, PolygonVertexModel pvgm)
	{
		pvgh = new PolygonVertexController(cg,this,pvgm);
		
		indexTextField = new JTextField();
		indexTextField.setColumns(4);
		indexTextField.setText("none");
		indexTextField.setAction(pvgh);
		indexTextField.setEnabled(false);
		indexTextField.setMaximumSize(new Dimension(40,20));

		xTextField = new JTextField();
		xTextField.setColumns(4);
		xTextField.setText("0");
		xTextField.setAction(pvgh);
		xTextField.setMaximumSize(new Dimension(40,20));
		
		yTextField = new JTextField();
		yTextField.setColumns(4);
		yTextField.setText("0");
		yTextField.setAction(pvgh);
		yTextField.setMaximumSize(new Dimension(40,20));

		zTextField = new JTextField();
		zTextField.setColumns(4);
		zTextField.setText("0");
		zTextField.setAction(pvgh);
		zTextField.setMaximumSize(new Dimension(40,20));
		
		addButton = new FixedSizeButton("/res/fullRenderButton.PNG");
		addButton.setAction(pvgh);
		addButton.setIcon("/res/addButton.PNG");
		
		moveWorkingToThisButton = new FixedSizeButton("/res/moveToVertexButton.PNG");
		moveWorkingToThisButton.setAction(pvgh);	
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
		container.add(outerbox);
	}
}

/** handles action events created by a PolygonVertexGUI */
