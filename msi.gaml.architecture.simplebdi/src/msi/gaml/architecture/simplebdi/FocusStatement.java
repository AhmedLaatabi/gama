/*********************************************************************************************
 * 
 *
 * 'FocusStatement.java', in plugin 'msi.gaml.architecture.simplebdi', is part of the source code of the 
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
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gaml.compilation.IDescriptionValidator.ValidNameValidator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;

@symbol(name = FocusStatement.FOCUS, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@facets(value = {
	@facet(name = IKeyword.NAME, type = IType.ID, optional = true, doc = @doc("the identifier of the focus")),	
	@facet(name = FocusStatement.VAR/*IKeyword.VAR*/, type = {IType.NONE,IType.LIST,IType.CONTAINER},optional = true, doc = @doc("the variable of the perceived agent you want to add to your beliefs")),
	@facet(name = FocusStatement.EXPRESSION, type = IType.NONE,optional = true, doc = @doc("an expression that will be the value kept in the belief")),
	@facet(name = IKeyword.AGENT, type = IType.AGENT,optional = false, doc = @doc("the agent that will add the belief (use the myself pseudo-variable")),
	@facet(name = IKeyword.WHEN, type = IType.BOOL, optional = true, doc = @doc("A boolean value to focus only with a certian condition")),
	@facet(name = FocusStatement.PRIORITY, type = {IType.FLOAT,IType.INT}, optional = true, doc = @doc("The priority of the created predicate"))}
,omissible = IKeyword.NAME)
//@validator(ValidNameValidator.class)
@doc( value = "enables to directly add a belief from the variable of a perceived specie.",
		examples={@example("focus var:speed /*where speed is a variable from a species that is being perceived*/ agent: myself")})
public class FocusStatement extends AbstractStatement {

	public static final String FOCUS = "focus";
	public static final String PRIORITY = "priority";
	public static final String EXPRESSION = "expression";
	public static final String VAR = "var";
	
	final IExpression name;
	final IExpression variable;
	final IExpression expression;
	final IExpression agentMyself;
	final IExpression when;
	final IExpression priority;
	
	
	public FocusStatement(IDescription desc) {
		super(desc);
		name = getFacet(IKeyword.NAME);
		variable = getFacet(FocusStatement.VAR);
		expression = getFacet(FocusStatement.EXPRESSION);
		agentMyself = getFacet(IKeyword.AGENT);
		when = getFacet(IKeyword.WHEN);
		priority = getFacet(FocusStatement.PRIORITY);
	}

	@Override
	protected Object privateExecuteIn(IScope scope) throws GamaRuntimeException {
		if ( when == null || Cast.asBool(scope, when.value(scope)) ){
			final IAgent mySelfAgent = (IAgent) agentMyself.value(scope);
			IScope scopeMySelf = null;
			if(mySelfAgent!=null){
				scopeMySelf = mySelfAgent.getScope().copy();
				scopeMySelf.push(mySelfAgent);
			}
			final Predicate tempPred;
			if(variable!=null){
//				if(variable.value(scope) instanceof IContainer){
//					String namePred;
//					if(name!=null){
//						namePred = (String) name.value(scope);
//					}else{
//						namePred = variable.getName()+"_"+scope.getAgentScope().getSpeciesName();
//					}
//					String nameVarTemp;
//					Map<String,Object> tempValues = (Map<String, Object>) new GamaMap<String,Object>(1, null, null);
//					Object truc = (Object)variable/*.value(scope)*/;
//					final IList variablesTemp = (IList) truc;
////					final IList variablesTemp = ((IContainer) variable.value(scope)).listValue(scope, null, true);
//					for(int temp=0;temp<variablesTemp.length(scope);temp++){
//						Object temp2 = variablesTemp.get(temp);
//						nameVarTemp = ((Object) variablesTemp.get(temp)).toString();
//						if(temp2 instanceof Integer){
//							tempValues.put(nameVarTemp + "_value", Cast.asInt(scope, temp2));
//						}
//					}
//					tempPred = new Predicate(namePred,(Map<String, Object>) GamaMapFactory.createWithoutCasting(((GamaMap<String,Object>) tempValues).getType().getKeyType(), ((GamaMap<String,Object>) tempValues).getType().getContentType(), tempValues));
//					if(priority!=null){
//						tempPred.setPriority(Cast.asFloat(scopeMySelf, priority.value(scopeMySelf)));
//					}
//					if(!SimpleBdiArchitecture.hasBelief(scopeMySelf, tempPred)){
//						SimpleBdiArchitecture.addBelief(scopeMySelf, tempPred);
//					}
//				}
//				else{
					String namePred;
					if(name!=null){
						namePred = (String) name.value(scope);
					}else{
						namePred = variable.getName()+"_"+scope.getAgentScope().getSpeciesName();
					}
					String nameVar = variable.getName();
					Map<String,Object> tempValues = (Map<String, Object>) new GamaMap<String,Object>(1, null, null);
					if(expression!=null){
						tempValues.put(nameVar + "_value", expression.value(scope));
					}else{
						tempValues.put(nameVar + "_value", variable.value(scope));
					}
					tempPred = new Predicate(namePred,(Map<String, Object>) GamaMapFactory.createWithoutCasting(((GamaMap<String,Object>) tempValues).getType().getKeyType(), ((GamaMap<String,Object>) tempValues).getType().getContentType(), tempValues));
					if(priority!=null){
						tempPred.setPriority(Cast.asFloat(scopeMySelf, priority.value(scopeMySelf)));
					}
					if(!SimpleBdiArchitecture.hasBelief(scopeMySelf, tempPred)){
						SimpleBdiArchitecture.addBelief(scopeMySelf, tempPred);
					}
//				}
			}else{
				if(expression!=null){
					String namePred;
					if(name!=null){
						namePred = (String) name.value(scope);
					}else{
						namePred = "expression"+"_"+scope.getAgentScope().getSpeciesName();
					}
					String nameVar = "expression";
					Map<String,Object> tempValues = (Map<String, Object>) new GamaMap<String,Object>(1, null, null);
					tempValues.put(nameVar + "_value", expression.value(scope));
					tempPred = new Predicate(namePred,(Map<String, Object>) GamaMapFactory.createWithoutCasting(((GamaMap<String,Object>) tempValues).getType().getKeyType(), ((GamaMap<String,Object>) tempValues).getType().getContentType(), tempValues));
					if(priority!=null){
						tempPred.setPriority(Cast.asFloat(scopeMySelf, priority.value(scopeMySelf)));
					}
					if(!SimpleBdiArchitecture.hasBelief(scopeMySelf, tempPred)){
						SimpleBdiArchitecture.addBelief(scopeMySelf, tempPred);
					}
				}
			}
		}
		return null;
	}

}