/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.std.memory;

import java.util.List;

import com.cburch.logisim.tools.FactoryDescription;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.tools.Tool;

public class Memory extends Library {

    public static final String LIB_NAME = "Memory";

    protected static final int DELAY = 5;
	
	private static FactoryDescription[] DESCRIPTIONS = {
		new FactoryDescription(DFlipFlop._ID,       Strings.getter("dFlipFlopComponent"),
				"dFlipFlop.gif",    DFlipFlop.class.getSimpleName()),
		new FactoryDescription(TFlipFlop._ID,       Strings.getter("tFlipFlopComponent"),
				"tFlipFlop.gif",    TFlipFlop.class.getSimpleName()),
		new FactoryDescription(JKFlipFlop._ID,      Strings.getter("jkFlipFlopComponent"),
				"jkFlipFlop.gif",   JKFlipFlop.class.getSimpleName()),
		new FactoryDescription(SRFlipFlop._ID,      Strings.getter("srFlipFlopComponent"),
				"srFlipFlop.gif",   SRFlipFlop.class.getSimpleName()),
		new FactoryDescription(Register._ID,        Strings.getter("registerComponent"),
				"register.gif",     Register.class.getSimpleName()),
		new FactoryDescription(Counter._ID,         Strings.getter("counterComponent"),
				"counter.gif",      Counter.class.getSimpleName()),
		new FactoryDescription(ShiftRegister._ID,   Strings.getter("shiftRegisterComponent"),
				"shiftreg.gif",     ShiftRegister.class.getSimpleName()),
		new FactoryDescription(Random._ID,          Strings.getter("randomComponent"),
				"random.gif",       Random.class.getSimpleName()),
		new FactoryDescription(Ram._ID,             Strings.getter("ramComponent"),
				"ram.gif",          Ram.class.getSimpleName()),
		new FactoryDescription(Rom._ID,             Strings.getter("romComponent"),
				"rom.gif",          Rom.class.getSimpleName()),
		new FactoryDescription(Timer._ID,           Strings.getter("timerComponent"),
				"rom.gif",          Timer.class.getSimpleName()),
	};
	
	private List<Tool> tools = null;

	public Memory() { }

	@Override
	public String getName() { return LIB_NAME; }

	@Override
	public String getDisplayName() { return Strings.get("memoryLibrary"); }

	@Override
	public List<Tool> getTools() {
		if (tools == null) {
			tools = FactoryDescription.getTools(Memory.class, DESCRIPTIONS);
		}
		return tools;
	}
}
