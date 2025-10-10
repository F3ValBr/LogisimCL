package com.cburch.logisim.std.gates;

import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.std.PortMapRegister;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.verilog.std.BuiltinPortMaps;

import java.util.Map;

public final class GatesPortMapRegister implements PortMapRegister {
    @Override
    public void register(LogisimFile lf) {
        Library gatesLib = lf.getLibrary("Gates");
        if (gatesLib == null) return;

        BuiltinPortMaps.registerByName(gatesLib.getName(), "AND Gate",
                Map.of("Y", 0, "A", 1, "B", 2));
        BuiltinPortMaps.registerByName(gatesLib.getName(), "NAND Gate",
                Map.of("Y", 0, "A", 1, "B", 2));
        BuiltinPortMaps.registerByName(gatesLib.getName(), "OR Gate",
                Map.of("Y", 0, "A", 1, "B", 2));
        BuiltinPortMaps.registerByName(gatesLib.getName(), "NOR Gate",
                Map.of("Y", 0, "A", 1, "B", 2));
        BuiltinPortMaps.registerByName(gatesLib.getName(), "XOR Gate",
                Map.of("Y", 0, "A", 1, "B", 2));
        BuiltinPortMaps.registerByName(gatesLib.getName(), "XNOR Gate",
                Map.of("Y", 0, "A", 1, "B", 2));
        BuiltinPortMaps.registerByName(gatesLib.getName(), "NOT Gate",
                Map.of("Y", 0, "A", 1));
        BuiltinPortMaps.registerByName(gatesLib.getName(), "Buffer",
                Map.of("Y", 0, "A", 1));
        BuiltinPortMaps.registerByName(gatesLib.getName(), "Controlled Buffer",
                Map.of("Y", 0, "A", 1, "EN", 2));

    }
}
