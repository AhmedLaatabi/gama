/*********************************************************************************************
 * 
 *
 * 'ClassicalSIREquations.java', in plugin 'ummisco.gaml.extensions.maths', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gaml.extensions.maths.ode.utils.classicalEquations.epidemiology;

import java.util.ArrayList;
import java.util.List;

import msi.gama.util.GAML;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.ListExpression;
import msi.gaml.factories.ChildrenProvider;
import msi.gaml.statements.Facets;
import ummisco.gaml.extensions.maths.ode.statements.SingleEquationStatement;

//SIR equation is defined by 
// diff(S,t) = (- beta * S * I / N);
// diff(I,t) = (beta * S * I / N) - (gamma * I);
// diff(R,t) = (gamma * I);
//
//It is called using
//equation eqSIR type: SIR vars: [S,I,R,t] params: [N,beta,gamma]

public class ClassicalSIREquations {
	private final IDescription parentDesc;

	public ClassicalSIREquations(IDescription p) {
		parentDesc = p;
	}

	public IDescription getDescription() {
		return parentDesc;
	}

	public List<SingleEquationStatement> SIR(IExpression with_vars, IExpression with_params) {
		if (with_vars == null || with_params == null) {
			return null;
		}
		ArrayList<SingleEquationStatement> cmd = new ArrayList<SingleEquationStatement>();
		IExpression[] v = ((ListExpression) with_vars).getElements();
		IExpression[] p = ((ListExpression) with_params).getElements();

		StatementDescription stm = new StatementDescription("=",
				getDescription(), new ChildrenProvider(null), false, false,
				null, new Facets("keyword", "="));

		SingleEquationStatement eq1 = new SingleEquationStatement(stm);
		eq1.setFunction(GAML.getExpressionFactory()
				.createExpr(
						"diff(" + v[0].literalValue() + ","
								+ v[3].literalValue() + ")", getDescription()));
		eq1.setExpression(GAML.getExpressionFactory().createExpr(
				"(- " + p[1].literalValue() + " * " + v[0].literalValue()
						+ " * " + v[1].literalValue() + " / "
						+ p[0].literalValue() + ")", getDescription()));
		eq1.establishVar();
		cmd.add(eq1);

		SingleEquationStatement eq2 = new SingleEquationStatement(stm);
		eq2.setFunction(GAML.getExpressionFactory()
				.createExpr(
						"diff(" + v[1].literalValue() + ","
								+ v[3].literalValue() + ")", getDescription()));
		eq2.setExpression(GAML.getExpressionFactory().createExpr(
				"(" + p[1].literalValue() + " * " + v[0].literalValue() + " * "
						+ v[1].literalValue() + " / " + p[0].literalValue()
						+ ") - ("+p[2].literalValue()+" * " + v[1].literalValue() + ")",
				getDescription()));
		eq2.establishVar();
		cmd.add(eq2);

		SingleEquationStatement eq3 = new SingleEquationStatement(stm);
		eq3.setFunction(GAML.getExpressionFactory().createExpr(
				"diff(" + v[2].literalValue() + "," + v[3].literalValue() + ")", getDescription()));
		eq3.setExpression(GAML.getExpressionFactory().createExpr(
				"(" + p[2].literalValue() + " * " + v[1].literalValue() + ")",
				getDescription()));
		eq3.establishVar();
		cmd.add(eq3);
		return cmd;
	}

}
