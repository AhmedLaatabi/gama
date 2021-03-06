/*********************************************************************************************
 *
 *
 * 'SpeciesDescription.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.descriptions;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.*;
import java.lang.reflect.Modifier;
import java.util.*;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.metamodel.agent.*;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix.GridPopulation.MinimalGridAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.architecture.reflex.AbstractArchitecture;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.SymbolSerializer.SpeciesSerializer;
import msi.gaml.expressions.*;
import msi.gaml.factories.*;
import msi.gaml.skills.*;
import msi.gaml.statements.Facets;
import msi.gaml.types.IType;
import org.eclipse.emf.ecore.EObject;

public class SpeciesDescription extends TypeDescription {

	private Map<String, StatementDescription> behaviors;
	private Map<String, StatementDescription> aspects;
	private Map<String, SpeciesDescription> microSpecies;
	protected final Map<Class, ISkill> skills = new THashMap();
	protected IArchitecture control;
	private IAgentConstructor agentConstructor;

	public SpeciesDescription(final String keyword, final IDescription macroDesc, final ChildrenProvider cp,
		final EObject source, final Facets facets) {
		this(keyword, null, macroDesc, null, cp, source, facets);
	}

	public SpeciesDescription(final String keyword, final Class clazz, final IDescription macroDesc,
		final IDescription parent, final ChildrenProvider cp, final EObject source, final Facets facets) {
		super(keyword, clazz, macroDesc, parent, cp, source, facets);
		setSkills(facets.get(SKILLS), Collections.EMPTY_SET);
	}

	/**
	 * This constructor is only called to build built-in species. The parent is passed directly as there is no
	 * ModelFactory to provide it
	 */
	public SpeciesDescription(final String name, final Class clazz, final IDescription superDesc,
		final SpeciesDescription parent, final IAgentConstructor helper, final Set<String> skills2, final Facets ff) {
		super(SPECIES, clazz, superDesc, null, ChildrenProvider.NONE, null, new Facets(KEYWORD, SPECIES, NAME, name));
		if ( ff.containsKey(CONTROL) ) {
			facets.putAsLabel(CONTROL, ff.get(CONTROL).toString());
		}
		setSkills(ff.get(SKILLS), skills2);
		setParent(parent);
		setAgentConstructor(helper);
	}

	@Override
	public SymbolSerializer createSerializer() {
		return new SpeciesSerializer();
	}

	@Override
	public void dispose() {
		if ( isBuiltIn() ) { return; }
		if ( behaviors != null ) {
			behaviors.clear();
		}
		if ( aspects != null ) {
			aspects.clear();
		}
		skills.clear();
		if ( control != null ) {
			control.dispose();
		}
		// macroSpecies = null;

		if ( microSpecies != null ) {
			microSpecies.clear();
		}
		// if ( inits != null ) {
		// inits.clear();
		// }
		super.dispose();
		// isDisposed = true;
	}

	protected void setSkills(final IExpressionDescription userDefinedSkills, final Set<String> builtInSkills) {
		final Set<String> skillNames = new TLinkedHashSet();
		/* We try to add the control architecture if any is defined */
		if ( facets.containsKey(CONTROL) ) {
			String control = facets.getLabel(CONTROL);
			if ( control == null ) {
				warning("This control  does not belong to the list of known agent controls (" +
					AbstractGamlAdditions.ARCHITECTURES + ")", IGamlIssue.WRONG_CONTEXT, CONTROL);
			} else {
				ISkill skill = AbstractGamlAdditions.getSkillInstanceFor(control);
				if ( skill == null ) {
					warning("The control " + control + " does not belong to the list of known agent controls (" +
						AbstractGamlAdditions.ARCHITECTURES + ")", IGamlIssue.WRONG_CONTEXT, CONTROL);
				}
			}
			skillNames.add(control);
		}
		/* We add the keyword as a possible skill (used for 'grid' species) */
		skillNames.add(getKeyword());
		/* We add the user defined skills (i.e. as in 'species a skills: [s1, s2...]') */
		if ( userDefinedSkills != null ) {
			skillNames.addAll(userDefinedSkills.getStrings(this, true));
		}
		/*
		 * We add the skills that are defined in Java, either using @species(value='a', skills=
		 * {s1,s2}), or @skill(value="s1", attach_to="a")
		 */
		skillNames.addAll(builtInSkills);

		/* We then create the list of classes from this list of names */
		for ( final String skillName : skillNames ) {
			final Class skillClass = AbstractGamlAdditions.getSkillClasses().get(skillName);
			if ( skillClass != null ) {
				addSkill(skillClass);
			}
		}

	}

	public String getControlName() {
		String controlName = facets.getLabel(CONTROL);
		// if the "control" is not explicitly declared then inherit it from the parent species.
		// Takes care of invalid species (see Issue 711)
		if ( controlName == null ) {
			if ( parent != null && parent != this ) {
				controlName = getParent().getControlName();
			} else {
				controlName = REFLEX;
			}
		}
		return controlName;
	}

	public ISkill getSkillFor(final Class clazz) {
		final ISkill skill = skills.get(clazz);
		if ( skill == null && clazz != null ) {
			for ( final Map.Entry<Class, ISkill> entry : skills.entrySet() ) {
				if ( clazz.isAssignableFrom(entry.getKey()) ) { return entry.getValue(); }
			}
		}
		// We go and try to find the skill in the parent
		if ( skill == null && parent != null && parent != this ) { return getParent().getSkillFor(clazz); }
		return skill;
	}

	private void buildSharedSkills() {
		// Necessary in order to prevent concurrentModificationExceptions
		final Set<Class> classes = new THashSet(skills.keySet());
		for ( final Class c : classes ) {
			Class clazz = c;
			if ( Skill.class.isAssignableFrom(clazz) ) {
				if ( IArchitecture.class.isAssignableFrom(clazz) && control != null ) {
					while (clazz != AbstractArchitecture.class) {
						skills.put(clazz, control);
						clazz = clazz.getSuperclass();
					}
				} else {
					skills.put(clazz, AbstractGamlAdditions.getSkillInstanceFor(c));
				}
			} else {
				skills.put(clazz, null);
			}
		}
	}

	public String getParentName() {
		return facets.getLabel(PARENT);
	}

	@Override
	public IExpression getVarExpr(final String n) {
		IExpression result = super.getVarExpr(n);
		if ( result == null ) {
			IDescription desc = getBehavior(n);
			if ( desc != null ) {
				result = new DenotedActionExpression(desc);
			}
			desc = getAspect(n);
			if ( desc != null ) {
				result = new DenotedActionExpression(desc);
			}
		}
		return result;
	}

	@Override
	public IDescription addChild(final IDescription child) {
		final IDescription desc = super.addChild(child);
		if ( desc == null ) { return null; }
		if ( desc instanceof StatementDescription ) {
			final StatementDescription statement = (StatementDescription) desc;
			final String kw = desc.getKeyword();
			if ( PRIMITIVE.equals(kw) || ACTION.equals(kw) ) {
				addAction(this, statement);
			} else if ( ASPECT.equals(kw) ) {
				addAspect(statement);
			} else {
				addBehavior(statement);
			}
		} else if ( desc instanceof VariableDescription ) {
			addOwnVariable((VariableDescription) desc);
		} else if ( desc instanceof SpeciesDescription ) {
			final ModelDescription md = getModelDescription();
			if ( md != null ) {
				md.addSpeciesType((TypeDescription) desc);
			}
			getMicroSpecies().put(desc.getName(), (SpeciesDescription) desc);
		}
		return desc;
	}

	private void addBehavior(final StatementDescription r) {
		final String behaviorName = r.getName();
		if ( behaviors == null ) {
			behaviors = new TOrderedHashMap<String, StatementDescription>();
		}
		final StatementDescription existing = behaviors.get(behaviorName);
		if ( existing != null ) {
			if ( existing.getKeyword().equals(r.getKeyword()) ) {
				duplicateInfo(r, existing);
				// children.remove(existing);
			}
		}
		behaviors.put(behaviorName, r);
	}

	public boolean hasBehavior(final String a) {
		return behaviors != null && behaviors.containsKey(a);

		// || parent != null &&
		// ((SpeciesDescription) parent).hasBehavior(a);
	}

	public StatementDescription getBehavior(final String s) {
		return behaviors != null ? behaviors.get(s) : null;
	}

	private void addAspect(final StatementDescription ce) {
		String aspectName = ce.getName();
		if ( aspectName == null ) {
			aspectName = DEFAULT;
			ce.getFacets().putAsLabel(NAME, aspectName);
		}
		if ( !aspectName.equals(DEFAULT) && hasAspect(aspectName) ) {
			duplicateInfo(ce, getAspect(aspectName));
		}
		if ( aspects == null ) {
			aspects = new TOrderedHashMap<String, StatementDescription>();
		}
		aspects.put(aspectName, ce);
	}

	public StatementDescription getAspect(final String aName) {
		// if ( aspects != null && aspects.containsKey(aName) ) { return aspects.get(aName); }
		// return parent == null ? null : ((SpeciesDescription) parent).getAspect(aName);
		return aspects == null ? null : aspects.get(aName);
	}

	public Collection<String> getAspectNames() {
		// Set<String> names = new HashSet();
		// if ( aspects != null ) {
		// names.addAll(aspects.keySet());
		// }
		// if ( parent != null ) {
		// names.addAll(((SpeciesDescription) parent).getAspectNames());
		// }
		// return names;
		return aspects == null ? Collections.EMPTY_LIST : aspects.keySet();
	}

	public Collection<StatementDescription> getAspects() {
		return aspects == null ? Collections.EMPTY_LIST : aspects.values();
	}

	public IArchitecture getControl() {
		return control;
	}

	/**
	 * Returns all the direct&in-direct micro-species of this species.
	 *
	 * @return
	 */
	public List<SpeciesDescription> getAllMicroSpecies() {
		final List<SpeciesDescription> retVal = new ArrayList<SpeciesDescription>();
		if ( hasMicroSpecies() ) {
			retVal.addAll(getMicroSpecies().values());

			for ( final SpeciesDescription micro : getMicroSpecies().values() ) {
				retVal.addAll(micro.getAllMicroSpecies());
			}
		}
		return retVal;
	}

	@Override
	public boolean hasAspect(final String a) {
		return aspects != null && aspects.containsKey(a);

		// || parent != null && parent.hasAspect(a);
	}

	@Override
	public SpeciesDescription getSpeciesContext() {
		return this;
	}

	public List<SpeciesDescription> getSelfAndParentMicroSpecies() {
		final ArrayList<SpeciesDescription> retVal = new ArrayList<SpeciesDescription>();
		if ( hasMicroSpecies() ) {
			retVal.addAll(getMicroSpecies().values());
		}
		// Takes care of invalid species (see Issue 711)
		if ( parent != null && parent != this ) {
			retVal.addAll(getParent().getSelfAndParentMicroSpecies());
		}

		return retVal;
	}

	public SpeciesDescription getMicroSpecies(final String name) {
		if ( hasMicroSpecies() ) {
			final SpeciesDescription retVal = microSpecies.get(name);
			if ( retVal != null ) { return retVal; }
		}
		// Takes care of invalid species (see Issue 711)
		if ( parent != null && parent != this ) { return getParent().getMicroSpecies(name); }
		return null;
	}

	@Override
	public String toString() {
		return "Description of " + getName();
	}

	public IAgentConstructor getAgentConstructor() {
		if ( agentConstructor == null && parent != null ) {
			agentConstructor = ((SpeciesDescription) parent).getAgentConstructor();
		}
		return agentConstructor;
	}

	protected void setAgentConstructor(final IAgentConstructor agentConstructor) {
		this.agentConstructor = agentConstructor;
	}

	public void addSkill(final Class c) {
		if ( c != null && ISkill.class.isAssignableFrom(c) && !c.isInterface() &&
			!Modifier.isAbstract(c.getModifiers()) ) {
			skills.put(c, null);
		}
	}

	@Override
	public Set<Class> getSkillClasses() {
		return skills.keySet();
	}

	public SpeciesDescription getMacroSpecies() {
		final IDescription d = getEnclosingDescription();
		if ( d instanceof SpeciesDescription ) { return (SpeciesDescription) d; }
		return null;
	}

	@Override
	public SpeciesDescription getParent() {
		return (SpeciesDescription) super.getParent();
	}

	@Override
	public void inheritFromParent() {
		final SpeciesDescription parent = getParent();
		// Takes care of invalid species (see Issue 711)
		// built-in parents are not considered as their actions/variables are normally already copied as java additions
		if ( parent != null && parent != this && !parent.isBuiltIn() ) {
			if ( !parent.getJavaBase().isAssignableFrom(getJavaBase()) ) {
				error("Species " + getName() + " Java base class (" + getJavaBase().getSimpleName() +
					") is not a subclass of its parent species " + parent.getName() + " base class (" +
					parent.getJavaBase().getSimpleName() + ")", IGamlIssue.GENERAL);
				// }
			}
			// GuiUtils.debug(" **** " + getName() + " inherits from " + parent.getName());
			inheritMicroSpecies(parent);
			inheritBehaviors(parent);
			inheritAspects(parent);
			super.inheritFromParent();
		}

	}

	// FIXME HACK !
	private void inheritMicroSpecies(final SpeciesDescription parent) {
		// Takes care of invalid species (see Issue 711)
		if ( parent == null || parent == this ) { return; }
		for ( final Map.Entry<String, SpeciesDescription> entry : parent.getMicroSpecies().entrySet() ) {
			if ( !getMicroSpecies().containsKey(entry.getKey()) ) {
				getMicroSpecies().put(entry.getKey(), entry.getValue());
				// children.add(entry.getValue());
			}
		}
	}

	private void inheritAspects(final SpeciesDescription parent) {
		// Takes care of invalid species (see Issue 711)
		if ( parent != null && parent != this && parent.aspects != null ) {
			for ( final String aName : parent.aspects.keySet() ) {
				if ( !hasAspect(aName) ) {
					addChild(parent.getAspect(aName).copy(this));
				}
			}
		}
	}

	private void inheritBehaviors(final SpeciesDescription parent) {
		// We only copy the behaviors that are not redefined in this species
		if ( parent.behaviors != null ) {
			for ( final StatementDescription b : parent.behaviors.values() ) {
				if ( !hasBehavior(b.getName()) ) {
					// Copy done here
					addChild(b.copy(this));
				}
			}
		}
	}

	/**
	 * @return
	 */
	public List<SpeciesDescription> getSelfWithParents() {
		// returns a reversed list of parents + self
		final List<SpeciesDescription> result = new ArrayList<SpeciesDescription>();
		SpeciesDescription currentSpeciesDesc = this;
		while (currentSpeciesDesc != null) {
			result.add(0, currentSpeciesDesc);
			SpeciesDescription parent = currentSpeciesDesc.getParent();
			// Takes care of invalid species (see Issue 711)
			if ( parent == currentSpeciesDesc ) {
				break;
			} else {
				currentSpeciesDesc = parent;
			}
		}
		return result;
	}

	public boolean isGrid() {
		return getKeyword().equals(GRID);
	}

	@Override
	public String getTitle() {
		return getKeyword() + " " + getName();
	}

	@Override
	public String getDocumentation() {
		StringBuilder sb = new StringBuilder(200);
		final String parentName = getParent() == null ? "nil" : getParent().getName();
		final String hostName = getMacroSpecies() == null ? null : getMacroSpecies().getName();
		sb.append("<b>Subspecies of:</b> ").append(parentName).append("<br>");
		if ( hostName != null ) {
			sb.append("<b>Microspecies of:</b> ").append(hostName).append("<br>");
		}
		sb.append("<b>Skills:</b> ").append(getSkillsNames()).append("<br>");
		sb.append("<b>Attributes:</b> ").append(getVarNames()).append("<br>");
		sb.append("<b>Actions: </b>").append(getActionNames()).append("<br>");
		sb.append("<br/>");
		sb.append(getMeta().getDocumentation());
		return sb.toString();
	}

	public Set<String> getSkillsNames() {
		final Set<String> names = new TLinkedHashSet();
		for ( final ISkill skill : skills.values() ) {
			if ( skill != null ) {
				names.add(AbstractGamlAdditions.getSkillNameFor(skill.getClass()));
			}
		}
		// Takes care of invalid species (see Issue 711)
		if ( getParent() != null && getParent() != this ) {
			names.addAll(getParent().getSkillsNames());
		}
		return names;
	}

	/**
	 * Returns a list of SpeciesDescription that can be the parent of this species.
	 * A species can be a sub-species of its "peer" species ("peer" species are species sharing the
	 * same direct macro-species).
	 *
	 * @return
	 */
	public List<SpeciesDescription> getPotentialParentSpecies() {
		final List<SpeciesDescription> retVal = getVisibleSpecies();
		retVal.removeAll(this.getSelfAndParentMicroSpecies());
		retVal.remove(this);

		return retVal;
	}

	/**
	 * Sorts the micro-species.
	 * Parent micro-species are ahead of the list followed by sub micro-species.
	 *
	 * @return
	 */
	private List<SpeciesDescription> sortedMicroSpecies() {
		if ( !hasMicroSpecies() ) { return Collections.EMPTY_LIST; }
		final Collection<SpeciesDescription> allMicroSpecies = getMicroSpecies().values();
		// validate and set the parent parent of each micro-species
		for ( final SpeciesDescription microSpec : allMicroSpecies ) {
			microSpec.verifyParent();
		}

		final List<SpeciesDescription> sortedMicroSpecs = new ArrayList<SpeciesDescription>();
		for ( final SpeciesDescription microSpec : allMicroSpecies ) {
			final List<SpeciesDescription> parents = microSpec.getSelfWithParents();

			for ( final SpeciesDescription p : parents ) {
				if ( !sortedMicroSpecs.contains(p) && allMicroSpecies.contains(p) ) {
					sortedMicroSpecs.add(p);
				}
			}
		}

		return sortedMicroSpecs;
	}

	/**
	 * Returns a list of visible species from this species.
	 *
	 * A species can see the following species:
	 * 1. Its direct micro-species.
	 * 2. Its peer species.
	 * 3. Its direct&in-direct macro-species and their peers.
	 *
	 * @return
	 */
	public List<SpeciesDescription> getVisibleSpecies() {
		final List<SpeciesDescription> retVal = new ArrayList<SpeciesDescription>();

		SpeciesDescription currentSpec = this;
		while (currentSpec != null) {
			retVal.addAll(currentSpec.getSelfAndParentMicroSpecies());

			// "world" species
			if ( currentSpec.getMacroSpecies() == null ) {
				retVal.add(currentSpec);
			}

			currentSpec = currentSpec.getMacroSpecies();
		}

		return retVal;
	}

	/**
	 * Returns a visible species from the view point of this species.
	 * If the visible species list contains a species with the specified name.
	 *
	 * @param speciesName
	 */
	public TypeDescription getVisibleSpecies(final String speciesName) {
		for ( final TypeDescription visibleSpec : getVisibleSpecies() ) {
			if ( visibleSpec.getName().equals(speciesName) ) { return visibleSpec; }
		}

		return null;
	}

	/**
	 * Verifies if the specified species can be a parent of this species.
	 *
	 * A species can be parent of other if the following conditions are hold
	 * 1. A parent species is visible to the sub-species.
	 * 2. A species can' be a sub-species of itself.
	 * 3. 2 species can't be parent of each other.
	 * 5. A species can't be a sub-species of its direct/in-direct micro-species.
	 * 6. A species and its direct/indirect micro/macro-species can't share one/some direct/indirect
	 * parent-species having micro-species.
	 * 7. The inheritance between species from different branches doesn't form a "circular"
	 * inheritance.
	 *
	 * @param parentName the name of the potential parent
	 * @throws GamlException if the species with the specified name can not be a parent of this
	 *             species.
	 */
	protected void verifyParent() {
		if ( parent == null ) { return; }
		if ( this == parent ) {
			error(getName() + " species can't be a sub-species of itself", IGamlIssue.GENERAL);
			return;
		}
		final List<SpeciesDescription> candidates = this.getPotentialParentSpecies();
		TypeDescription potentialParent = null;
		if ( candidates.contains(parent) ) {
			potentialParent = parent;
		}
		if ( potentialParent == null ) {

			// List<String> availableSpecies =new GamaList<String>(Types.STRING);
			// for ( TypeDescription p : candidates ) {
			// availableSpecies.add(p.getName());
			// }
			// availableSpecies.remove(availableSpecies.size() - 1);

			error(parent.getName() + " can't be a parent species of " + this.getName() + " species.",
				IGamlIssue.WRONG_PARENT, PARENT);

			return;
		}

		final List<SpeciesDescription> parentsOfParent = ((SpeciesDescription) potentialParent).getSelfWithParents();
		if ( parentsOfParent.contains(this) ) {
			final String error =
				this.getName() + " species and " + potentialParent.getName() +
					" species can't be sub-species of each other.";
			// potentialParent.error(error);
			error(error);
			return;
		}

		// TODO Commented because the test does not make sense
		// if ( this.getAllMicroSpecies().contains(parentsOfParent) ) {
		// flagError(
		// this.getName() + " species can't be a sub-species of " + potentialParent.getName() +
		// " species because a species can't be sub-species of its direct or indirect micro-species.",
		// IGamlIssue.GENERAL);
		// return null;
		// }

	}

	/**
	 * Finalizes the species description
	 * + Copy the behaviors, attributes from parent;
	 * + Creates the control if necessary.
	 * Add a variable representing the population of each micro-species
	 *
	 * @throws GamlException
	 */
	public void finalizeDescription() {
		if ( isMirror() ) {
			IExpression expr = facets.getExpr(MIRRORS);
			addChild(DescriptionFactory.create(AGENT, this, NAME, TARGET));
		}

		control = (IArchitecture) AbstractGamlAdditions.getSkillInstanceFor(getControlName());
		buildSharedSkills();
		// recursively finalize the sorted micro-species
		for ( final SpeciesDescription microSpec : sortedMicroSpecies() ) {
			microSpec.finalizeDescription();
			if ( !microSpec.isExperiment() ) {
				final VariableDescription var =
					(VariableDescription) DescriptionFactory.create(CONTAINER, this, NAME, microSpec.getName());
				var.setSyntheticSpeciesContainer();
				var.getFacets().put(
					OF,
					GAML.getExpressionFactory().createTypeExpression(
						getModelDescription().getTypeNamed(microSpec.getName())));
				// We compute the dependencies of micro species with respect to the variables
				// defined in the macro species.
				final IExpressionDescription exp = microSpec.getFacets().get(DEPENDS_ON);
				final Set<String> dependencies = exp == null ? new TLinkedHashSet() : exp.getStrings(this, false);
				for ( final VariableDescription v : microSpec.getVariables().values() ) {
					dependencies.addAll(v.getExtraDependencies());
				}
				dependencies.add(SHAPE);
				dependencies.add(LOCATION);
				var.getFacets().put(DEPENDS_ON, new StringListExpressionDescription(dependencies));
				final GamaHelper get = new GamaHelper() {

					@Override
					public Object run(final IScope scope, final IAgent agent, final ISkill skill,
						final Object ... values) throws GamaRuntimeException {
						// TODO Make a test ?
						return ((IMacroAgent) agent).getMicroPopulation(microSpec.getName());
					}
				};
				final GamaHelper set = new GamaHelper() {

					@Override
					public Object run(final IScope scope, final IAgent agent, final ISkill target,
						final Object ... value) throws GamaRuntimeException {
						return null;
					}

				};
				final GamaHelper init = new GamaHelper(null) {

					@Override
					public Object run(final IScope scope, final IAgent agent, final ISkill skill,
						final Object ... values) throws GamaRuntimeException {
						((IMacroAgent) agent).initializeMicroPopulation(scope, microSpec.getName());
						return ((IMacroAgent) agent).getMicroPopulation(microSpec.getName());
					}

				};
				var.addHelpers(get, init, set);
				addChild(var);
			}
		}
		sortVars();
	}

	@Override
	protected void validateChildren() {
		IExpression mirrors = getFacets().getExpr(MIRRORS);
		if ( mirrors != null ) {
			// We try to change the type of the 'target' variable if the expression contains only agents from the
			// same species
			IType t = mirrors.getType().getContentType();
			if ( t.isAgentType() && t.id() != IType.AGENT ) {
				VariableDescription v = getVariable(TARGET);
				if ( v != null ) {
					// In case, but should not be null
					v.setType(t);
					info("The 'target' variable will be of type " + t.getSpeciesName(), IGamlIssue.GENERAL, MIRRORS);
				}
			} else {
				info("No common species detected in 'mirrors'. The 'target' variable will be of generic type 'agent'",
					IGamlIssue.WRONG_TYPE, MIRRORS);
			}
		}

		// We try to issue information about the state of the species: at first, abstract.

		for ( final StatementDescription a : getActions() ) {
			if ( a.isAbstract() ) {
				this.info("Action '" + a.getName() + "' is defined or inherited as virtual. In consequence, " +
					getName() + " is considered as abstract and cannot be instantiated.", IGamlIssue.MISSING_ACTION);
			}
		}

		super.validateChildren();
	}

	public boolean isExperiment() {
		return false;
	}

	boolean hasMicroSpecies() {
		return microSpecies != null;
	}

	public Map<String, SpeciesDescription> getMicroSpecies() {
		if ( microSpecies == null ) {
			microSpecies = new TOrderedHashMap<String, SpeciesDescription>();
		}
		return microSpecies;
	}

	public boolean isMirror() {
		return facets.containsKey(MIRRORS);
	}

	public Boolean implementsSkill(final String skill) {
		return skills.containsKey(AbstractGamlAdditions.getSkillClassFor(skill));
	}

	public Map<Class, ISkill> getSkills() {
		return skills;
	}

	@Override
	public Class getJavaBase() {
		// FIXME HACK Remove at some point in the future
		if ( isGrid() ) {
			if ( !facets.containsKey("use_regular_agents") || TRUE.equals(facets.getLabel("use_regular_agents")) ) {
				javaBase = GamlAgent.class;
			} else {
				javaBase = MinimalGridAgent.class;
			}
			return javaBase;
		}
		if ( getName().equals(AGENT) ) {
			javaBase = MinimalAgent.class;
			return javaBase;
		}
		// Takes care of invalid species (see Issue 711)
		if ( javaBase == null && parent != null && parent != this ) {
			javaBase = getParent().getJavaBase();
		}
		if ( javaBase == MinimalAgent.class ) {
			javaBase = GamlAgent.class;
		}
		return javaBase;
	}

	/**
	 * @param found_sd
	 * @return
	 */
	public boolean hasMacroSpecies(final SpeciesDescription found_sd) {
		SpeciesDescription sd = getMacroSpecies();
		if ( sd == null ) { return false; }
		if ( sd.equals(found_sd) ) { return true; }
		return sd.hasMacroSpecies(found_sd);
	}

	/**
	 * @param macro
	 * @return
	 */
	public boolean hasParent(final SpeciesDescription p) {
		SpeciesDescription sd = getParent();
		// Takes care of invalid species (see Issue 711)
		if ( sd == null || sd == this ) { return false; }
		if ( sd.equals(p) ) { return true; }
		return sd.hasMacroSpecies(p);
	}

	@Override
	public List<IDescription> getChildren() {
		List<IDescription> result = super.getChildren();
		if ( microSpecies != null ) {
			result.addAll(microSpecies.values());
		}
		if ( behaviors != null ) {
			result.addAll(behaviors.values());
		}
		if ( aspects != null ) {
			result.addAll(aspects.values());
		}
		return result;
	}

	/**
	 * @return
	 */
	public Collection<StatementDescription> getBehaviors() {
		return behaviors == null ? Collections.EMPTY_LIST : behaviors.values();
	}

}
