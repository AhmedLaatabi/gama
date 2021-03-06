/**
 * Created by drogoul, 22 avr. 2014
 * 
 */
package msi.gaml.expressions;

import java.util.*;
import msi.gaml.types.IType;

/**
 * Class UnitConstantExpression.
 * 
 * @author drogoul
 * @since 22 avr. 2014
 * 
 */
public class UnitConstantExpression extends ConstantExpression {

	public static UnitConstantExpression create(final Object val, final IType t, final String unit, final String doc,
		final String[] names) {
		if ( unit.equals("pixels") || unit.equals("px") ) { return new PixelUnitExpression(unit, doc); }
		if ( unit.equals("display_width") ) { return new DisplayWidthUnitExpression(doc); }
		if ( unit.equals("display_height") ) { return new DisplayHeightUnitExpression(doc); }
		return new UnitConstantExpression(val, t, unit, doc, names);
	}

	final String documentation;
	final List<String> alternateNames;

	public UnitConstantExpression(final Object val, final IType t, final String name, final String doc,
		final String[] names) {
		super(val, t);
		this.name = name;
		documentation = doc;
		alternateNames = new ArrayList();
		alternateNames.add(name);
		if ( names != null ) {
			alternateNames.addAll(Arrays.asList(names));
		}
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return "°" + name;
	}

	@Override
	public String getDocumentation() {
		return documentation;
	}

	@Override
	public String getTitle() {
		String s = "Unit " + serialize(false);
		if ( alternateNames.size() > 1 ) {
			s += " (" + alternateNames + ")";
		}
		return s;
	}

	@Override
	public boolean shouldBeParenthesized() {
		return false;
	}

}
