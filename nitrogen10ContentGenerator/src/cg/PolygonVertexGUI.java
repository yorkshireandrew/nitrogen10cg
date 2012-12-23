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

public class PolygonVertexGUI {
	JTextField indexTextField;
	JTextField xTextField;
	JTextField yTextField;
	JTextField zTextField;
	FixedSizeButton addButton;
	FixedSizeButton moveWorkingToThisButton;
	
	PolygonVertexGUI(ContentGenerator cg, int index)
	{
		indexTextField = new JTextField();
		indexTextField.setColumns(4);
		indexTextField.setText("none");
		indexTextField.setAction(new PolygonVertexAction(cg, index, "index"));
		indexTextField.setEnabled(false);
		indexTextField.setMaximumSize(new Dimension(40,20));

		xTextField = new JTextField();
		xTextField.setColumns(4);
		xTextField.setText("0");
		xTextField.setAction(new PolygonVertexAction(cg, index, "x"));
		xTextField.setMaximumSize(new Dimension(40,20));
		
		yTextField = new JTextField();
		yTextField.setColumns(4);
		yTextField.setText("0");
		yTextField.setAction(new PolygonVertexAction(cg, index, "y"));
		yTextField.setMaximumSize(new Dimension(40,20));

		zTextField = new JTextField();
		zTextField.setColumns(4);
		zTextField.setText("0");
		zTextField.setAction(new PolygonVertexAction(cg, index, "z"));
		zTextField.setMaximumSize(new Dimension(40,20));
		
		addButton = new FixedSizeButton("/res/fullRenderButton.PNG");
		addButton.setAction(new PolygonAddButtonAction(cg,this));
		addButton.setIcon("/res/addButton.PNG");
		moveWorkingToThisButton = new FixedSizeButton("/res/moveToVertexButton.PNG");
		moveWorkingToThisButton.setAction(new PolygonMoveToVertexAction(cg,this));	
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

class PolygonVertexAction extends AbstractAction
{
	PolygonVertexAction(ContentGenerator cg, int index, String type)
	{
		super();
		this.putValue("ContentGenerator", cg);
		this.putValue("index", new Integer(index));
		this.putValue("type", type);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("actionPerformed called on [" + this.getValue("index") + "] " + this.getValue("type"));			
	}	
}

class PolygonAddButtonAction extends AbstractAction
{
	PolygonAddButtonAction(ContentGenerator cg, PolygonVertexGUI pvgui)
	{
		super();
		this.putValue("ContentGenerator", cg);
		this.putValue("pvgui", pvgui);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("actionPerformed called on add button");			
	}	
}

class PolygonMoveToVertexAction extends AbstractAction
{
	PolygonMoveToVertexAction(ContentGenerator cg, PolygonVertexGUI pvgui)
	{
		super();
		this.putValue("ContentGenerator", cg);
		this.putValue("pvgui", pvgui);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("actionPerformed called on moveToVertexAction");			
	}	
}
