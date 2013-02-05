package cg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import modified_nitrogen1.ImmutableBackside;
import modified_nitrogen1.ImmutableVertex;

public class ScaleDialog extends JDialog implements ActionListener{
	ContentGenerator cg;
	
	JTextField scaleTextField;
	JButton okButton;
	JButton cancelButton;
	
	ScaleDialog(ContentGenerator cg)
	{
		super(cg);
		setTitle("Scale");
		
		this.cg = cg;
		
		scaleTextField = new JTextField(10);
		scaleTextField.setText("1");
		
		okButton = new JButton("OK");
		okButton.addActionListener(this);
		cancelButton = new JButton("CANCEL");
		cancelButton.addActionListener(this);
		Box controls = Box.createHorizontalBox();
		controls.add(okButton);
		controls.add(cancelButton);
		
		Box dialog = Box.createVerticalBox();
		dialog.add(new JLabel("scale"));
		dialog.add(scaleTextField);
		dialog.add(controls);
		
		this.add(dialog);
		this.setSize(250,250);
		this.setModal(true);
		this.validate();
		this.setLocationRelativeTo(cg);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == cancelButton)
		{
			this.setVisible(false);
			this.dispose();	
		}
		
		if(e.getSource() == okButton)
		{
			handleOKButton();
		}
		
	}
	
	void handleOKButton()
	{
		float scale;
		try{
			scale = Float.parseFloat(scaleTextField.getText());
		}
		catch(NumberFormatException e)
		{
			JOptionPane.showMessageDialog(cg, "Please enter a number", "Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
			
		ContentGeneratorSISI cgSISI = cg.contentGeneratorSISI;
		
		// Move Vertexes
		List<ImmutableVertex> ivlin = cgSISI.immutableVertexList;
		List<ImmutableVertex> ivlout = new ArrayList<ImmutableVertex>();
		Iterator<ImmutableVertex> ivin_it = ivlin.iterator();
		while(ivin_it.hasNext())
		{
			ImmutableVertex iv_element = ivin_it.next();
			ImmutableVertex iv_element_out = new ImmutableVertex(
					iv_element.is_x * scale,
					iv_element.is_y * scale,
					iv_element.is_z * scale
					);
			ivlout.add(iv_element_out);
		}
		
		// Move CollisionVertexes
		List<ImmutableVertex> ivclin = cgSISI.collisionVertexList;
		List<ImmutableVertex> ivclout = new ArrayList<ImmutableVertex>();
		Iterator<ImmutableVertex> ivcin_it = ivclin.iterator();
		while(ivcin_it.hasNext())
		{
			ImmutableVertex ivc_element = ivcin_it.next();
			ImmutableVertex ivc_element_out = new ImmutableVertex(
					ivc_element.is_x * scale,
					ivc_element.is_y * scale,
					ivc_element.is_z * scale
					);
			ivclout.add(ivc_element_out);
		}
		
		// Move backsides
		Map<String, ImmutableBackside> ibm_in = cgSISI.immutableBacksideMap;
		Map<String, ImmutableBackside> ibm_out = new HashMap<String, ImmutableBackside>();
		
		Set<Entry<String, ImmutableBackside>> ibms_in = ibm_in.entrySet();
		Iterator<Entry<String, ImmutableBackside>> ibms_in_it = ibms_in.iterator();
		
		while(ibms_in_it.hasNext())
		{
			Entry<String, ImmutableBackside> in = ibms_in_it.next();
			ImmutableBackside backsideIn = in.getValue();
			
			ImmutableBackside backsideOut = new ImmutableBackside(
					backsideIn.ix * scale,
					backsideIn.iy * scale,
					backsideIn.iz * scale,
					backsideIn.inx,
					backsideIn.iny,
					backsideIn.inz,
					backsideIn.calculateLighting
					);
			
			ibm_out.put(in.getKey(), backsideOut);	
		}
		
		// calculate new bounding radius
		float boundingRadius = cgSISI.boundingRadius;

		// write back to cgSISI		
		cgSISI.immutableVertexList = ivlout;
		cgSISI.collisionVertexList = ivclout;
		cgSISI.immutableBacksideMap = ibm_out;
		cgSISI.boundingRadius = boundingRadius;
		
		// update the display
		cg.cgc.updateGeneratedItemAndEditArea();
		this.setVisible(false);
		this.dispose();	
	}
}
