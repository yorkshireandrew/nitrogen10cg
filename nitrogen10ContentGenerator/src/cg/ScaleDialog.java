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

import com.bombheadgames.nitrogen2.ImmutableBackside;
import com.bombheadgames.nitrogen2.ImmutableCollisionVertex;
import com.bombheadgames.nitrogen2.ImmutableVertex;


public class ScaleDialog extends JDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	
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
		
		cg.cgc.saveSISI();
		
		ContentGenerator cgL = cg;
						
		ContentGeneratorSISI cgSISI = cgL.contentGeneratorSISI;
		
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
		updateImmutableVertexReferences(ivlin,ivlout);
		
		// Move CollisionVertexes
		List<ImmutableCollisionVertex> ivclin = cgSISI.collisionVertexList;
		List<ImmutableCollisionVertex> ivclout = new ArrayList<ImmutableCollisionVertex>();
		Iterator<ImmutableCollisionVertex> ivcin_it = ivclin.iterator();
		while(ivcin_it.hasNext())
		{
			ImmutableCollisionVertex ivc_element = ivcin_it.next();
			ImmutableCollisionVertex ivc_element_out = new ImmutableCollisionVertex(
					ivc_element.is_x * scale,
					ivc_element.is_y * scale,
					ivc_element.is_z * scale,
					ivc_element.radius * scale
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
		
		// get distances
		float boundingRadius = cgSISI.boundingRadius;	
		float billboardOrientationDist = cgSISI.billboardOrientationDist;
		float nearRendererDist = cgSISI.nearRendererDist;
		float farRendererDist = cgSISI.farRendererDist;
		float hlpBreakingDist = cgSISI.hlpBreakingDist;	
		float farPlane = cgSISI.farPlane;
		
		// write back to cgSISI		
		cgSISI.immutableVertexList = ivlout;
		cgSISI.collisionVertexList = ivclout;
		cgSISI.immutableBacksideMap = ibm_out;
		
		// write back to cgSISI scaled distances
		cgSISI.boundingRadius = boundingRadius * scale;
		cgSISI.billboardOrientationDist = billboardOrientationDist * scale;
		cgSISI.nearRendererDist = nearRendererDist * scale;
		cgSISI.farRendererDist = farRendererDist * scale;
		cgSISI.hlpBreakingDist = hlpBreakingDist * scale;	
		cgSISI.farPlane = farPlane;
		
		// update the display
		cgL.workingVertexModel.pickedVertex = null;
		cgL.cgc.updateGeneratedItemAndEditArea();
		
		this.setVisible(false);
		this.dispose();	
	}
	

	
	void updateImmutableVertexReferences(List<ImmutableVertex> oldVertexes, List<ImmutableVertex> newVertexes)
	{
		ContentGeneratorSISI cgSISI = cg.contentGeneratorSISI;
		Map<String,ContentGeneratorPolygon> cgcgp_in = cgSISI.contentGeneratorPolygonMap;
		Map<String,ContentGeneratorPolygon> newPolygons = new HashMap<String,ContentGeneratorPolygon>();
		
		Set<Entry<String,ContentGeneratorPolygon>> s = cgcgp_in.entrySet();
		Iterator<Entry<String,ContentGeneratorPolygon>> cgcgp_in_it = s.iterator();
		
		while(cgcgp_in_it.hasNext())
		{
			Entry<String,ContentGeneratorPolygon> element = cgcgp_in_it.next();
			ContentGeneratorPolygon cgp_in = element.getValue();
			
			int numberOfVertexes = cgp_in.numberOfVertexes;
			
			int[] indexes = new int[numberOfVertexes];
			
			for(int x = 0; x < numberOfVertexes; x++)
			{
				indexes[x] = oldVertexes.indexOf(cgp_in.vertexes[x]);
			}
		
			ContentGeneratorPolygon cgp_out = new ContentGeneratorPolygon(cgp_in);
			
			for(int x = 0; x < numberOfVertexes; x++)
			{
				cgp_out.vertexes[x] = newVertexes.get(indexes[x]);
			}
			
			newPolygons.put(element.getKey(), cgp_out);		
		}
		
		cgSISI.contentGeneratorPolygonMap = newPolygons;
	}
}
