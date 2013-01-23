package cg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import modified_nitrogen1.PolygonVertexData;
import modified_nitrogen1.SharedImmutableSubItem;

public class VertexDataDialog  extends JDialog implements ChangeListener{

	private ContentGenerator contentGenerator;
	
	JTextField dataNameTextField;
	JTextField aux1TextField;
	JTextField aux2TextField;
	JTextField aux3TextField;
	
	JLabel dataNameLabel 			= new JLabel("Name ");
	JLabel aux1Label				= new JLabel("Aux 1");
	JLabel aux2Label				= new JLabel("Aux 2");
	JLabel aux3Label				= new JLabel("Aux 3");

	
	VertexDataDialog(ContentGenerator cg)
	{
		final VertexDataDialog vdd = this;
		contentGenerator = cg;
			
		dataNameTextField = new JTextField(null,10);
		dataNameTextField.setMaximumSize(dataNameTextField.getPreferredSize());		
		aux1TextField = new JTextField("0",10);
		aux1TextField.setMaximumSize(aux1TextField.getPreferredSize());		
		aux2TextField = new JTextField("0",10);
		aux2TextField.setMaximumSize(aux2TextField.getPreferredSize());		
		aux3TextField = new JTextField("0",10);
		aux3TextField.setMaximumSize(aux3TextField.getPreferredSize());
		
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
						vdd.handleOK();	
					}			
				});	
				
		Box nameBox = new Box(BoxLayout.X_AXIS);
		nameBox.add(dataNameLabel);
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

		Box dialog = new Box(BoxLayout.Y_AXIS);
		dialog.add(nameBox);
		dialog.add(aux1Box);
		dialog.add(aux2Box);
		dialog.add(aux3Box);
		dialog.add(buttonBox);
		
		this.add(dialog);
		this.setSize(400,250);
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
		
		PolygonVertexData pvd = new PolygonVertexData(aux1,aux2,aux3);
		addPolygonVertexData(name,pvd);
		this.setVisible(false);
		this.dispose();		
	}
	
	/** checks the name returning OK if it is not present or user wishes to overwrite*/
	private boolean nameIsOK(String name)
	{
		SharedImmutableSubItem sisi = contentGenerator.generatedSISI;
		Map<String,PolygonVertexData> polygonVertexDataMap = sisi.getPolygonVertexDataMap();
		
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
	
	/** checks the name returning OK if it is not present or user wishes to overwrite*/
	private void addPolygonVertexData(String name, PolygonVertexData pvd)
	{
		SharedImmutableSubItem sisi = contentGenerator.generatedSISI;
		Map<String,PolygonVertexData> polygonVertexDataMap = sisi.getPolygonVertexDataMap();
		
		polygonVertexDataMap.put(name, pvd);
	}

}
