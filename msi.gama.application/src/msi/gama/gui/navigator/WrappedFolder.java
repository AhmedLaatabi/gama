/**
 * Created by drogoul, 5 févr. 2015
 * 
 */
package msi.gama.gui.navigator;

import java.util.*;
import msi.gama.gui.swt.*;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.graphics.*;

/**
 * Class ImportFolder.
 * 
 * @author drogoul
 * @since 5 févr. 2015
 * 
 */
public class WrappedFolder extends VirtualContent {

	final Collection<String> fileNames;

	/**
	 * @param root
	 * @param name
	 */
	public WrappedFolder(final IFile root, final Collection<String> object, final String name) {
		super(root, name);
		fileNames = object;
	}

	/**
	 * Method hasChildren()
	 * @see msi.gama.gui.navigator.VirtualContent#hasChildren()
	 */
	@Override
	public boolean hasChildren() {
		return !fileNames.isEmpty();
	}

	@Override
	public Font getFont() {
		return SwtGui.getSmallFont(); // by default
	}

	/**
	 * Method getNavigatorChildren()
	 * @see msi.gama.gui.navigator.VirtualContent#getNavigatorChildren()
	 */
	@Override
	public Object[] getNavigatorChildren() {
		if ( fileNames.isEmpty() ) { return EMPTY; }
		List<WrappedFile> files = new ArrayList();
		IFile file = (IFile) getParent();
		IPath filePath = file.getLocation();
		IPath projectPath = file.getProject().getLocation();
		for ( String s : fileNames ) {
			IPath resPath = new Path(s);
			if ( !resPath.isAbsolute() ) {
				URI fileURI = URI.createFileURI(filePath.toString());
				URI resURI = URI.createURI(resPath.toString()).resolve(fileURI);
				resPath = new Path(resURI.toFileString()).makeRelativeTo(projectPath);
			}
			IFile newFile = file.getProject().getFile(resPath);
			if ( newFile.exists() ) {
				// files.add(newFile);
				WrappedFile proxy = new WrappedFile(this, newFile);
				files.add(proxy);
			}
		}
		return files.toArray();
	}

	/**
	 * Method getImage()
	 * @see msi.gama.gui.navigator.VirtualContent#getImage()
	 */
	@Override
	public Image getImage() {
		return GamaIcons.create("gaml/_" + getName().toLowerCase()).image();
	}

	/**
	 * Method getColor()
	 * @see msi.gama.gui.navigator.VirtualContent#getColor()
	 */
	@Override
	public Color getColor() {
		return IGamaColors.BLACK.color();
	}

	/**
	 * Method isParentOf()
	 * @see msi.gama.gui.navigator.VirtualContent#isParentOf(java.lang.Object)
	 */
	@Override
	public boolean isParentOf(final Object element) {
		if ( !(element instanceof IFile) ) { return false; }
		String path = ((IFile) element).getLocation().makeRelativeTo(((IFile) getParent()).getLocation()).toString();
		return fileNames.contains(path);
	}
}
