/*********************************************************************************************
 * 
 * 
 * 'IUnits.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.operators;

import java.awt.Font;
import java.lang.reflect.Field;
import java.util.*;
import msi.gama.precompiler.GamlAnnotations.constant;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.*;
import msi.gama.util.*;
import msi.gaml.expressions.UnitConstantExpression;
import msi.gaml.types.*;

public class IUnits {

	/**
	 * Font style constants
	 */

	@constant(value = "bold",
		category = { IConstantCategory.GRAPHIC },
		doc = @doc("This contant allows to build a font with a bold face. Can be combined with #italic"))
	public final static int bold = Font.BOLD; /* 1 */

	@constant(value = "italic",
		category = { IConstantCategory.GRAPHIC },
		doc = @doc("This contant allows to build a font with an italic face. Can be combined with #bold"))
	public final static int italic = Font.ITALIC; /* 2 */

	@constant(value = "plain",
		category = { IConstantCategory.GRAPHIC },
		doc = @doc("This contant allows to build a font with a plain face"))
	public final static int plain = Font.PLAIN;
	/**
	 * Special units
	 */

	@constant(value = "pixels",
		altNames = { "px" },
		category = { IConstantCategory.GRAPHIC },
		doc = @doc("This unit, only available when running aspects or declaring displays, can be obtained using the same approach, but returns a dynamic value instead of a fixed one. px (or pixels), returns the value of one pixel on the current view in terms of model units."))
	public final static double pixels = 1, px = pixels; // Represents the value of a pixel in terms
														// of model units. Parsed early
														// and never used as a constant.
	@constant(value = "display_width",
		category = { IConstantCategory.GRAPHIC },
		doc = @doc("This constant is only accessible in a graphical context: display, graphics..."))
	public final static double display_width = 1;

	@constant(value = "display_height",
		category = { IConstantCategory.GRAPHIC },
		doc = @doc("This constant is only accessible in a graphical context: display, graphics..."))
	public final static double display_height = 1;

	/**
	 * Mathematical constants
	 * 
	 */
	@constant(value = "pi", category = { IConstantCategory.CONSTANT }, doc = @doc("The PI constant"))
	public final static double pi = Math.PI;

	@constant(value = "e", category = { IConstantCategory.CONSTANT }, doc = @doc("The e constant"))
	public final static double e = Math.E;

	@constant(value = "to_deg",
		category = { IConstantCategory.CONSTANT },
		doc = @doc("A constant holding the value to convert radians into degrees"))
	public final static double to_deg = 180d / Math.PI;
	@constant(value = "to_rad",
		category = { IConstantCategory.CONSTANT },
		doc = @doc("A constant holding the value to convert degrees into radians"))
	public final static double to_rad = Math.PI / 180d;

	@constant(value = "nan",
		category = { IConstantCategory.CONSTANT },
		doc = @doc("A constant holding a Not-a-Number (NaN) value of type float (Java Double.POSITIVE_INFINITY)"))
	public final static double nan = Double.NaN;
	@constant(value = "infinity",
		category = { IConstantCategory.CONSTANT },
		doc = @doc("A constant holding the positive infinity of type (Java Double.POSITIVE_INFINITY)"))
	public final static double infinity = Double.POSITIVE_INFINITY;
	@constant(value = "min_float",
		category = { IConstantCategory.CONSTANT },
		doc = @doc("A constant holding the smallest positive nonzero value of type float (Java Double.MIN_VALUE)"))
	public final static double min_float = Double.MIN_VALUE;
	@constant(value = "max_float",
		category = { IConstantCategory.CONSTANT },
		doc = @doc("A constant holding the largest positive finite value of type float (Java Double.MAX_VALUE)"))
	public final static double max_float = Double.MAX_VALUE;
	@constant(value = "min_int",
		category = { IConstantCategory.CONSTANT },
		doc = @doc("A constant holding the minimum value an int can have (Java Integer.MIN_VALUE)"))
	public final static double min_int = Integer.MIN_VALUE;
	@constant(value = "max_int",
		category = { IConstantCategory.CONSTANT },
		doc = @doc("A constant holding the maximum value an int can have (Java Integer.MAX_VALUE)"))
	public final static double max_int = Integer.MAX_VALUE;
	/*
	 * 
	 * Distance & size conversions
	 */
	/** The Constant m. */
	@constant(value = "m",
		altNames = { "meter", "meters" },
		category = { IConstantCategory.LENGTH },
		doc = @doc("meter: the length basic unit"))
	public final static double m = 1, meter = m, meters = m;

	/** The Constant cm. */
	@constant(value = "cm", altNames = { "centimeter", "centimeters" }, category = { IConstantCategory.LENGTH })
	public final static double cm = 0.01f * m, centimeter = cm, centimeters = cm;

	/** The Constant dm. */
	@constant(value = "dm", altNames = { "decimeter", "decimeters" }, category = { IConstantCategory.LENGTH })
	public final static double dm = 0.1f * m, decimeter = dm, decimeters = dm;

	/** The Constant mm. */
	@constant(value = "mm", altNames = { "milimeter", "milimeters" }, category = { IConstantCategory.LENGTH })
	public final static double mm = cm / 10, millimeter = mm, millimeters = mm;

	/** The Constant km. */
	@constant(value = "km", altNames = { "kilometer", "kilometers" }, category = { IConstantCategory.LENGTH })
	public final static double km = 1000 * m, kilometer = km, kilometers = km;

	/** The Constant mile. */
	@constant(value = "mile", altNames = { "miles" }, category = { IConstantCategory.LENGTH })
	public final static double mile = 1.609344d * km, miles = mile;

	/** The Constant yard. */
	@constant(value = "yard", altNames = { "yards" }, category = { IConstantCategory.LENGTH })
	public final static double yard = 0.9144d * m, yards = yard;

	/** The Constant inch. */
	@constant(value = "inch", altNames = { "inches" }, category = { IConstantCategory.LENGTH })
	public final static double inch = 2.54d * cm, inches = inch;

	/** The Constant foot. */
	@constant(value = "foot", altNames = { "feet", "ft" }, category = { IConstantCategory.LENGTH })
	public final static double foot = 30.48d * cm, feet = foot, ft = foot;

	/*
	 * 
	 * Time conversions
	 */
	/** The Constant s. */
	@constant(value = "sec",
		altNames = { "second", "seconds", "s" },
		category = { IConstantCategory.TIME },
		doc = @doc("second: the time basic unit"))
	public final static double sec = 1, second = sec, seconds = sec, s = sec;

	/** The Constant mn. */
	@constant(value = "minute", altNames = { "minutes", "mn" }, category = { IConstantCategory.TIME })
	public final static double minute = 60 * sec, minutes = minute, mn = minute;

	/** The Constant h. */
	@constant(value = "h", altNames = { "hour", "hours" }, category = { IConstantCategory.TIME })
	public final static double h = 60 * minute, hour = h, hours = h;

	/** The Constant d. */
	@constant(value = "day", altNames = { "days", "day" }, category = { IConstantCategory.TIME })
	public final static double day = 24 * h, days = day, d = day;

	/** The Constant month. */
	@constant(value = "month",
		altNames = { "months" },
		category = { IConstantCategory.TIME },
		doc = @doc("Note that 1 month equals 30 days and 1 year 360 days in these units"))
	public final static double month = 30 * day, months = month;

	/** The Constant y. */
	@constant(value = "year",
		altNames = { "years", "y" },
		category = { IConstantCategory.TIME },
		doc = @doc("Note that 1 month equals 30 days and 1 year 360 days in these units"))
	public final static double year = 12 * month, years = year, y = year;

	/** The Constant msec. */
	@constant(value = "msec", altNames = { "millisecond", "milliseconds" }, category = { IConstantCategory.TIME })
	public final static double msec = sec / 1000, millisecond = msec, milliseconds = msec;

	/*
	 * 
	 * Weight conversions
	 */

	/** The Constant kg. */
	@constant(value = "kg",
		altNames = { "kilo", "kilogram", "kilos" },
		category = { IConstantCategory.WEIGHT },
		doc = @doc("second: the basic unit for weights"))
	public final static double kg = 1, kilo = kg, kilogram = kg, kilos = kg;

	/** The Constant g. */
	@constant(value = "gram", altNames = { "grams" }, category = { IConstantCategory.WEIGHT })
	public final static double gram = kg / 1000, grams = gram;

	/** The Constant ton. */
	@constant(value = "ton", altNames = { "tons" }, category = { IConstantCategory.WEIGHT })
	public final static double ton = 1000 * kg, tons = ton;

	/** The Constant ounce. */
	@constant(value = "ounce", altNames = { "oz", "ounces" }, category = { IConstantCategory.WEIGHT })
	public final static double ounce = 28.349523125 * gram, oz = ounce, ounces = ounce;

	/** The Constant pound. */
	@constant(value = "pound", altNames = { "lb", "poudns", "lbm" }, category = { IConstantCategory.WEIGHT })
	public final static double pound = 0.45359237 * kg, lb = pound, pounds = pound, lbm = pound;

	/*
	 * 
	 * Volume conversions
	 */
	/** The Constant m3. */
	@constant(value = "m3",
		category = { IConstantCategory.VOLUME },
		doc = @doc("cube meter: the basic unit for volumes"))
	public final static double m3 = 1;

	/** Constant field dm3. */
	@constant(value = "l", altNames = { "liter", "liters", "dm3" }, category = { IConstantCategory.VOLUME })
	public final static double l = m3 / 1000, liter = l, liters = l, dm3 = l;

	/** The Constant cl. */
	@constant(value = "cl", altNames = { "centiliter", "centiliters" }, category = { IConstantCategory.VOLUME })
	public final static double cl = l / 100, centiliter = cl, centiliters = cl;

	/** The Constant dl. */
	@constant(value = "dl", altNames = { "deciliter", "deciliters" }, category = { IConstantCategory.VOLUME })
	public final static double dl = l / 10, deciliter = dl, deciliters = dl;

	/** The Constant hl. */
	@constant(value = "hl", altNames = { "hectoliter", "hectoliters" }, category = { IConstantCategory.VOLUME })
	public final static double hl = l * 100, hectoliter = hl, hectoliters = hl;
	/*
	 * 
	 * Surface conversions
	 */
	/** The Constant m2. */
	@constant(value = "m2",
		category = { IConstantCategory.SURFACE },
		doc = @doc("square meter: the basic unit for surfaces"))
	public final static double m2 = m * m, square_meter = m2, square_meters = m2;

	/** The Constant square inch. */
	@constant(value = "sqin", altNames = { "square_inch", "square_inches" }, category = { IConstantCategory.SURFACE })
	public final static double sqin = inch * inch, square_inch = sqin, square_inches = sqin;

	/** The Constant square foot. */
	@constant(value = "sqft", altNames = { "square_foot", "square_feet" }, category = { IConstantCategory.SURFACE })
	public final static double sqft = foot * foot, square_foot = sqft, square_feet = sqft;

	/** The Constant square mile. */
	@constant(value = "sqmi", altNames = { "square_mile", "square_miles" }, category = { IConstantCategory.SURFACE })
	public final static double sqmi = mile * mile, square_mile = sqmi, square_miles = sqmi;

	public final static Map<String, UnitConstantExpression> UNITS_EXPR = new HashMap();

	static Object add(final String name, final Object value, final String doc, final String[] names) {
		if ( UNITS_EXPR.containsKey(name) ) { return null; }
		IType t = Types.get(value.getClass());
		UnitConstantExpression exp = GAML.getExpressionFactory().createUnit(value, t, name, doc, names);
		UNITS_EXPR.put(name, exp);
		if ( names != null ) {
			for ( String s : names ) {
				UNITS_EXPR.put(s, exp);
			}
		}
		return value;
	}

	static {
		for ( Map.Entry<String, GamaColor> entry : GamaColor.colors.entrySet() ) {
			GamaColor c = entry.getValue();
			String doc =
				"standard CSS color corresponding to " + "rgb (" + c.red() + ", " + c.green() + ", " + c.blue() + "," +
					c.getAlpha() + ")";
			add(entry.getKey(), c, doc, null);
		}

		for ( final Field f : IUnits.class.getDeclaredFields() ) {
			try {
				Object v = f.get(IUnits.class);
				String[] names = null;
				constant annotation = f.getAnnotation(constant.class);
				String documentation = "Its value is " + Cast.toGaml(v) + ". </b>";
				if ( annotation != null ) {
					names = annotation.altNames();
					doc[] ds = annotation.doc();
					if ( ds != null && ds.length > 0 ) {
						doc d = ds[0];
						documentation += d.value();
					}
				}
				add(f.getName(), v, documentation, names);

			} catch (final IllegalArgumentException e) {
				e.printStackTrace();
			} catch (final IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

}