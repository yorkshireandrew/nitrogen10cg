package cg;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.bombheadgames.nitrogen1.ImmutableVertex;


public class PolygonVertexView {
	JTextField indexTextField;
	JTextField xTextField;
	JTextField yTextField;
	JTextField zTextField;
	FixedSizeButton addButton;
	FixedSizeButton moveWorkingToThisButton;
	
	ContentGenerator cg;
	ImmutableVertex pvm;
	PolygonVertexController polygonVertexController;	
	
	PolygonVertexView(ContentGenerator cg)
	{
		this.cg =cg;
		polygonVertexController = new PolygonVertexController(cg, this);
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
		int index = -1;
		
		if(pvm != null)
		{
			ContentGenerator cgL = cg;
			ContentGeneratorSISI cgSISI = cgL.contentGeneratorSISI;
			List<ImmutableVertex> ivl = cgSISI.immutableVertexList;
			
			index = ivl.indexOf(pvm);
			
			// ok see if we can find an equal vertex 
			if(index == -1)
			{
				pvm = cg.cgc.vertexAlreadyThere((int)pvm.is_x, (int)pvm.is_y, (int)pvm.is_z);
				index = ivl.indexOf(pvm);
			}
			System.out.println("index = " + index);
		}
		
		if(index == -1)
		{
			pvm = null;
			SwingUtilities.invokeLater(
					new Runnable()
					{
						@Override
						public void run() {
							xTextField.setText("");
							yTextField.setText("");
							zTextField.setText("");
							indexTextField.setEditable(true);
							indexTextField.setText("");
							indexTextField.setEditable(false);
						}
					});
			return;			
		}
		
		final int finalIndex = index;
		SwingUtilities.invokeLater(
				new Runnable()
				{
					@Override
					public void run() {
						
							xTextField.setText(Integer.toString((int)pvm.is_x));
							yTextField.setText(Integer.toString((int)pvm.is_y));
							zTextField.setText(Integer.toString((int)pvm.is_z));

							indexTextField.setEditable(true);
							indexTextField.setText(Integer.toString(finalIndex));
							indexTextField.setEditable(false);
						}
		});
	}
}

