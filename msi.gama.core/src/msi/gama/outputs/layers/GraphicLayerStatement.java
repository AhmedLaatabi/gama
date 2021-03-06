/*********************************************************************************************
 * 
 *
 * 'GraphicLayerStatement.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.outputs.layers;

import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.statements.AspectStatement;
import msi.gaml.types.IType;

@symbol(name = "graphics", kind = ISymbolKind.LAYER, with_sequence = true)
@inside(symbols = IKeyword.DISPLAY)
@facets(value = {
	@facet(name = IKeyword.POSITION, type = IType.POINT, optional = true, doc = @doc("position of the upper-left corner of the layer. Note that if coordinates are in [0,1[, the position is relative to the size of the environment (e.g. {0.5,0.5} refers to the middle of the display) whereas it is absolute when coordinates are greter than 1. The position can only be a 3D point {0.5, 0.5, 0.5}, the last coordinate specifying the elevation of the layer.")),
	@facet(name = IKeyword.TRACE, type = { IType.BOOL, IType.INT }, optional = true, doc = @doc("Allows to aggregate the visualization at each timestep on the display. Default is false. If set to an int value, only the last n-th steps will be visualized. If set to true, no limit of timesteps is applied. ")),
	@facet(name = IKeyword.FADING, type = { IType.BOOL }, optional = true, doc = @doc("Used in conjunction with 'trace:', allows to apply a fading effect to the previous traces. Default is false")),
	@facet(name = IKeyword.SIZE, type = IType.POINT, optional = true, doc = @doc("the layer resize factor: {1,1} refers to the original size whereas {0.5,0.5} divides by 2 the height and the width of the layer. In case of a 3D layer, a 3D point can be used (note that {1,1} is equivalent to {1,1,0}, so a resize of a layer containing 3D objects with a 2D points will remove the elevation)")),
	@facet(name = IKeyword.TRANSPARENCY, type = IType.FLOAT, optional = true, doc = @doc("the transparency rate of the agents (between 0 and 1, 1 means no transparency)")),
	@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false, doc = @doc("the identifier of the graphics")),
	@facet(name = IKeyword.REFRESH, type = IType.BOOL, optional = true, doc = @doc("(openGL only) specify whether the display of the species is refreshed. (true by default, usefull in case of agents that do not move)")) }, omissible = IKeyword.NAME)
@doc(value="`graphics` allows the modeler to freely draw shapes/geometries/texts without having to define a species. It works exactly like a species [Aspect161 aspect]: the draw statement can be used in the same way.", usages = {
	@usage(value = "The general syntax is:", examples = {
		@example(value="display my_display {", isExecutable=false),
		@example(value="   graphics \"my new layer\" {", isExecutable=false),
		@example(value="      draw circle(5) at: {10,10} color: #red;", isExecutable=false),
		@example(value="      draw \"test\" at: {10,10} size: 20 color: #black;", isExecutable=false),	
		@example(value="   }", isExecutable=false),
		@example(value="}", isExecutable=false)})},
	see={IKeyword.DISPLAY,IKeyword.AGENTS,IKeyword.CHART,IKeyword.EVENT,"graphics",IKeyword.GRID_POPULATION,IKeyword.IMAGE,IKeyword.OVERLAY,IKeyword.QUADTREE,IKeyword.POPULATION,IKeyword.TEXT})
public class GraphicLayerStatement extends AbstractLayerStatement {

	AspectStatement aspect;
	static int i;

	public GraphicLayerStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		IDescription d = DescriptionFactory.create(IKeyword.ASPECT, desc, IKeyword.NAME, "graphic_aspect" + i++);
		aspect = new AspectStatement(d);
	}

	@Override
	public void setChildren(final List<? extends ISymbol> children) {
		aspect.setChildren(children);
	}

	public AspectStatement getAspect() {
		return aspect;
	}

	@Override
	public short getType() {
		return ILayerStatement.GRAPHICS;
	}

	@Override
	protected boolean _init(final IScope scope) {
		return true;
	}

	@Override
	protected boolean _step(final IScope scope) {
		return true;
	}

}
