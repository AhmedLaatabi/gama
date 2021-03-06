/*********************************************************************************************
 * 
 * 
 * 'EventLayer.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.outputs.layers;

import java.util.Collection;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.ConstantExpressionDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.*;

/**
 * Written by marilleau
 */

public class EventLayer extends AbstractLayer {

	@Override
	protected void setPositionAndSize(final IDisplayLayerBox box, final IGraphics g) {
		super.setPositionAndSize(box, g);
	}

	EventListener listener;
	private final String pointArg, listArg;

	public EventLayer(final ILayerStatement layer) {
		super(layer);
		IExpression exp = layer.getFacet(EventLayerStatement.defaultPointArg);
		pointArg = exp == null ? null : exp.literalValue();
		exp = layer.getFacet(EventLayerStatement.defaultListArg);
		listArg = exp == null ? null : exp.literalValue();
	}

	@Override
	public void enableOn(final IDisplaySurface surface) {
		surface.addMouseListener(listener);
	}

	@Override
	public void disableOn(final IDisplaySurface surface) {
		super.disableOn(surface);
		surface.removeMouseListener(listener);
	}

	@Override
	public void firstLaunchOn(final IDisplaySurface surface) {
		super.firstLaunchOn(surface);
		final IExpression eventType = definition.getFacet(IKeyword.NAME);
		final IExpression actionName = definition.getFacet(IKeyword.ACTION);
		IScope scope = surface.getDisplayScope();

		String currentMouseEvent = Cast.asString(scope, eventType.value(scope));
		String currentAction = Cast.asString(scope, actionName.value(scope));

		listener = new EventListener(surface, currentMouseEvent, currentAction);
		surface.addMouseListener(listener);
	}

	@Override
	public void dispose() {
		super.dispose();
		listener.dispose();
	}

	@Override
	public String getType() {
		return "Event layer";
	}

	// We explicitely translate by the origin of the surface
	@Override
	public ILocation getModelCoordinatesFrom(final int xOnScreen, final int yOnScreen, final IDisplaySurface g) {
		return super.getModelCoordinatesFrom(xOnScreen/* - g.getOriginX(), */, yOnScreen /*- g.getOriginY()*/, g);
	}

	private class EventListener implements ILayerMouseListener {

		private final static int MOUSE_PRESS = 0;
		private final static int MOUSE_RELEASED = 1;
		private final static int MOUSE_CLICKED = 2;

		private final int listenedEvent;
		private final IStatement.WithArgs executer;
		private final IDisplaySurface surface;

		public EventListener(final IDisplaySurface display, final String event, final String action) {
			listenedEvent = getListeningEvent(event);
			IAgent a = display.getDisplayScope().getSimulationScope();
			if ( a == null ) {
				a = display.getDisplayScope().getExperiment();
			}
			executer = a.getSpecies().getAction(action);
			surface = display;
		}

		public void dispose() {
			surface.removeMouseListener(this);
		}

		public int getListeningEvent(final String eventTypeName) {
			if ( eventTypeName.equals(IKeyword.MOUSE_DOWN) ) { return MOUSE_PRESS; }
			if ( eventTypeName.equals(IKeyword.MOUSE_UP) ) { return MOUSE_RELEASED; }
			if ( eventTypeName.equals(IKeyword.MOUSE_CLICKED) ) { return MOUSE_CLICKED; }
			return -1;
		}

		@Override
		public void mouseClicked(final int x, final int y, final int button) {
			if ( MOUSE_CLICKED == listenedEvent && button == 1 ) {
				executeEvent(x, y);
			}
		}

		@Override
		public void mouseDown(final int x, final int y, final int button) {
			if ( MOUSE_PRESS == listenedEvent && button == 1 ) {
				executeEvent(x, y);
			}
		}

		@Override
		public void mouseUp(final int x, final int y, final int button) {
			if ( MOUSE_RELEASED == listenedEvent && button == 1 ) {
				executeEvent(x, y);
			}
		}

		private void executeEvent(final int x, final int y) {
			if ( executer == null ) { return; }
			final ILocation pp = getModelCoordinatesFrom(x, y, surface);
			if ( pp.getX() < 0 || pp.getY() < 0 || pp.getX() >= surface.getEnvWidth() ||
				pp.getY() >= surface.getEnvHeight() ) { return; }
			final Arguments args = new Arguments();
			final Collection<IAgent> agentset = surface.selectAgent(x, y);
			if ( pointArg != null ) {
				args.put(pointArg, ConstantExpressionDescription.create(new GamaPoint(pp.getX(), pp.getY())));
			}
			if ( listArg != null ) {
				args.put(listArg, ConstantExpressionDescription.create(agentset));
			}

			executer.setRuntimeArgs(args);
			GAMA.run(new GAMA.InScope.Void() {

				@Override
				public void process(final IScope scope) {
					executer.executeOn(scope);
				}
			});
			if ( surface.getOutput().isPaused() || GAMA.isPaused() ) {
				surface.updateDisplay(true);
			}
		}
	}

	@Override
	protected void privateDrawDisplay(final IScope scope, final IGraphics g) throws GamaRuntimeException {}

	@Override
	public void drawDisplay(final IScope scope, final IGraphics g) throws GamaRuntimeException {
		if ( definition != null ) {
			definition.getBox().compute(scope);
			setPositionAndSize(definition.getBox(), g);
		}
	}

}
