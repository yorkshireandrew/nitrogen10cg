package cg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;
// import javax.swing.JFrame;

public class TemplateDialog extends JDialog
{
	JTextField t;
	TemplateDialog(final ContentGenerator cg)
	{

		// initialise file text field
		final TemplateDialog td = this;
		t = new JTextField(null,30);
		if (cg.templateFileName[cg.viewDirection] != null)
		{
			t.setText(cg.templateFileName[cg.viewDirection]);
		}
		else
		{
			t.setText("none");
		}
		
		JButton okButton = new JButton("OK OK");
		okButton.addActionListener(
				new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e) {
						
						// record the new templateFileName
						if(t.getText().equals("none"))
						{
							// do nothing
						}
						else
						{
							cg.templateFileName[cg.viewDirection] = t.getText();
						}
						td.setVisible(false);
						td.dispose();			
					}			
				});
		Box dialog = new Box(BoxLayout.Y_AXIS);	
		dialog.add(t);
		dialog.add(okButton);
		this.add(dialog);
		this.setSize(200,100);
		this.setModal(true);
		this.validate();
		this.setLocationRelativeTo(cg);
        
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}
