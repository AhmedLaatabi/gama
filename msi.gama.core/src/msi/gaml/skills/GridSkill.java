/*********************************************************************************************
 * 
 *
 * 'GridSkill.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.skills;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.topology.grid.IGrid;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.util.GamaColor;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 24 juin 2010
 * 
 * @todo Description
 * 
 */
@vars({
	@var(name = IKeyword.COLOR, type = IType.COLOR),
	// @var(name = IKeyword.AGENTS, type = IType.LIST, of = IType.AGENT, doc = @doc(deprecated =
	// "This variable is deprecated for grid agents. Use agents_inside(cell) or agents_overlapping(cell) instead")),
	@var(name = IKeyword.GRID_VALUE, type = IType.FLOAT),
	@var(name = IKeyword.GRID_X, type = IType.INT, constant = true),
	@var(name = IKeyword.GRID_Y, type = IType.INT, constant = true) })
@skill(name = GridSkill.SKILL_NAME)
public class GridSkill extends Skill {

	public static interface IGridAgent {

		public GamaColor getColor();

		public void setColor(final GamaColor color);

		public int getX();

		public int getY();

		public double getValue();

		public void setValue(final double d);
	}

	public static final String SKILL_NAME = "grid";

	protected final IGrid getGrid(final IAgent agent) {
		return (IGrid) agent.getPopulation().getTopology().getPlaces();
	}

	@getter("grid_x")
	public final int getX(final IAgent agent) {
		return ((IGridAgent) agent).getX();
	}

	@getter(value = "grid_value", initializer = true)
	public final double getValue(final IAgent agent) {
		return ((IGridAgent) agent).getValue();
	}

	@getter("grid_y")
	public final int getY(final IAgent agent) {
		return ((IGridAgent) agent).getY();
	}

	@setter("grid_x")
	public final void setX(final IAgent agent, final Integer i) {}

	@setter("grid_value")
	public final void setValue(final IAgent agent, final Double d) {
		((IGridAgent) agent).setValue(d);
	}

	@setter("grid_y")
	public final void setY(final IAgent agent, final Integer i) {}

	@getter(value = "color", initializer = true)
	public GamaColor getColor(final IAgent agent) {
		return ((IGridAgent) agent).getColor();
	}

	@setter("color")
	public void setColor(final IAgent agent, final GamaColor color) {
		((IGridAgent) agent).setColor(color);
	}

}
