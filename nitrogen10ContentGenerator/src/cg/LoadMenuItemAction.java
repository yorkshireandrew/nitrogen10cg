package cg;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

class LoadMenuItemAction extends AbstractAction
{
	ContentGenerator cg;
	File initialFile = null;
	LoadMenuItemAction(ContentGenerator cg)
	{
		this.cg = cg;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		final JFileChooser fileChooser = new JFileChooser(initialFile);
		fileChooser.setFileFilter(new GeneralFileFilter("ncg"));

		int retval = fileChooser.showOpenDialog(cg);

        if (retval == JFileChooser.APPROVE_OPTION) {
            File loadFile = fileChooser.getSelectedFile();
            
            ObjectInputStream in = null;
            try {
				in = new ObjectInputStream(new FileInputStream(loadFile));
				cg.readFromFile(in);
				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
            finally
            {
            	try {
					in.close();
				} catch (IOException e1) {e1.printStackTrace();}
            }        
        }	
	}
}
