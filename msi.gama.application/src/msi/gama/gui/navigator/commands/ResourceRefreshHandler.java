package msi.gama.gui.navigator.commands;

import java.util.List;
import msi.gama.gui.navigator.FileMetaDataProvider;
import org.eclipse.core.commands.*;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class ResourceRefreshHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		List files = ((IStructuredSelection) HandlerUtil.getCurrentSelection(event)).toList();
		for ( Object o : files ) {
			if ( o instanceof IFile ) {
				discardMetaData((IFile) o);
			}
		}
		// RefreshHandler.run();
		return null;
	}

	public static void discardMetaData(final IFile file) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				FileMetaDataProvider.getInstance().storeMetadata(file, null);
			}
		});
	}

}
