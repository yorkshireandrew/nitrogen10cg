package cg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
// import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.event.ChangeListener;

public class TemplateDialog extends JDialog implements ChangeListener
{
	JTextField fileNameTextField;
	JSpinner leftRightSpinner;
	JSpinner downUpSpinner;
	JTextField scale;
	TemplateDialog(final ContentGenerator cg, final TemplateModel tm)
	{

		// create a (final) reference to this template dialog
		// so it can be passed to the ActionListeners for OK button etc.
		final TemplateDialog td = this;
		
		// create and initialise the fileNameText Field
		fileNameTextField = new JTextField(null,10);
		fileNameTextField.setEnabled(false);
		
		// *** FIX FOR JTextField (which works) ***
		// place JTextField in a JPanel with flow layout
		// to prevent it expanding
		// add fileNameTextFieldPanel into TemplateDialog instead
		//   JPanel fileNameTextFieldPanel = new JPanel();
		//   fileNameTextFieldPanel.add(fileNameTextField);
		
		// *** CLEANER FIX FOR JTextField (which also works) ***
		fileNameTextField.setMaximumSize(fileNameTextField.getPreferredSize());

		// initialise fileNameTextField from the TemplateModel
		if (tm.templateFile != null)
		{
			fileNameTextField.setText(tm.templateFile.getName());
		}
		else
		{
			fileNameTextField.setText("none");
		}
		
		// initialise the leftRightSpinner
		leftRightSpinner = new JSpinner();
		leftRightSpinner.setModel(new SpinnerNumberModel(tm.leftRightOffset,-500,500,1));
		leftRightSpinner.addChangeListener(this);
		
				
				
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(
				new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e) {
						/*
						// record the new templateFileName
						if(t.getText().equals("none"))
						{
							// do nothing
						}
						else
						{
							cg.templateFileName[cg.viewDirection] = t.getText();
						}
						*/
						td.setVisible(false);
						td.dispose();			
					}			
				});
		
		final JFileChooser fileChooser = new JFileChooser(tm.templateFile);
		final JButton fileChooserButton = new JButton("Select File");
		fileChooserButton.addActionListener(
				new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e) {
					int retval = fileChooser.showOpenDialog(td);

			        if (retval == JFileChooser.APPROVE_OPTION) {
			            tm.templateFile = fileChooser.getSelectedFile();
			            fileNameTextField.setText(tm.templateFile.getName());
			            fileNameTextField.repaint();
			            System.out.println("Opening: " + tm.templateFile.getName());
			            // TO DO cause the model to update
			            
			        } else {
			        	System.out.println("Open command cancelled by user.");
			        }

					}	
				}
		);
		Box dialog = new Box(BoxLayout.Y_AXIS);	
		//   dialog.add(tp);
		
		Box fileBox = new Box(BoxLayout.X_AXIS);
		fileBox.add(fileNameTextField);
		fileBox.add(fileChooserButton);		
		dialog.add(fileBox);
		
		dialog.add(leftRightSpinner);
		dialog.add(okButton);
		this.add(dialog);
		this.setSize(400,150);
		this.setModal(true);
		this.validate();
		this.setLocationRelativeTo(cg);
        
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
    public void stateChanged(javax.swing.event.ChangeEvent evt) {

       if (evt.getSource() == leftRightSpinner) {
            this.leftRightSpinnerStateChanged(evt);
        }
    }
    
    public void leftRightSpinnerStateChanged(javax.swing.event.ChangeEvent evt)
    {
    	System.out.println("TO DO - do something the spinner changed");
    }
    
    
}
