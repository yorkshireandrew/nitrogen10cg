package cg;

import java.io.File;

public class TemplateModel {

	File templateFile;
	int leftRightOffset;
	int downUpOffset;
	boolean over;	// set true if the template is shown over the content
	int intensity;
	int oppositeSideIntensity;
	int scale = 1000;
	TemplateModel oppositeSideTemplateModel;
	
	TemplateModel(ContentGenerator cg)
	{
		// TO DO
	}
	
	// default constructor
	TemplateModel()
	{
	}
	
	/** Copy constructor. Creates a copy of the state of the passed in prototype for cancel buttons etc */
	static TemplateModel copy(TemplateModel prototype)
	{
		TemplateModel retval = new TemplateModel();
		retval.templateFile = prototype.templateFile;
		retval.leftRightOffset = prototype.leftRightOffset;
		retval.downUpOffset = prototype.downUpOffset;
		retval.over = prototype.over;	
		retval.intensity = prototype.intensity;
		retval.oppositeSideIntensity = prototype.oppositeSideIntensity;
		retval.scale = prototype.scale;
		return retval;
	}
	
	/** Sets the state of this TemplateModel using a 
	previously savedState */
	void restore(TemplateModel savedState)
	{
		this.templateFile = savedState.templateFile;
		this.leftRightOffset = savedState.leftRightOffset;
		this.downUpOffset = savedState.downUpOffset;
		this.over = savedState.over;	
		this.intensity = savedState.intensity;
		this.oppositeSideIntensity = savedState.oppositeSideIntensity;
		this.scale = savedState.scale;
	}
}
