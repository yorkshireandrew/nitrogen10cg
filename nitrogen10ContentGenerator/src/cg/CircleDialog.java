package cg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import com.bombheadgames.nitrogen2.ImmutableVertex;
import com.bombheadgames.nitrogen2.PolygonVertexData;

public class CircleDialog extends JDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	JTextField	nameTextField;	
	JButton 	nameButton; 
	
	JTextField	initialUnitVectorX;
	JTextField	initialUnitVectorY;
	JTextField	initialUnitVectorZ;
	
	JTextField	perpendicularUnitVectorX;
	JTextField	perpendicularUnitVectorY;
	JTextField	perpendicularUnitVectorZ;
	
	JTextField	radius;
	
	JTextField	textureRadius;
	
	JTextField	numberOfPoints;
	
	JCheckBox	createTextureCoordinates;
	
	JButton		cancelButton;
	
	private ContentGenerator cg;
	
	CircleDialog(ContentGenerator cg)
	{
		super(cg);
		setTitle("New Circle");
		this.cg = cg;
		
		nameButton = new JButton("Name");
		nameButton.addActionListener(this);
					
		nameTextField = new JTextField(null,10);
		nameTextField.setMaximumSize(nameTextField.getPreferredSize());
		
		initialUnitVectorX = new JTextField(6);
		initialUnitVectorX.setText("0");
		initialUnitVectorX.setMaximumSize(initialUnitVectorX.getPreferredSize());

		initialUnitVectorY = new JTextField(6);
		initialUnitVectorY.setText("1");
		initialUnitVectorY.setMaximumSize(initialUnitVectorX.getPreferredSize());
		
		initialUnitVectorZ = new JTextField(6);
		initialUnitVectorZ.setText("0");
		initialUnitVectorZ.setMaximumSize(initialUnitVectorX.getPreferredSize());
		
		perpendicularUnitVectorX = new JTextField(6);
		perpendicularUnitVectorX.setText("1");
		perpendicularUnitVectorX.setMaximumSize(perpendicularUnitVectorX.getPreferredSize());

		perpendicularUnitVectorY = new JTextField(6);
		perpendicularUnitVectorY.setText("0");
		perpendicularUnitVectorY.setMaximumSize(perpendicularUnitVectorX.getPreferredSize());
		
		perpendicularUnitVectorZ = new JTextField(6);
		perpendicularUnitVectorZ.setText("0");
		perpendicularUnitVectorZ.setMaximumSize(perpendicularUnitVectorX.getPreferredSize());

		radius = new JTextField(6);
		radius.setText("10");
		radius.setMaximumSize(perpendicularUnitVectorX.getPreferredSize());

		numberOfPoints = new JTextField(6);
		numberOfPoints.setText("8");
		numberOfPoints.setMaximumSize(perpendicularUnitVectorX.getPreferredSize());
		
		textureRadius = new JTextField(6);
		textureRadius.setText("10");
		textureRadius.setMaximumSize(perpendicularUnitVectorX.getPreferredSize());
		
		createTextureCoordinates = new JCheckBox("create texture coordinates");
		
		cancelButton = new JButton("CANCEL");
		cancelButton.addActionListener(
				new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e) {
						CircleDialog.this.setVisible(false);
						CircleDialog.this.dispose();			
					}			
				});	
		
		JButton OKButton = new JButton("OK");
		OKButton.addActionListener(
				new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) {
						CircleDialog.this.handleOK();	
					}			
				});	
		
		
		Box nameBox = Box.createHorizontalBox();
		nameBox.add(nameButton);
		nameBox.add(nameTextField);
		
		Box initialUnitVectorLabel = Box.createHorizontalBox();
		initialUnitVectorLabel.add(new JLabel("Initial Unit Vector"));
		initialUnitVectorLabel.add(Box.createHorizontalGlue());
		
		Box initialUnitVector =  Box.createHorizontalBox();
		initialUnitVector.add(initialUnitVectorX);		
		initialUnitVector.add(initialUnitVectorY);		
		initialUnitVector.add(initialUnitVectorZ);	
		initialUnitVector.add(Box.createHorizontalGlue());
		
		Box perpendicularUnitVectorLabel = Box.createHorizontalBox();
		perpendicularUnitVectorLabel.add(new JLabel("perpendicular Unit Vector"));
		perpendicularUnitVectorLabel.add(Box.createHorizontalGlue());
		
		Box perpendicularUnitVector =  Box.createHorizontalBox();
		perpendicularUnitVector.add(perpendicularUnitVectorX);		
		perpendicularUnitVector.add(perpendicularUnitVectorY);		
		perpendicularUnitVector.add(perpendicularUnitVectorZ);
		perpendicularUnitVector.add(Box.createHorizontalGlue());
		
		Box radiusLabelBox = Box.createHorizontalBox();
		radiusLabelBox.add(new JLabel("Radius"));
		radiusLabelBox.add(Box.createHorizontalGlue());		
		
		Box radiusBox = Box.createHorizontalBox();
		radiusBox.add(radius);
		radiusBox.add(Box.createHorizontalGlue());
		
		Box numberOfPointsLabelBox = Box.createHorizontalBox();
		numberOfPointsLabelBox.add(new JLabel("Number of points"));
		numberOfPointsLabelBox.add(Box.createHorizontalGlue());		

		Box numberOfPointsBox = Box.createHorizontalBox();
		numberOfPointsBox.add(numberOfPoints);
		numberOfPointsBox.add(Box.createHorizontalGlue());		
		
		Box textureRadiusLabelBox = Box.createHorizontalBox();
		textureRadiusLabelBox.add(new JLabel("Texture Radius"));
		textureRadiusLabelBox.add(Box.createHorizontalGlue());		
		
		Box textureRadiusBox = Box.createHorizontalBox();
		textureRadiusBox.add(textureRadius);
		textureRadiusBox.add(Box.createHorizontalGlue());
		
		Box createTextureCoordinatesBox = Box.createHorizontalBox();
		createTextureCoordinatesBox.add(createTextureCoordinates);
		createTextureCoordinatesBox.add(Box.createHorizontalGlue());
		
	
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(OKButton);
		buttonBox.add(cancelButton);
		
		
		Box dialog = Box.createVerticalBox();
		dialog.add(nameBox);
		dialog.add(initialUnitVectorLabel);
		dialog.add(initialUnitVector);
		dialog.add(perpendicularUnitVectorLabel);
		dialog.add(perpendicularUnitVector);
		dialog.add(radiusLabelBox);
		dialog.add(radiusBox);
		dialog.add(numberOfPointsLabelBox);
		dialog.add(numberOfPointsBox);
		dialog.add(Box.createVerticalGlue());
		dialog.add(textureRadiusLabelBox);
		dialog.add(textureRadiusBox);	
		dialog.add(createTextureCoordinatesBox);
		dialog.add(buttonBox);
		
		this.add(dialog);
		this.setSize(200,320);
		this.setModal(true);
		this.validate();
		this.setLocationRelativeTo(cg);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);	
	}
	
	void handleOK()
	{	
		String name = nameTextField.getText();
		float centreX, centreY, centreZ;
		float initX, initY, initZ;
		float perpX, perpY, perpZ;
		float r;
		int pointNumber;
		float tr;
		boolean createTexture;
		float tx = 0;
		float ty = 0;
		
		
		try{
			initX = Float.parseFloat(initialUnitVectorX.getText());
			initY = Float.parseFloat(initialUnitVectorY.getText());
			initZ = Float.parseFloat(initialUnitVectorZ.getText());

			perpX = Float.parseFloat(perpendicularUnitVectorX.getText());
			perpY = Float.parseFloat(perpendicularUnitVectorY.getText());
			perpZ = Float.parseFloat(perpendicularUnitVectorZ.getText());
			
			r = Float.parseFloat(radius.getText());
			
			pointNumber = Integer.parseInt(numberOfPoints.getText());
			
			tr = Float.parseFloat(textureRadius.getText());
			
			createTexture = createTextureCoordinates.isSelected();
		}
		catch(NumberFormatException nfe)
		{
			JOptionPane.showMessageDialog(cg, "Something is not a number!", "Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		centreX = cg.workingVertexModel.x;
		centreY = cg.workingVertexModel.y;
		centreZ = cg.workingVertexModel.z;

		if(createTexture)
		{
			try{
				tr = Float.parseFloat(textureRadius.getText());
				tx = Float.parseFloat(cg.textureMapX.getText());
				ty = Float.parseFloat(cg.textureMapY.getText());
			}
			catch(NumberFormatException nfe)
			{
				JOptionPane.showMessageDialog(cg, "Texture coordinates... Something is not a number!", "Error",JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		
		if(pointNumber < 3)
		{
			JOptionPane.showMessageDialog(cg, "At least three points are needed to be a circle", "Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		cg.cgc.saveSISI();
		
		float k = 2 * ((float)Math.PI) /  pointNumber;
		for(int x  = 0; x < pointNumber; x++)
		{
			ImmutableVertex newVertex = new ImmutableVertex(
					(float) (int) (centreX + r * initX * Math.cos(k * x) + r * perpX * Math.sin(k * x)),
					(float) (int) (centreY + r * initY * Math.cos(k * x) + r * perpY * Math.sin(k * x)),
					(float) (int) (centreZ + r * initZ * Math.cos(k * x) + r * perpZ * Math.sin(k * x))
			);
			cg.contentGeneratorSISI.immutableVertexList.add(newVertex);
		}
		if(createTexture)
		{
			for(int x  = 0; x < pointNumber; x++)
			{
				PolygonVertexData newVertexData = new PolygonVertexData(
						(float) (int) (tx + tr * Math.sin(k * x)),
						(float) (int) (ty - tr * Math.cos(k * x)),
						0
				);
				
				String n = name + "_" + Integer.toString(x+1);
				cg.contentGeneratorSISI.polygonVertexDataMap.put(n, newVertexData);
			}
		}
		
		cg.cgc.updateGeneratedItemAndEditArea();
		setVisible(false);
		dispose();	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == nameButton)
		{
			Map<String,PolygonVertexData> map = cg.contentGeneratorSISI.polygonVertexDataMap;
			
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
