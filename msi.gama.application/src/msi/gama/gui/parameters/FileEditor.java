/*********************************************************************************************
 * 
 * 
 * 'FileEditor.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.parameters;

import msi.gama.common.interfaces.EditorListener;
import msi.gama.gui.swt.IGamaColors;
import msi.gama.gui.swt.controls.FlatButton;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import msi.gama.util.file.IGamaFile;
import msi.gaml.operators.Files;
import msi.gaml.types.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;

public class FileEditor extends AbstractEditor<IGamaFile> {

	private FlatButton textBox;

	FileEditor(final IParameter param) {
		super(param);
	}

	FileEditor(final IAgent agent, final IParameter param) {
		this(agent, param, null);
	}

	FileEditor(final IAgent agent, final IParameter param, final EditorListener l) {
		super(agent, param, l);
	}

	FileEditor(final Composite parent, final String title, final String value, final EditorListener<String> whenModified) {
		// Convenience method
		super(new InputParameter(title, value), whenModified);
		this.createComposite(parent);
	}

	@Override
	public Control createCustomParameterControl(final Composite comp) {
		textBox = FlatButton.menu(comp, IGamaColors.NEUTRAL, "").light().small();
		textBox.setText("No file");
		textBox.addSelectionListener(this);
		// GridData d = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		// textBox.setLayoutData(d);
		return textBox;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.NULL);
		IGamaFile file = currentValue;
		dialog.setFileName(file.getPath());
		dialog.setText("Choose a file for parameter '" + param.getTitle() + "'");
		final String path = dialog.open();
		if ( path != null ) {
			file = GAMA.run(new InScope<IGamaFile>() {

				@Override
				public IGamaFile run(final IScope scope) {
					return Files.from(scope, path);
				}

			});
			modifyAndDisplayValue(file);
		}
	}

	@Override
	protected void displayParameterValue() {
		internalModification = true;
		if ( currentValue == null ) {
			textBox.setText("No file");
		} else {
			IGamaFile file = currentValue;
			textBox.setToolTipText(file.getPath());
			textBox.setText(file.getPath());
		}
		internalModification = false;
	}

	@Override
	public Control getEditorControl() {
		return textBox;
	}

	@Override
	public IType getExpectedType() {
		return Types.FILE;
	}

	@Override
	protected void applyEdit() {
		widgetSelected(null);
	}

	@Override
	protected int[] getToolItems() {
		return new int[] { EDIT, REVERT };
	}

}
