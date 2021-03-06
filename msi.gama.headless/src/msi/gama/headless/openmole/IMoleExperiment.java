/*********************************************************************************************
 * 
 *
 * 'IMoleExperiment.java', in plugin 'msi.gama.headless', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.headless.openmole;


public interface IMoleExperiment { 

	public void setup(final String experimentName);
	public void setup(final String experimentName, final long seed);
	
	public long step();
	
	public void setParameter(final String parameterName, final Object value);
	public Object getOutput(final String parameterName);
	public Object getVariableOutput(final String parameterName);
	
	public void dispose();
	
	
}
