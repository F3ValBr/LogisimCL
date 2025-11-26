package com.cburch.logisim.verilog.file.importer;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitMutation;
import com.cburch.logisim.circuit.Wire;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.verilog.std.Strings;

import java.util.ArrayList;
import java.util.List;

// Acumulador simple para un módulo
public final class ImportBatch {
    private final Circuit circuit;
    private final List<Object> toAdd = new ArrayList<>();
    private final List<Object> toRemove = new ArrayList<>();

    public ImportBatch(Circuit circuit) {
        this.circuit = circuit;
    }

    // --- helpers de uso cómodo ---
    public void addComponent(Component c) {
        if (c != null) toAdd.add(c);
    }

    public void addWire(Wire w) {
        if (w != null) toAdd.add(w);
    }

    public void removeComponent(Component c) {
        if (c != null) toRemove.add(c);
    }

    public void removeWire(Wire w) {
        if (w != null) toRemove.add(w);
    }

    // Versión genérica por compatibilidad
    public void add(Object o) {
        if (o != null) toAdd.add(o);
    }

    public void commit(Project proj, String actionKey) {
        if (toAdd.isEmpty() && toRemove.isEmpty()) return;

        CircuitMutation m = new CircuitMutation(circuit);
        for (Object o : toAdd) {
            if (o instanceof Component c) m.add(c);
            else if (o instanceof Wire w) m.add(w);
        }
        for (Object o : toRemove) {
            if (o instanceof Component c) m.remove(c);
            else if (o instanceof Wire w) m.remove(w);
        }

        if (!m.isEmpty()) {
            proj.doAction(m.toAction(Strings.getter(actionKey)));
        }

        toAdd.clear();
        toRemove.clear();
    }
}

