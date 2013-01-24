
package cg;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import modified_nitrogen1.PolygonVertexData;
import modified_nitrogen1.SharedImmutableSubItem;

public class PolygonDataDialog  extends JDialog implements ChangeListener, ActionListener{

	private ContentGenerator contentGenerator;
	
	JTextField 			dataNameTextField;
	JSpinner			lengthSpinner;
	
	List<JTextField> 	dataTextFields;
	List<JLabel> 		dataLabels;
	
	JLabel dataNameLabel 	= new JLabel("Name   ");
	JLabel lengthLabel 		= new JLabel("Length ");
	
	JButton cancelButton;
	JButton okButton;
	
	int length = 0;
	
	int[]	existingData;	// used for updating dialog on name changes
	
	PolygonDataDialog(ContentGenerator cg)
	{
		final PolygonDataDialog pdd = this;
		contentGenerator = cg;
			
		dataNameTextField = new JTextField(null,10);
		dataNameTextField.addActionListener(this);
		dataNameTextField.setMaximumSize(dataNameTextField.getPreferredSize());		

		// initialise the lengthSpinner
		lengthSpinner = new JSpinner();
		lengthSpinner.setModel(new SpinnerNumberModel(0,0,100,1));
		lengthSpinner.addChangeListener(this);
		lengthSpinner.setMaximumSize(lengthSpinner.getPreferredSize());
		
		dataTextFields = new ArrayList<JTextField>();
		dataLabels = new ArrayList<JLabel>();
		
		cancelButton = new JButton("CANCEL");
		cancelButton.addActionListener(
				new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e) {
						pdd.setVisible(false);
						pdd.dispose();			
					}			
				});	
		
		okButton = new JButton("OK");
		
		okButton.addActionListener(
				new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) {
						pdd.handleOK();	
					}			
				});	
		
		this.setSize(400,250);
		this.setModal(true);
		this.generateContent();
		this.validate();
		this.setLocationRelativeTo(cg);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}

	
	
	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		
		if(e.getSource() == lengthSpinner)
		{
			System.out.println("lengthSpinner changed");

			int proposedLength = (Integer)lengthSpinner.getModel().getValue();
			
			if(proposedLength < length)
			{
				System.out.println("proposed length "+ proposedLength);
				List<JTextField> 	newDataTextFields = new ArrayList<JTextField>();
				List<JLabel> 		newDataLabels = new ArrayList<JLabel>();
				
				// copy across existing data
				for(int x = 0; x < proposedLength; x++)
				{
					newDataTextFields.add(dataTextFields.get(x));
					newDataLabels.add(dataLabels.get(x));
				}
				
				dataTextFields = newDataTextFields;
				dataLabels = newDataLabels;
				length = proposedLength;
				generateContent();
				this.validate();
				this.repaint();
			}
			
			if(proposedLength > length)
			{
				for(int x = length; x < proposedLength; x++)
				{
					JTextField newTextField = new JTextField(10);
					newTextField.setText("0");
					newTextField.setMaximumSize(newTextField.getPreferredSize());
					dataTextFields.add(newTextField);
					dataLabels.add(new JLabel(Integer.toString(x+1)));
					
				}
				length = proposedLength;
				generateContent();
				this.validate();
				this.repaint();
			}		
		}		
	}
	
	private void generateContent()
	{
		Box nameBox = new Box(BoxLayout.X_AXIS);
		nameBox.add(dataNameLabel);
		nameBox.add(dataNameTextField);
		
		Box lengthBox = new Box(BoxLayout.X_AXIS);
		lengthBox.add(lengthLabel);
		lengthBox.add(lengthSpinner);
		
		Box buttonBox = new Box(BoxLayout.X_AXIS);
		buttonBox.add(okButton);
		buttonBox.add(cancelButton);
		
		Box dataBox = new Box(BoxLayout.Y_AXIS);	
		for( int x = 0; x < length; x++)
		{
			Box dataElementBox = new Box(BoxLayout.X_AXIS);
			dataElementBox.add(dataLabels.get(x));
			dataElementBox.add(dataTextFields.get(x));
			dataBox.add(dataElementBox);
		}	

		Box dialog = new Box(BoxLayout.Y_AXIS);
		dialog.add(nameBox);
		dialog.add(lengthBox);
		dialog.add(dataBox);
		dialog.add(buttonBox);
		this.getContentPane().removeAll();
		this.getContentPane().add(dialog);
	}

	
	private void handleOK() {
		// TODO add code for if OK is pressed
		System.out.println("ok pressed");
		String name = dataNameTextField.getText().trim();
		
		if(!nameIsOK(name))
		{
			return;
		}
		
		int[] data = new int[length];
		try{
			for(int x = 0; x < length; x++)
			{
				data[x] = Integer.parseInt(dataTextFields.get(x).getText().trim());
			}
		}
		catch(NumberFormatException nfe)
		{
			JOptionPane.showMessageDialog(this, "A numeric value is incorrectly formatted", "Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		addPolygonData(name,data);
		this.setVisible(false);
		this.dispose();		
	}
	
	/** checks the name returning OK if it is not present or user wishes to overwrite*/
	private void addPolygonData(String name, int[] data)
	{
		SharedImmutableSubItem sisi = contentGenerator.generatedSISI;
		Map<String,int[]> polygonDataMap = sisi.getPolygonDataMap();	
		polygonDataMap.put(name, data);
	}
	
	
	/** checks the name returning OK if it is not present or user wishes to overwrite*/
	private boolean nameAlreadyExists(String name)
	{
		SharedImmutableSubItem sisi = contentGenerator.generatedSISI;
		Map<String,int[]> polygonDataMap = sisi.getPolygonDataMap();
		
		if(polygonDataMap.containsKey(name))
		{
			existingData = polygonDataMap.get(name);
			return true;
		}		
		return false;
	}
	
	/** checks the name returning OK if it is not present or user wishes to overwrite*/
	private boolean nameIsOK(String name)
	{
		SharedImmutableSubItem sisi = contentGenerator.generatedSISI;
		Map<String,int[]> polygonVertexDataMap = sisi.getPolygonDataMap();
		
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

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == dataNameTextField)
		{
			System.out.println("dataNameTextField changed");
			
			String name = dataNameTextField.getText().trim();
			
			if(nameAlreadyExists(name))
			{
				System.out.println("name already exists");
				length = existingData.length;
				lengthSpinner.getModel().setValue(new Integer(length));
				dataTextFields = new ArrayList<JTextField>();
				dataLabels = new ArrayList<JLabel>();
				for(int x = 0; x < length; x++)
				{
					dataLabels.add(new JLabel(Integer.toString(x+1)));
					
					JTextField newTextField = new JTextField(10);
					newTextField.setText(Integer.toString(existingData[x]));
					newTextField.setMaximumSize(newTextField.getPreferredSize());
					dataTextFields.add(newTextField);	
				}
				generateContent();
				this.validate();
				this.repaint();
			}	
		}
	}
}
