package cg;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.bombheadgames.nitrogen2.ImmutableBackside;
import com.bombheadgames.nitrogen2.ImmutableVertex;
import com.bombheadgames.nitrogen2.NitrogenContext;
import com.bombheadgames.nitrogen2.PolygonVertexData;
import com.bombheadgames.nitrogen2.RendererHelper;
import com.bombheadgames.nitrogen2.TexMap;


public class PolygonDialog extends JDialog implements ActionListener{

	ContentGenerator cg;
	
	JTextField 	polygonNameTextField;
	JButton 	polygonNameButton;
	JButton		fillNameButton;
	
	JButton		deletePolygonButton;
	
	// polygon data UI
	JComboBox 	polygonDataComboBox;
	JLabel 		polygonDataLabel = new JLabel("Polygon Data ");
	
	// polygon vertex data UI
	int					numberOfVertexes;
	JComboBox[] 		polygonVertexDataComboArray;
	ImmutableVertex[]	immutableVertexes; 
	JLabel				polygonVertexDataLabel = new JLabel("Vertex Data ");
	
	// polygon renderer UI
	JComboBox 	polygonRendererComboBox;
	JLabel 		polygonRendererLabel = new JLabel("Polygon Renderer ");
	
	// polygon backside UI
	JCheckBox	isBacksideCulledCheckBox;
	JComboBox 	polygonBacksideComboBox;
	JLabel 		polygonBacksideLabel = new JLabel("Backside ");	
	
	// polygon backside UI
	JComboBox 	textureMapComboBox;
	JLabel 		textureMapLabel = new JLabel("TextureMap ");
	JButton		textureMapAutoFillButton;
	
	// polygon transparent UI
	JCheckBox	isTransparentCheckBox;
	
	JButton 	cancelButton;
	JButton 	okButton;
	
	boolean		isNewPolygon;
		
	PolygonDialog(ContentGenerator cg)
	{
		super(cg);
		setTitle("Polygon Dialog");
		this.cg = cg;
		
		if(cg.workingPolygon == null)
		{
			generatePartialContent();
		}
		else
		{
			generateCompleteContent();
		}
		
		stickItTogether();
		
		this.setSize(450,350);
		this.setModal(true);
		this.validate();
		this.setLocationRelativeTo(cg);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	private void handleOK()
	{
		String name = (String)polygonNameTextField.getText();
		
		// validate the name
		if(isNewPolygon)
		{
			if(!nameIsOK(name))return;
		}
		
		// validate the combo boxes
		if(getString(polygonDataComboBox) == null)
		{
			JOptionPane.showMessageDialog(cg, "Polygon Data incorrect", "Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		for(JComboBox jcb: polygonVertexDataComboArray)
		{
			if(getString(jcb) == null)
			{
				JOptionPane.showMessageDialog(cg, "Vertex Data combo box incorrect (" + (String)(jcb.getEditor().getItem())+")", "Error",JOptionPane.ERROR_MESSAGE);
				return;
			}			
		}
		
		if(getString(polygonRendererComboBox) == null)
		{
			JOptionPane.showMessageDialog(cg, "Polygon renderer incorrect", "Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(getString(polygonBacksideComboBox) == null)
		{
			JOptionPane.showMessageDialog(cg, "Polygon Backside incorrect", "Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if(getString(textureMapComboBox) == null)
		{
			JOptionPane.showMessageDialog(cg, "Texture Map incorrect", "Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		cg.cgc.saveSISI();
		
		ContentGeneratorPolygon newPolygon = new ContentGeneratorPolygon();
		
		newPolygon.numberOfVertexes = numberOfVertexes;
		newPolygon.vertexes = immutableVertexes;	
		newPolygon.polyData_name = getString(polygonDataComboBox);
		
		String[] ivd_names = new String[numberOfVertexes];
		for(int x = 0; x < numberOfVertexes; x++)
		{
			ivd_names[x] = getString(polygonVertexDataComboArray[x]);
		}
		newPolygon.pvd_names = ivd_names;
		newPolygon.rendererTriplet_name = getString(polygonRendererComboBox);
		newPolygon.backside_name = getString(polygonBacksideComboBox);
		newPolygon.textureMap_name = getString(textureMapComboBox);
		newPolygon.isBacksideCulled = isBacksideCulledCheckBox.isSelected();
		newPolygon.isTransparent = isTransparentCheckBox.isSelected();
		
		// add a copy of the model to the polygon map
		cg.contentGeneratorSISI.contentGeneratorPolygonMap.put(name, newPolygon);
		
		// remember the model for next time the dialog gets opened
		cg.polygonDialogModel = newPolygon;
		cg.workingPolygon = name;
		
		// move polygon finish points
		int size = cg.contentGeneratorSISI.contentGeneratorPolygonMap.size();
		cg.contentGeneratorSISI.normalDetailPolyFinish = size;
		cg.contentGeneratorSISI.improvedDetailPolyFinish = size;
		
		// update the bounding radius
		updateBoundingRadius();
		
		// update the display
		cg.cgc.updateGeneratedItemAndEditArea();
		
		PolygonDialog.this.setVisible(false);
		PolygonDialog.this.dispose();	
	}
	
	void updateBoundingRadius()
	{
		ImmutableVertex origin = new ImmutableVertex(0,0,0);
		float present = cg.contentGeneratorSISI.boundingRadius;
		System.out.println("OLD BOUNDING RADIUS" + present);
		
		for(int x = 0; x < numberOfVertexes; x++)
		{
			if(immutableVertexes[x] != null)
			{
				float d = distBetween(immutableVertexes[x], origin);
				if(d > present)present = d;
			}
		}
		
		cg.contentGeneratorSISI.boundingRadius = present;
		System.out.println("NEW BOUNDING RADIUS" + present);
	}
	
	static float distBetween(ImmutableVertex a, ImmutableVertex b)
	{
		float dx = a.is_x - b.is_x;
		float dy = a.is_y - b.is_y;
		float dz = a.is_z - b.is_z;
		return (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == polygonNameButton)
		{
			Map<String,ContentGeneratorPolygon> map = cg.contentGeneratorSISI.contentGeneratorPolygonMap;
			
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
			polygonNameTextField.setText(Integer.toString(max));	
		}	
		
		if(e.getSource() == deletePolygonButton)
		{
			System.out.println("delete polygon pressed");
			handleDeletePolygonEvent();		
		}
		
		if(e.getSource() == textureMapAutoFillButton)
		{
			if(cg.selectedTextureMap == null)return;
			textureMapComboBox.getEditor().setItem(cg.selectedTextureMap);
		}
		
	}

	private String[] getPolygonDataNames()
	{
		ContentGeneratorSISI cgsisi = cg.contentGeneratorSISI;
		Map<String,int[]> polygonDataMap = cgsisi.polygonDataMap;
		Set<String> names = polygonDataMap.keySet();
		String[] nameArray = names.toArray(new String[0]);;
		Arrays.sort(nameArray);
		return nameArray;
	}
	
	private String getPolygonDataValue()
	{
		String retval = cg.polygonDialogModel.polyData_name;
		if(retval == null)return("");
		return retval;
	}
	
	private String[] getPolygonVertexDataNames()
	{
		ContentGeneratorSISI cgsisi = cg.contentGeneratorSISI;
		Map<String,PolygonVertexData> polygonDataMap = cgsisi.polygonVertexDataMap;
		Set<String> names = polygonDataMap.keySet();
		String[] nameArray = names.toArray(new String[0]);;
		Arrays.sort(nameArray);
		return nameArray;
	}
	
	private String getPolygonVertexDataValue(int index)
	{
		if(cg.polygonDialogModel.pvd_names == null )return("");
		String retval = cg.polygonDialogModel.pvd_names[index];
		if(retval == null)return("");
		return retval;
	}
	
	private String[] getPolygonRendererTripletNames()
	{
		return RendererHelper.getRendererTripletNames();
	}
	
	private String getPolygonRendererTripletValue()
	{
		String retval = cg.polygonDialogModel.rendererTriplet_name;
		if(retval == null)return("");
		return retval;
	}
	
	private String[] getPolygonImmutableBacksideNames()
	{
		ContentGeneratorSISI cgsisi = cg.contentGeneratorSISI;
		Map<String,ImmutableBackside> polygonDataMap = cgsisi.immutableBacksideMap;
		Set<String> names = polygonDataMap.keySet();
		String[] nameArray = names.toArray(new String[0]);
		Arrays.sort(nameArray);
		return nameArray;
	}
	
	private boolean getIsBacksideCulledValue()
	{
		boolean retval = cg.polygonDialogModel.isBacksideCulled;
		return retval;
	}
	
	private boolean getIsTransparentValue()
	{
		boolean retval = cg.polygonDialogModel.isTransparent;
		return retval;
	}
	
	private String getPolygonImmutableBacksideValue()
	{
		String retval = cg.polygonDialogModel.backside_name;
		if(retval == null)return("");
		return retval;
	}
	
	private String[] getTextureMapNames()
	{
		ContentGeneratorSISI cgsisi = cg.contentGeneratorSISI;
		Map<String,ContentGeneratorTextureMap> polygonDataMap = cgsisi.textureMapMap;
		Set<String> names = polygonDataMap.keySet();
		String[] nameArray = names.toArray(new String[0]);
		Arrays.sort(nameArray);
		return nameArray;
	}
	
	private String getPolygonTextureMapValue()
	{
		String retval = cg.polygonDialogModel.textureMap_name;
		if(retval == null)return("");
		return retval;
	}
	
	private void fatten(JLabel label)
	{
		label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}
	
	/** checks the name returning OK if it is not present or user wishes to overwrite*/
	private boolean nameIsOK(String name)
	{
		ContentGeneratorSISI cgsisi = cg.contentGeneratorSISI;
		Map<String,ContentGeneratorPolygon> polygonBacksideMap = cgsisi.contentGeneratorPolygonMap;
					
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
	
	/** returns the string in the combo box or null if it does not exist in the dropdown */
	private String getString(JComboBox combo)
	{
		String name = (String)combo.getEditor().getItem();
		name = name.trim();
		ComboBoxModel cbm = combo.getModel();
		int length = cbm.getSize();
		for(int x = 0; x < length; x++)
		{
			if(name.equals((String)cbm.getElementAt(x)))
			{
				return name;
			}
		}
		System.out.println(name + " doesn't exist in dropdown");
		return null;	
	}
	

	
	void handleDeletePolygonEvent()
	{
		String currentNameInCombo = (String)polygonNameTextField.getText();
		ContentGeneratorSISI cgSISI = cg.contentGeneratorSISI;
		Map<String,ContentGeneratorPolygon> cgpMap = cg.contentGeneratorSISI.contentGeneratorPolygonMap;
		if(cgpMap.containsKey(currentNameInCombo))
		{
			// cg.contentGeneratorSISI.contentGeneratorPolygonMap = LinkedHashMapRemover.remove((LinkedHashMap<String,ContentGeneratorPolygon>)cgpMap,currentNameInCombo);
			cgSISI.contentGeneratorPolygonMap.remove(currentNameInCombo);
			
			// adjust polygon start/finish
			cgSISI.normalDetailPolyStart = 0;
			cgSISI.improvedDetailPolyFinish = 0;
			cgSISI.normalDetailPolyFinish = cgSISI.contentGeneratorPolygonMap.size();
			cgSISI.improvedDetailPolyFinish = cgSISI.contentGeneratorPolygonMap.size();	
	
			// update the display
			cg.cgc.updateGeneratedItemAndEditArea();
			
			// we have just deleted the working polygon!
			cg.workingPolygon = null;
			
			// close the dialog. a re-open will cause name combo to be refreshed
			PolygonDialog.this.setVisible(false);
			PolygonDialog.this.dispose();		
		}
	}
	
	
	static void pickPolygon(ContentGenerator cg, int index)
	{
		Set<Entry<String,ContentGeneratorPolygon>> set = cg.contentGeneratorSISI.contentGeneratorPolygonMap.entrySet();
		Iterator <Entry<String,ContentGeneratorPolygon>> it = set.iterator();
		for(int x = 0; x < index; x++)it.next();
		Entry<String,ContentGeneratorPolygon> e = it.next();
		cg.workingPolygon = e.getKey();
		cg.polygonDialogModel = e.getValue();	
	}
	
	
	/** construct the dialog contents using PolygonVertexViews 
	 *  and whatever previous settings the ContentGenerator has
	 */
	private void generatePartialContent()
	{
		// remember this for OK
		isNewPolygon = true;
		
		polygonNameButton = new JButton("Name");
		polygonNameButton.addActionListener(this);
		
		polygonNameTextField = new JTextField(10);
		polygonNameTextField.setEditable(true);
		polygonNameTextField.setMaximumSize(polygonNameTextField.getPreferredSize());

		polygonDataComboBox = new JComboBox(getPolygonDataNames());
		polygonDataComboBox.setEditable(true);
		polygonDataComboBox.setMaximumSize(polygonDataComboBox.getPreferredSize());
		
		int pvvCount = cg.polygonVertexViews.length;
		
		int vertexCount = 0;
		
		// ********** Count the number of unique vertexes *****
		ImmutableVertex last = new ImmutableVertex(0,0,0);
		for(int x = 0; x < pvvCount; x++)
		{
			PolygonVertexView pvv = cg.polygonVertexViews[x];
			
			if(pvv.pvm != null)
			{
				if(pvv.pvm != last)
				{
					vertexCount++;
					last = pvv.pvm;
				}
			}
		}
		// ********** create combo box array *****************
		polygonVertexDataComboArray = new JComboBox[vertexCount];
		immutableVertexes = new ImmutableVertex[vertexCount];
		numberOfVertexes = vertexCount;
		
		// ********** Create vertex data combo boxes  ***
		last = null;
		vertexCount = 0;
		for(int x = 0; x < pvvCount; x++)
		{
			PolygonVertexView pvv = cg.polygonVertexViews[x];
			
			if(pvv.pvm != null)
			{
				if(pvv.pvm != last)
				{
					JComboBox pvcb = new JComboBox(getPolygonVertexDataNames());
					pvcb.setEditable(true);
					pvcb.getEditor().setItem("null");
					pvcb.setMaximumSize(pvcb.getPreferredSize());		
					polygonVertexDataComboArray[vertexCount] = pvcb;
					immutableVertexes[vertexCount] = pvv.pvm;					
					vertexCount++;
					last = pvv.pvm;		
				}
			}
		}
				
		polygonRendererComboBox = new JComboBox(getPolygonRendererTripletNames());
		polygonRendererComboBox.setEditable(true);
		polygonRendererComboBox.setMaximumSize(polygonRendererComboBox.getPreferredSize());

		isBacksideCulledCheckBox = new JCheckBox("Backside culling ");
		
		polygonBacksideComboBox = new JComboBox(getPolygonImmutableBacksideNames());
		polygonBacksideComboBox.setEditable(true);
		polygonBacksideComboBox.setMaximumSize(polygonBacksideComboBox.getPreferredSize());

		textureMapComboBox = new JComboBox(getTextureMapNames());
		textureMapComboBox.setEditable(true);
		textureMapComboBox.setMaximumSize(textureMapComboBox.getPreferredSize());
		
		textureMapAutoFillButton = new JButton ("Use Tex Map");
		textureMapAutoFillButton.addActionListener(this);
		
		isTransparentCheckBox = new JCheckBox("Is transparent ");
		
		// we don't exist yet so cannot be deleted
		deletePolygonButton = null;
		
		// pre-populate if possible
		ContentGeneratorPolygon previous  = cg.polygonDialogModel;
		
		if(previous != null)
		{
			polygonDataComboBox.getEditor().setItem(getPolygonDataValue());
			polygonRendererComboBox.getEditor().setItem(getPolygonRendererTripletValue());
			isBacksideCulledCheckBox.setSelected(getIsBacksideCulledValue());
			polygonBacksideComboBox.getEditor().setItem(getPolygonImmutableBacksideValue());		
			textureMapComboBox.getEditor().setItem(getPolygonTextureMapValue());
			isTransparentCheckBox.setSelected(getIsTransparentValue());
		}
	}
	
	/** construct the dialog contents using PolygonVertexViews 
	 *  and whatever previous settings the ContentGenerator has
	 */
	private void generateCompleteContent()
	{
		// polygon name is not editable
		polygonNameButton = null;
		
		// remember this for OK
		isNewPolygon = false;
		
		polygonNameTextField = new JTextField(10);
		polygonNameTextField.setEditable(true);
		polygonNameTextField.setText(cg.workingPolygon);
		polygonNameTextField.setEditable(false);
		
		polygonNameTextField.setMaximumSize(polygonNameTextField.getPreferredSize());

		polygonDataComboBox = new JComboBox(getPolygonDataNames());
		polygonDataComboBox.setEditable(true);
		polygonDataComboBox.setMaximumSize(polygonDataComboBox.getPreferredSize());
		
		numberOfVertexes = cg.polygonDialogModel.numberOfVertexes;
		polygonVertexDataComboArray = new JComboBox[numberOfVertexes];

		for(int x = 0; x < numberOfVertexes; x++)
		{
			JComboBox pvcb = new JComboBox(getPolygonVertexDataNames());
			pvcb.setEditable(true);
			pvcb.getEditor().setItem(getPolygonVertexDataValue(x));
			pvcb.setMaximumSize(pvcb.getPreferredSize());		
			polygonVertexDataComboArray[x] = pvcb;
		}
		
		immutableVertexes = cg.polygonDialogModel.vertexes;
					
		polygonRendererComboBox = new JComboBox(getPolygonRendererTripletNames());
		polygonRendererComboBox.setEditable(true);
		polygonRendererComboBox.setMaximumSize(polygonRendererComboBox.getPreferredSize());

		isBacksideCulledCheckBox = new JCheckBox("Backside culling ");
		
		polygonBacksideComboBox = new JComboBox(getPolygonImmutableBacksideNames());
		polygonBacksideComboBox.setEditable(true);
		polygonBacksideComboBox.setMaximumSize(polygonBacksideComboBox.getPreferredSize());

		textureMapComboBox = new JComboBox(getTextureMapNames());
		textureMapComboBox.setEditable(true);
		textureMapComboBox.setMaximumSize(textureMapComboBox.getPreferredSize());
		
		textureMapAutoFillButton = new JButton ("Use Tex Map");
		textureMapAutoFillButton.addActionListener(this);
		
		isTransparentCheckBox = new JCheckBox("Is transparent ");
		
		deletePolygonButton = new JButton("DELETE");
		deletePolygonButton.addActionListener(this);
		
		// pre-populate if possible
		ContentGeneratorPolygon previous  = cg.polygonDialogModel;
		
		if(previous != null)
		{
			polygonDataComboBox.getEditor().setItem(getPolygonDataValue());
			polygonRendererComboBox.getEditor().setItem(getPolygonRendererTripletValue());
			isBacksideCulledCheckBox.setSelected(getIsBacksideCulledValue());
			polygonBacksideComboBox.getEditor().setItem(getPolygonImmutableBacksideValue());		
			textureMapComboBox.getEditor().setItem(getPolygonTextureMapValue());
			isTransparentCheckBox.setSelected(getIsTransparentValue());
		}
	}
	
	/* fills the dialog with content */
	private void stickItTogether()
	{
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
		
		Box polygonNameBox = new Box(BoxLayout.X_AXIS);
		polygonNameBox.add(polygonNameTextField);
		polygonNameBox.add(Box.createHorizontalGlue());
		
		Box polygonDataBox = new Box(BoxLayout.X_AXIS);
		polygonDataBox.add(polygonDataComboBox);
		polygonDataBox.add(Box.createHorizontalGlue());
				
		Box polygonRendererBox = new Box(BoxLayout.X_AXIS);
		polygonRendererBox.add(polygonRendererComboBox);
		polygonRendererBox.add(Box.createHorizontalGlue());
		
		Box polygonBacksideBox = new Box(BoxLayout.X_AXIS);
		polygonBacksideBox.add(polygonBacksideComboBox);
		polygonBacksideBox.add(Box.createHorizontalGlue());

		Box textureMapBox = new Box(BoxLayout.X_AXIS);
		textureMapBox.add(textureMapComboBox);
		textureMapBox.add(Box.createHorizontalGlue());
		
		Box isTransparentBox = new Box(BoxLayout.X_AXIS);
		isTransparentBox.add(isTransparentCheckBox);
		
		Box buttonBox = new Box(BoxLayout.X_AXIS);
		buttonBox.add(okButton);
		buttonBox.add(cancelButton);
		buttonBox.add(Box.createHorizontalGlue());
		
		fatten(polygonDataLabel);
		fatten(polygonRendererLabel);
		fatten(polygonBacksideLabel);
		fatten(textureMapLabel);
		
		GridLayout gridLayout = new GridLayout(11 + numberOfVertexes,3);
		setLayout(gridLayout);
		
		if(polygonNameButton != null)
		{
			add(polygonNameButton);
		}
		else
		{
			add(new JLabel(" "));
		}

		add(polygonNameBox);
		add(new JLabel(" "));
		
		add(polygonDataLabel);
		add(polygonDataBox);
		add(new JLabel(" "));
		
		add(new JLabel(" "));
		add(polygonVertexDataLabel);
		add(new JLabel(" "));
		
		System.out.println("nov = " + numberOfVertexes);
		for(int x = 0; x < numberOfVertexes; x++)
		{
			add(new JLabel(" "));
			add(polygonVertexDataComboArray[x]);
			add(new JLabel(" "));
		}
		
		add(new JLabel(" "));
		add(new JLabel(" "));
		add(new JLabel(" "));
		
		add(polygonRendererLabel);
		add(polygonRendererBox);
		add(new JLabel(" "));
		
		add(polygonBacksideLabel);
		add(polygonBacksideBox);
		add(isBacksideCulledCheckBox);
		
		add(textureMapLabel);
		add(textureMapBox);
		add(textureMapAutoFillButton);
		
		add(isTransparentBox);
		add(new JLabel(" "));
		add(new JLabel(" "));
		
		add(new JLabel(" "));
		
		if(deletePolygonButton != null)
		{
			add(deletePolygonButton);
		}
		else
		{
			add(new JLabel(" "));
		}
		add(new JLabel(" "));
		
		add(buttonBox);
		add(new JLabel(" "));
		add(new JLabel(" "));
	}
	
	
	
	

}
