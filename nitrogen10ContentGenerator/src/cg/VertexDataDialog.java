package cg;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import modified_nitrogen1.PolygonVertexData;
import modified_nitrogen1.SharedImmutableSubItem;

public class VertexDataDialog  extends JDialog implements ChangeListener,ActionListener{

	private ContentGenerator contentGenerator;
	
	JTextField dataNameTextField;
	JTextField aux1TextField;
	JTextField aux2TextField;
	JTextField aux3TextField;
	
	JButton dataNameButton 			= new JButton("Name");
	JLabel aux1Label				= new JLabel("Aux 1");
	JLabel aux2Label				= new JLabel("Aux 2");
	JLabel aux3Label				= new JLabel("Aux 3");

	JButton autoFillButton;
	
	// colour chooser stuff
	JTextField	redTextField;
	JTextField	greenTextField;
	JTextField	blueTextField;
	JTextField 	integerColourTextField;
	FixedSizeButton colourChooserButton;
	
	VertexDataDialog(ContentGenerator cg)
	{
		super(cg);
		setTitle("Vertex Data");
		final VertexDataDialog vdd = this;
		contentGenerator = cg;
		
		dataNameButton 			= new JButton("Name");
		dataNameButton.addActionListener(this);
		
			
		dataNameTextField = new JTextField(null,10);
		dataNameTextField.setMaximumSize(dataNameTextField.getPreferredSize());		
		aux1TextField = new JTextField("0",10);
		aux1TextField.setMaximumSize(aux1TextField.getPreferredSize());		
		aux2TextField = new JTextField("0",10);
		aux2TextField.setMaximumSize(aux2TextField.getPreferredSize());		
		aux3TextField = new JTextField("0",10);
		aux3TextField.setMaximumSize(aux3TextField.getPreferredSize());
		
		autoFillButton = new JButton("Use TexMap");
		autoFillButton.addActionListener(this);
		
		JButton cancelButton = new JButton("CANCEL");
		cancelButton.addActionListener(
				new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e) {
						vdd.setVisible(false);
						vdd.dispose();			
					}			
				});	
		
		JButton OKButton = new JButton("OK");
		
		OKButton.addActionListener(
				new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) {
						VertexDataDialog.this.handleOK();	
					}			
				});	
		
		redTextField = new JTextField(4);
		redTextField.setMaximumSize(redTextField.getPreferredSize());
		greenTextField = new JTextField(4);
		greenTextField.setMaximumSize(greenTextField.getPreferredSize());
		blueTextField = new JTextField(4);
		blueTextField.setMaximumSize(blueTextField.getPreferredSize());
		integerColourTextField = new JTextField(10);
		integerColourTextField.setMaximumSize(redTextField.getPreferredSize());
		colourChooserButton = new FixedSizeButton("/res/colourChooserButton.PNG");

		colourChooserButton.addActionListener(
				new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) {
						VertexDataDialog.this.handleColourChooserButton();	
					}			
				});			
		
		
		Box nameBox = new Box(BoxLayout.X_AXIS);
		nameBox.add(dataNameButton);
		nameBox.add(dataNameTextField);
		
		Box aux1Box = new Box(BoxLayout.X_AXIS);
		aux1Box.add(aux1Label);
		aux1Box.add(aux1TextField);
		
		Box aux2Box = new Box(BoxLayout.X_AXIS);	
		aux2Box.add(aux2Label);
		aux2Box.add(aux2TextField);
		
		Box aux3Box = new Box(BoxLayout.X_AXIS);
		aux3Box.add(aux3Label);
		aux3Box.add(aux3TextField);
		
		Box buttonBox = new Box(BoxLayout.X_AXIS);
		buttonBox.add(OKButton);
		buttonBox.add(cancelButton);
		
		Box colourBox = new Box(BoxLayout.X_AXIS);
		colourBox.add(redTextField);
		colourBox.add(greenTextField);
		colourBox.add(blueTextField);
		colourBox.add(integerColourTextField);
		colourBox.add(colourChooserButton);

		Box dialog = new Box(BoxLayout.Y_AXIS);
		dialog.add(nameBox);
		dialog.add(Box.createVerticalGlue());
		dialog.add(aux1Box);
		dialog.add(aux2Box);
		dialog.add(aux3Box);
		dialog.add(Box.createVerticalGlue());
		dialog.add(autoFillButton);
		dialog.add(Box.createVerticalGlue());
		dialog.add(buttonBox);
		dialog.add(Box.createVerticalGlue());
		dialog.add(colourBox);
		
		this.add(dialog);
		this.setSize(250,250);
		this.setModal(true);
		this.validate();
		this.setLocationRelativeTo(cg);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}

	
	
	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		
		
	}
	
	private void handleOK() {
		// TODO add code for if OK is pressed
		System.out.println("ok pressed");
		String name = dataNameTextField.getText().trim();
		
		if(!nameIsOK(name))
		{
			this.setVisible(false);
			this.dispose();					
		}
		
		float aux1, aux2, aux3;
		try{
			aux1 = Float.parseFloat(aux1TextField.getText().trim());
			aux2 = Float.parseFloat(aux2TextField.getText().trim());
			aux3 = Float.parseFloat(aux3TextField.getText().trim());
		}
		catch(NumberFormatException nfe)
		{
			JOptionPane.showMessageDialog(this, "A numeric value is incorrectly formatted", "Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		contentGenerator.cgc.saveSISI();
		PolygonVertexData pvd = new PolygonVertexData(aux1,aux2,aux3);
		addPolygonVertexData(name,pvd);
		this.setVisible(false);
		this.dispose();		
	}
	
	/** checks the name returning OK if it is not present or user wishes to overwrite*/
	private boolean nameIsOK(String name)
	{
		ContentGeneratorSISI cgsisi = contentGenerator.contentGeneratorSISI;
		Map<String,PolygonVertexData> polygonVertexDataMap = cgsisi.polygonVertexDataMap;
		
		if(polygonVertexDataMap.containsKey(name))
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
	
	private void addPolygonVertexData(String name, PolygonVertexData pvd)
	{
		ContentGeneratorSISI cgsisi = contentGenerator.contentGeneratorSISI;
		Map<String,PolygonVertexData> polygonVertexDataMap = cgsisi.polygonVertexDataMap;
		
		polygonVertexDataMap.put(name, pvd);
	}



	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == dataNameButton)
		{
			Map<String,PolygonVertexData> map = contentGenerator.contentGeneratorSISI.polygonVertexDataMap;
			
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
			dataNameTextField.setText(Integer.toString(max));	
		}
		
		if(e.getSource() == autoFillButton)
		{
			if(contentGenerator.textureMapPixels != null)
			{
				aux1TextField.setText(contentGenerator.textureMapX.getText());
				aux2TextField.setText(contentGenerator.textureMapY.getText());
			}
		}
	}
	
	void handleColourChooserButton()
	{
		Color c = JColorChooser.showDialog(this, "Colour Picker", contentGenerator.colourChoosen);

		if(c != null)
		{
			redTextField.setText(Integer.toString(c.getRed()));
			greenTextField.setText(Integer.toString(c.getGreen()));
			blueTextField.setText(Integer.toString(c.getBlue()));
			int v = (c.getRed() << 16) + (c.getGreen() << 8) + c.getBlue();
			integerColourTextField.setText(Integer.toString(v));
			contentGenerator.colourChoosen = c;
		}
	
	}

}
