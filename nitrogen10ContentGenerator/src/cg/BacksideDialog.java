package cg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import modified_nitrogen1.Backside;
import modified_nitrogen1.ImmutableBackside;
import modified_nitrogen1.ImmutableVertex;
import modified_nitrogen1.SharedImmutableSubItem;

public class BacksideDialog extends JDialog implements ActionListener{
	
	JTextField	nameTextField;	
	JButton 	nameButton; 
	JCheckBox	calculateLightingCheckBox;
	JButton		cancelButton;
	
	private ContentGenerator cg;
	
	BacksideDialog(ContentGenerator cg)
	{
		super(cg);
		setTitle("New Backside");
		this.cg = cg;
		
		nameButton = new JButton("Name");
		nameButton.addActionListener(this);
		
			
		nameTextField = new JTextField(null,10);
		nameTextField.setMaximumSize(nameTextField.getPreferredSize());		

		cancelButton = new JButton("CANCEL");
		cancelButton.addActionListener(
				new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e) {
						BacksideDialog.this.setVisible(false);
						BacksideDialog.this.dispose();			
					}			
				});	
		
		JButton OKButton = new JButton("OK");
		OKButton.addActionListener(
				new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) {
						BacksideDialog.this.handleOK();	
					}			
				});	
		
		calculateLightingCheckBox = new JCheckBox("calculate lighting");
		
		Box nameBox = Box.createHorizontalBox();
		nameBox.add(nameButton);
		nameBox.add(nameTextField);
		
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(OKButton);
		buttonBox.add(cancelButton);
		
		Box calculateLightingBox = Box.createHorizontalBox();
		calculateLightingBox.add(calculateLightingCheckBox);
		
		Box dialog = Box.createVerticalBox();
		dialog.add(nameBox);
		dialog.add(calculateLightingBox);
		dialog.add(buttonBox);
		
		this.add(dialog);
		this.setSize(240,120);
		this.setModal(true);
		this.validate();
		this.setLocationRelativeTo(cg);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);	
	}
	
	void handleOK()
	{
		// a bit of re-validation in case something changed 
		if(!BacksideDialog.polygonVertexesAreOK(cg))
		{
			setVisible(false);
			dispose();				
		}
		
		// validate the name to ensure its not used twice. Unless user is OK with this
		String name = nameTextField.getText().trim();
		if(!nameIsOK(name))return;
		
		// create an ImmutableBackside
		ImmutableBackside ib = generateImmutableBackside(cg);

		// Check its facing the viewer
		Backside testBackside = new Backside();
		testBackside.initializeBackside(ib);
		cg.generatedItem.testBackside = testBackside;
		cg.rootTransform.calculateTestBacksides(cg.nc);
		
		cg.cgc.saveSISI();
		
		if(testBackside.facingViewer())
		{
			System.out.println("Test Backside facing viewer");
			// OK add immutable backside to SISI		
			addImmutableBacksideToSISI(name, ib);
		}
		else
		{
			System.out.println("Test Backside facing away from viewer");
			ImmutableBackside ib2 = ib.flippedImmutableBackside();
			addImmutableBacksideToSISI(name, ib2);
		}
		setVisible(false);
		dispose();		
	}
	
	/** static method to check polygon vertexes are OK to create a backside
	 * . If a backside could not be generated the method provides messages to the user why, then returns false.
	 */
	static boolean polygonVertexesAreOK(ContentGenerator cg)
	{
		final float minDist = 1;
		
		ImmutableVertex pvm1 = cg.polygonVertexViews[0].pvm;
		ImmutableVertex pvm2 = cg.polygonVertexViews[1].pvm;
		ImmutableVertex pvm3 = cg.polygonVertexViews[2].pvm;
		
		if(pvm1 == null)
		{
			JOptionPane.showMessageDialog(cg, "first polygon vertex not set. Unable to create a backside", "Error",JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		if(pvm2 == null)
		{
			JOptionPane.showMessageDialog(cg, "second polygon vertex not set. Unable to create a backside", "Error",JOptionPane.ERROR_MESSAGE);
			return false;
		}

		if(pvm3 == null)
		{
			JOptionPane.showMessageDialog(cg, "third polygon vertex not set. Unable to create a backside", "Error",JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		if(distBetween(pvm1,pvm2) < minDist)
		{
			JOptionPane.showMessageDialog(cg, "distance between first and second polygon vertexes insufficient. Unable to create a backside", "Error",JOptionPane.ERROR_MESSAGE);
			return false;			
		}
		
		if(distBetween(pvm1,pvm3) < minDist)
		{
			JOptionPane.showMessageDialog(cg, "distance between first and third polygon vertexes insufficient. Unable to create a backside", "Error",JOptionPane.ERROR_MESSAGE);
			return false;			
		}
		
		if(distBetween(pvm2,pvm3) < minDist)
		{
			JOptionPane.showMessageDialog(cg, "distance between second and third polygon vertexes insufficient. Unable to create a backside", "Error",JOptionPane.ERROR_MESSAGE);
			return false;			
		}
		
		return true;	
	}
	
	private static int distBetween(ImmutableVertex a, ImmutableVertex b)
	{
		float dx = a.is_x - b.is_x;
		float dy = a.is_y - b.is_y;
		float dz = a.is_z - b.is_z;
		return (int)(dx*dx + dy*dy + dz*dz);
	}
	
	private ImmutableBackside generateImmutableBackside(ContentGenerator cg)
	{
				
		ImmutableVertex pvm1 = cg.polygonVertexViews[0].pvm;
		ImmutableVertex pvm2 = cg.polygonVertexViews[1].pvm;
		ImmutableVertex pvm3 = cg.polygonVertexViews[2].pvm;
		
		float v1x = pvm1.is_x;
		float v1y = pvm1.is_y;
		float v1z = pvm1.is_z;
		
		float v2x = pvm2.is_x;
		float v2y = pvm2.is_y;
		float v2z = pvm2.is_z;
		
		float v3x = pvm3.is_x;
		float v3y = pvm3.is_y;
		float v3z = pvm3.is_z;
		
		float ux = v2x - v1x;
		float uy = v2y - v1y; 
		float uz = v2z - v1z;
		
		float vx = v3x - v1x;
		float vy = v3y - v1y; 
		float vz = v3z - v1z;
		
		// normalise v12
		float uDist = (float)Math.sqrt(ux*ux+uy*uy+uz*uz);
		ux = ux / uDist;
		uy = uy / uDist;
		uz = uz / uDist;

		// normalise v13
		float vDist = (float)Math.sqrt(vx*vx+vy*vy+vz*vz);
		vx = vx / vDist;
		vy = vy / vDist;
		vz = vz / vDist;
		
		// compute vector product which is perpendicular
		float nx = uy * vz - uz * vy;
		float ny = uz * vx - ux * vz;
		float nz = ux * vy - uy * vx;
		
		// normalise the normal
		float nDist = (float)Math.sqrt(nx*nx+ny*ny+nz*nz);
		nx = nx / nDist;
		ny = ny / nDist;
		nz = nz / nDist;
		
		ImmutableBackside retval = new ImmutableBackside(
				v1x,v1y,v1z,
				nx,ny,nz,calculateLightingCheckBox.isSelected());

		return retval;
	}
	
	/** checks the name returning OK if it is not present or user wishes to overwrite*/
	private boolean nameIsOK(String name)
	{
		ContentGeneratorSISI cgsisi = cg.contentGeneratorSISI;
		Map<String,ImmutableBackside> polygonBacksideMap = cgsisi.immutableBacksideMap;
					
		if(polygonBacksideMap.containsKey(name))
		{
			if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
											this, 
											"The name " + name + " is already in use. Overwrite existing data?", 
											"Name already exists"
											,JOptionPane.YES_NO_OPTION)
			)
			{
				return true;
			}
			else
			{
				return false;
			}
		}		
		return true;
	}

	
	void addImmutableBacksideToSISI(String name, ImmutableBackside ib)
	{
		ContentGeneratorSISI cgsisi = cg.contentGeneratorSISI;
		Map<String,ImmutableBackside> immutableBacksideMap = cgsisi.immutableBacksideMap;	
		
		immutableBacksideMap.put(name,ib);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == nameButton)
		{
			Map<String,ImmutableBackside> map = cg.contentGeneratorSISI.immutableBacksideMap;
			
			int max = 0;
			
			if(map == null)return;
			
			Set<String> keyset = map.keySet();
			
			Iterator<String> it = keyset.iterator();
			
			while(it.hasNext())
			{
				String el = it.next();
				int val;
				try{
					val = Integer.parseInt(el);
					if(val > max)max = val;
				}
				catch(NumberFormatException nfe){}				
			}
			max +=1;
			nameTextField.setText(Integer.toString(max));	
		}	
	}
}
