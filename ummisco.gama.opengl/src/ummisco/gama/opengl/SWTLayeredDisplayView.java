/**
 * Created by drogoul, 25 mars 2015
 *
 */
package ummisco.gama.opengl;

import java.io.IOException;
import java.net.*;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.displays.awt.DisplaySurfaceMenu;
import msi.gama.gui.views.LayeredDisplayView;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.Composite;
import com.jogamp.common.util.JarUtil;

/**
 * Class OpenGLLayeredDisplayView.
 *
 * @author drogoul
 * @since 25 mars 2015
 *
 */
public class SWTLayeredDisplayView extends LayeredDisplayView implements /* ControlListener, */MouseMoveListener {

	SWTOpenGLDisplaySurface surface;

	static {
		// Necessary to load the native libraries correctly (see
		// http://forum.jogamp.org/Return-of-the-quot-java-lang-UnsatisfiedLinkError-Can-t-load-library-System-Library-Frameworks-glueg-td4034549.html)
		JarUtil.setResolver(new JarUtil.Resolver() {

			@Override
			public URL resolve(final URL url) {
				try {
					URL urlUnescaped = FileLocator.resolve(url);
					URL urlEscaped = new URI(urlUnescaped.getProtocol(), urlUnescaped.getPath(), null).toURL();
					return urlEscaped;
				} catch (IOException ioexception) {
					return url;
				} catch (URISyntaxException urisyntaxexception) {
					return url;
				}
			}
		});
	}

	public static String ID = "msi.gama.application.view.OpenGLDisplayView";

	@Override
	protected Composite createSurfaceComposite() {
		surface = new SWTOpenGLDisplaySurface(parent, getOutput());
		surfaceComposite = surface.renderer.getCanvas();
		surfaceComposite.addMouseMoveListener(this);
		surface.setSWTMenuManager(new DisplaySurfaceMenu(surface, surfaceComposite, this));
		surface.outputReloaded();
		return surfaceComposite;
	}

	@Override
	public void setFocus() {
		surfaceComposite.setFocus();
	}

	@Override
	public void close() {

		GuiUtils.asyncRun(new Runnable() {

			@Override
			public void run() {
				try {
					if ( surface != null ) {
						surface.dispose();
					}
					getSite().getPage().hideView(SWTLayeredDisplayView.this);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	public void mouseMove(final MouseEvent e) {
		GuiUtils.asyncRun(new Runnable() {

			@Override
			public void run() {
				overlay.update();
			}
		});
	}

	@Override
	public IDisplaySurface getDisplaySurface() {
		return surface;
	}

}
