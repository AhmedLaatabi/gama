/*********************************************************************************************
 * 
 *
 * 'RuleStatement.java', in plugin 'msi.gaml.architecture.simplebdi', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/

package msi.gaml.architecture.simplebdi;

import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;


@symbol(name = RuleStatement.RULE, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@inside(kinds = { ISymbolKind.SPECIES })
@facets(value = {
	@facet(name = RuleStatement.BELIEF, type = PredicateType.id, optional = true, doc = @doc("The mandatory belief")),
	@facet(name = RuleStatement.DESIRE, type = PredicateType.id, optional = true, doc = @doc("The mandatory desire")),
	@facet(name = RuleStatement.NEW_DESIRE , type = PredicateType.id, optional = true, doc = @doc("The desire that will be added")),
	@facet(name = RuleStatement.NEW_BELIEF, type = PredicateType.id, optional = true, doc = @doc("The belief that will be added")),
	@facet(name = RuleStatement.REMOVE_BELIEF, type = PredicateType.id, optional = true, doc = @doc("The belief that will be removed")),
	@facet(name = RuleStatement.REMOVE_DESIRE, type = PredicateType.id, optional = true, doc = @doc("The desire that will be removed")),
	@facet(name = RuleStatement.REMOVE_INTENTION, type = PredicateType.id, optional = true, doc = @doc("The intention that will be removed")),
	@facet(name = IKeyword.WHEN, type = IType.BOOL, optional = true, doc = @doc(" ")),
	@facet(name = RuleStatement.PRIORITY, type = {IType.FLOAT,IType.INT}, optional = true, doc = @doc("The priority of the predicate added as a desire")),
	@facet(name = IKeyword.NAME, type = IType.ID, optional = true, doc = @doc("The name of the rule"))}
,omissible = IKeyword.NAME)
@doc( value = "enables to add a desire or a belief or to remove a belief, a desire or an intention if the agent gets the belief or/and desire or/and condition mentioned.",
		examples={@example("rule belief: new_predicate(\"test\") when: flip(0.5) new_desire: new_predicate(\"test\")")})
public class RuleStatement extends AbstractStatement{

	public static final String RULE = "rule";
	public static final String BELIEF = "belief";
	public static final String DESIRE = "desire";
	public static final String NEW_DESIRE = "new_desire";
	public static final String NEW_BELIEF = "new_belief";
	public static final String REMOVE_BELIEF = "remove_belief";
	public static final String REMOVE_DESIRE = "remove_desire";
	public static final String REMOVE_INTENTION = "remove_intention";
	public static final String PRIORITY = "priority";
	
	final IExpression when;
	final IExpression belief;
	final IExpression desire;
	final IExpression newBelief;
	final IExpression newDesire;
	final IExpression removeBelief;
	final IExpression removeDesire;
	final IExpression removeIntention;
	final IExpression priority;
	
	public RuleStatement(IDescription desc) {
		super(desc);
		when = getFacet(IKeyword.WHEN);
		belief = getFacet(RuleStatement.BELIEF);
		desire = getFacet(RuleStatement.DESIRE);
		newBelief = getFacet(RuleStatement.NEW_BELIEF);
		newDesire = getFacet(RuleStatement.NEW_DESIRE);
		removeBelief = getFacet(RuleStatement.REMOVE_BELIEF);
		removeDesire = getFacet(RuleStatement.REMOVE_DESIRE);
		removeIntention = getFacet(RuleStatement.REMOVE_INTENTION);
		priority = getFacet(RuleStatement.PRIORITY);
	}

	@Override
	protected Object privateExecuteIn(IScope scope) throws GamaRuntimeException {
		if (newBelief == null && newDesire == null && removeBelief == null && removeDesire == null && removeIntention == null) return null;
		if ( when == null || Cast.asBool(scope, when.value(scope)) ){
			if( belief == null || SimpleBdiArchitecture.hasBelief(scope, (Predicate)(belief.value(scope)))) {
				if( desire == null || SimpleBdiArchitecture.hasDesire(scope, (Predicate)(desire.value(scope)))) {
					if (newDesire != null) {
						Predicate newDes = ((Predicate)(newDesire.value(scope)));
						if(newDes.getValues()==null){
							if(priority!=null){
								newDes.setPriority(Cast.asFloat(scope, priority.value(scope)));
							}
							SimpleBdiArchitecture.addDesire(scope, null, newDes);
						}else{
							//Il faut copier la liste des valeurs.
							newDes.setValues((Map<String, Object>) GamaMapFactory.createWithoutCasting(newDes.getType().getKeyType(), newDes.getType().getContentType(), ((Predicate)(newDesire.value(scope))).getValues()));
							if(priority!=null){
								newDes.setPriority(Cast.asFloat(scope, priority.value(scope)));
							}
							SimpleBdiArchitecture.addDesire(scope, null, newDes);
						}
					}
					if (newBelief != null) {
						Predicate newBel = ((Predicate)(newBelief.value(scope)));
						if(newBel.getValues()==null){
							SimpleBdiArchitecture.addBelief(scope, ((Predicate)(newBelief.value(scope))));
						}else{
							//Il faut copier la liste des valeurs.
							newBel.setValues((Map<String, Object>) GamaMapFactory.createWithoutCasting(newBel.getType().getKeyType(), newBel.getType().getContentType(), ((Predicate)(newBelief.value(scope))).getValues()));
							SimpleBdiArchitecture.addBelief(scope, newBel);
						}
					}
					if(removeBelief != null){
						Predicate removBel = (Predicate)removeBelief.value(scope);
						SimpleBdiArchitecture.removeBelief(scope, removBel);
					}
					if(removeDesire != null){
						Predicate removeDes = (Predicate)removeDesire.value(scope);
						SimpleBdiArchitecture.removeDesire(scope, removeDes);
					}
					if(removeIntention != null){
						Predicate removeInt = (Predicate)removeIntention.value(scope);
						SimpleBdiArchitecture.removeIntention(scope, removeInt);
					}
				}
			}
		}
		return null;
	}

}
