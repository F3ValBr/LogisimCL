package com.cburch.logisim.std.plexers;

import com.cburch.logisim.comp.Component;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.std.PortMapRegister;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.verilog.std.BuiltinPortMaps;

import java.util.LinkedHashMap;
import java.util.Map;

public class PlexersPortMapRegister implements PortMapRegister {
    @Override
    public void register(LogisimFile lf) {
        Library plexersLib = lf.getLibrary("Plexers");
        if (plexersLib == null) return;

        BuiltinPortMaps.registerResolverByName(plexersLib.getName(), "Multiplexer",
                PlexersPortMapRegister::resolveMultiplexerPorts);
        BuiltinPortMaps.registerByName(plexersLib.getName(), "Demultiplexer",
                java.util.Map.of("A", 0, "S", 1, "Y", 2));
    }

    private static Map<String,Integer> resolveMultiplexerPorts(Component component) {
        AttributeSet as = component.getAttributeSet();

        // Leer select width y enable
        BitWidth selBw = as.getValue(Plexers.ATTR_SELECT);
        int selW = (selBw == null ? 1 : Math.max(1, selBw.getWidth()));
        int inputs = 1 << selW;
        boolean enable = Boolean.TRUE.equals(as.getValue(Plexers.ATTR_ENABLE));

        // Índices según Multiplexer.updatePorts(...)
        int sIdx  = inputs;
        int enIdx = enable ? inputs + 1 : -1;
        int yIdx  = enable ? inputs + 2 : inputs + 1;

        // Mapa de nombres -> índices
        LinkedHashMap<String,Integer> m = new LinkedHashMap<>();

        // Entradas: usar letras A, B, C, ... Z, AA, AB, etc.
        for (int i = 0; i < inputs; i++) {
            m.put(indexToLetter(i), i);
        }

        m.put("S", sIdx);
        if (enable) m.put("EN", enIdx);
        m.put("Y", yIdx);

        // Aliases para compatibilidad
        m.putIfAbsent("A", 0);
        if (inputs > 1) m.putIfAbsent("B", 1);

        return m;
    }

    /** Convierte un índice a una letra tipo Excel: 0→A, 1→B, ..., 25→Z, 26→AA, 27→AB, etc. */
    private static String indexToLetter(int index) {
        StringBuilder sb = new StringBuilder();
        int n = index;
        do {
            int rem = n % 26;
            sb.insert(0, (char) ('A' + rem));
            n = n / 26 - 1;
        } while (n >= 0);
        return sb.toString();
    }
}
