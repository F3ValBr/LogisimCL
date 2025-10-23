package com.cburch.logisim.std.arith;

import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.std.PortMapRegister;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.verilog.std.BuiltinPortMaps;

import java.util.Map;

public final class ArithmeticPortMapRegister implements PortMapRegister {
    @Override
    public void register(LogisimFile lf) {
        Library arithLib = lf.getLibrary("Arithmetic");
        if (arithLib == null) return;

        BuiltinPortMaps.registerByName(arithLib.getName(), "Adder",
                Map.of("A", 0, "B", 1, "Y", 2, "CIN", 3, "COUT", 4));
        BuiltinPortMaps.registerByName(arithLib.getName(), "Subtractor",
                Map.of("A", 0, "B", 1, "Y", 2, "BIN", 3, "BOUT", 4));
        BuiltinPortMaps.registerByName(arithLib.getName(), "Multiplier",
                Map.of("A", 0, "B", 1, "Y", 2, "CIN", 3, "COUT", 4));
        BuiltinPortMaps.registerByName(arithLib.getName(), "Divider",
                Map.of("A", 0, "B", 1, "Y", 2, "REM", 4));
        BuiltinPortMaps.registerByName(arithLib.getName(), "Comparator",
                Map.of("A", 0, "B", 1, "GT", 2, "EQ", 3, "LT", 4));
        BuiltinPortMaps.registerByName(arithLib.getName(), "Negator",
                Map.of("A", 0, "Y", 1));
        BuiltinPortMaps.registerByName(arithLib.getName(), "Shifter",
                Map.of("A", 0, "B", 1, "Y", 2));
    }
}
