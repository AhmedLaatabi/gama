/*********************************************************************************************
 * 
 * 
 * 'PopulationEditor.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.parameters;

import java.util.Collection;
import msi.gama.common.interfaces.EditorListener;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.outputs.InspectDisplayOutput;
import msi.gama.util.IContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

public class PopulationEditor extends AbstractEditor<IContainer> {

	Text populationDisplayer;

	PopulationEditor(final IParameter param) {
		super(param);
	}

	PopulationEditor(final IAgent agent, final IParameter param) {
		this(agent, param, null);
	}

	PopulationEditor(final IAgent agent, final IParameter param, final EditorListener l) {
		super(agent, param, l);
	}

	PopulationEditor(final Composite parent, final String title, final Object value,
		final EditorListener<java.util.List> whenModified) {
		// Convenience method
		super(new InputParameter(title, value), whenModified);
		this.createComposite(parent);
	}

	@Override
	public Control createCustomParameterControl(final Composite compo) {
		populationDisplayer = new Text(compo, SWT.READ_ONLY);
		populationDisplayer.setEnabled(false);
		final GridData data = new GridData(GridData.FILL, GridData.CENTER, true, false);
		populationDisplayer.setLayoutData(data);
		return populationDisplayer;
	}

	@Override
	protected void displayParameterValue() {
		internalModification = true;
		String s =
			currentValue instanceof IPopulation ? ((IPopulation) currentValue).getName()
				: currentValue instanceof IContainer ? currentValue.serialize(true) : "nil";
		populationDisplayer.setText(s);
		populationDisplayer.setToolTipText(s);
		internalModification = false;
	}

	@Override
	public Control getEditorControl() {
		return populationDisplayer;
	}

	@Override
	protected void applyBrowse() {
		if ( currentValue instanceof Collection ) {
			InspectDisplayOutput.browse((Collection) currentValue);
		}
	}

	@Override
	protected int[] getToolItems() {
		return new int[] { BROWSE };
	}

}
