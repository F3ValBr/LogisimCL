/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.std.arith;

import java.util.List;

import com.cburch.logisim.tools.FactoryDescription;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.tools.Tool;

public class Arithmetic extends Library {

    public static final String LIB_NAME = "Arithmetic";

    private static final FactoryDescription[] DESCRIPTIONS = {
		new FactoryDescription(Adder._ID,       Strings.getter("adderComponent"),
				"adder.gif",        Adder.class.getSimpleName()),
		new FactoryDescription(Subtractor._ID,  Strings.getter("subtractorComponent"),
				"subtractor.gif",   Subtractor.class.getSimpleName()),
		new FactoryDescription(Multiplier._ID, Strings.getter("multiplierComponent"),
				"multiplier.gif",   Multiplier.class.getSimpleName()),
		new FactoryDescription(Divider._ID,     Strings.getter("dividerComponent"),
				"divider.gif",      Divider.class.getSimpleName()),
		new FactoryDescription(Negator._ID,     Strings.getter("negatorComponent"),
				"negator.gif",      Negator.class.getSimpleName()),
		new FactoryDescription(Comparator._ID,  Strings.getter("comparatorComponent"),
				"comparator.gif",   Comparator.class.getSimpleName()),
		new FactoryDescription(Shifter._ID,     Strings.getter("shifterComponent"),
				"shifter.gif",      Shifter.class.getSimpleName()),
		new FactoryDescription(BitAdder._ID,    Strings.getter("bitAdderComponent"),
				"bitadder.gif",     BitAdder.class.getSimpleName()),
		new FactoryDescription(BitFinder._ID,   Strings.getter("bitFinderComponent"),
				"bitfindr.gif",     BitFinder.class.getSimpleName()),
	};
	
	private List<Tool> tools = null;

	public Arithmetic() { }

	@Override
	public String getName() { return LIB_NAME; }

	@Override
	public String getDisplayName() { return Strings.get("arithmeticLibrary"); }

	@Override
	public List<Tool> getTools() {
		if (tools == null) {
			tools = FactoryDescription.getTools(Arithmetic.class, DESCRIPTIONS);
		}
		return tools;
	}
}
