package cg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class PolygonDialog extends JDialog implements ActionListener{

	ContentGenerator cg;
	PolygonDialogModel pdm;
	
	JComboBox 	polygonNameComboBox;
	JLabel 		polygonNameLabel = new JLabel("Name ");
	
	JButton 	cancelButton;
	JButton 	okButton;
		
	PolygonDialog(ContentGenerator cg)
	{
		this.cg = cg;
		this.pdm = cg.polygonDialogModel;
		
		polygonNameComboBox = new JComboBox(getPolygonNames());
		
		cancelButton = new JButton("CANCEL");
		cancelButton.addActionListener(
				new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e) {
						PolygonDialog.this.setVisible(false);
						PolygonDialog.this.dispose();			
					}			
				});	
		
		okButton = new JButton("OK");
		
		okButton.addActionListener(
				new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) {
						PolygonDialog.this.handleOK();	
					}			
				});	
		
		this.setSize(400,250);
		this.setModal(true);
		this.generateContent();
		this.validate();
		this.setLocationRelativeTo(cg);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	void handleOK()
	{
		// TO DO
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private void generateContent()
	{
		Box nameBox = new Box(BoxLayout.X_AXIS);
		nameBox.add(polygonNameLabel);
		nameBox.add(polygonNameComboBox);
		
		Box buttonBox = new Box(BoxLayout.X_AXIS);
		buttonBox.add(okButton);
		buttonBox.add(cancelButton);

		Box dialog = new Box(BoxLayout.Y_AXIS);
		dialog.add(nameBox);
		dialog.add(buttonBox);
		this.getContentPane().removeAll();
		this.getContentPane().add(dialog);
	}
	
	private String[] getPolygonNames()
	{
		ContentGeneratorSISI cgsisi = cg.contentGeneratorSISI;
		Map<String,ContentGeneratorPolygon> polygonMap = cgsisi.contentGeneratorPolygonMap;
		Set<String> names = polygonMap.keySet();
		String[] nameArray = names.toArray(new String[0]);
		String[] nameArray2 = {"thisy","thaty","tuthery"};
		return nameArray2;
	}
}
