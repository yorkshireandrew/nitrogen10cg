package cg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

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

public class BacksideDialog extends JDialog {
	
	JTextField	nameTextField;	
	JLabel 		nameLabel = new JLabel("name ");
	JCheckBox	calculateLightingCheckBox;
	JButton		cancelButton;
	
	private ContentGenerator cg;
	
	BacksideDialog(ContentGenerator cg)
	{
		this.cg = cg;
			
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
		nameBox.add(nameLabel);
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
		this.setSize(400,250);
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
		
		// validate the name to ensure its not used twice
		String name = nameTextField.getText().trim();
		if(!nameIsOk(name))
		{
			JOptionPane.showMessageDialog(this, "The name " + name + " has already been used. Please choose another name.", "Error",JOptionPane.ERROR_MESSAGE);
			return;		
		}
		
		// create an ImmutableBackside
		ImmutableBackside ib = generateImmutableBackside(cg);

		// Check its facing the viewer
		Backside testBackside = new Backside();
		testBackside.initializeBackside(ib);
		cg.generatedItem.testBackside = testBackside;
		cg.rootTransform.calculateTestBacksides(cg.nc);
		
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
		PolygonVertexModel[] polygonVertexModels = cg.polygonVertexModels;
		if(polygonVertexModels.length < 3)
		{
			JOptionPane.showMessageDialog(cg, "insufficient polygon vertexes!", "Error",JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		PolygonVertexModel pvm1 = polygonVertexModels[0];
		PolygonVertexModel pvm2 = polygonVertexModels[1];
		PolygonVertexModel pvm3 = polygonVertexModels[2];
		
		if(pvm1.index == -1)
		{
			JOptionPane.showMessageDialog(cg, "first polygon vertex not set. Unable to create a backside", "Error",JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		if(pvm2.index == -1)
		{
			JOptionPane.showMessageDialog(cg, "second polygon vertex not set. Unable to create a backside", "Error",JOptionPane.ERROR_MESSAGE);
			return false;
		}

		if(pvm3.index == -1)
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
	
	private static int distBetween(PolygonVertexModel a, PolygonVertexModel b)
	{
		int dx = a.x - b.x;
		int dy = a.y - b.y;
		int dz = a.z - b.z;
		return (dx*dx + dy*dy + dz*dz);
	}
	
	private ImmutableBackside generateImmutableBackside(ContentGenerator cg)
	{
		PolygonVertexModel[] polygonVertexModels = cg.polygonVertexModels;		
		PolygonVertexModel pvm1 = polygonVertexModels[0];
		PolygonVertexModel pvm2 = polygonVertexModels[1];
		PolygonVertexModel pvm3 = polygonVertexModels[2];
		
		float v1x = (float)pvm1.x;
		float v1y = (float)pvm1.y;
		float v1z = (float)pvm1.z;
		
		float v2x = (float)pvm2.x;
		float v2y = (float)pvm2.y;
		float v2z = (float)pvm2.z;
		
		float v3x = (float)pvm3.x;
		float v3y = (float)pvm3.y;
		float v3z = (float)pvm3.z;
		
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
	
	boolean nameIsOk(String name)
	{
		// get the generatedSISI
		SharedImmutableSubItem gs = cg.generatedSISI;
		
		if(gs.getImmutableBacksideNameList().contains(name))
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	void addImmutableBacksideToSISI(String name, ImmutableBackside ib)
	{
		// get the generatedSISI
		SharedImmutableSubItem gs = cg.generatedSISI;
		ImmutableBackside[] immutableBacksides = gs.getImmutableBacksides();
		int ibl = immutableBacksides.length;
		
		// update it adding the supplied ImmutableBackside
		ImmutableBackside[] newImmutableBacksideArray = Arrays.copyOf(immutableBacksides, (ibl+1));
		newImmutableBacksideArray[ibl]= ib;
		gs.setImmutableBacksides(newImmutableBacksideArray);
		
		// add the supplied name to the list
		gs.getImmutableBacksideNameList().add(name);
	}
}
