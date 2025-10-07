package com.cburch.logisim.verilog.file.importer;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.data.*;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.file.LogisimFileActions;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.std.wiring.Constant;
import com.cburch.logisim.verilog.comp.auxiliary.LogicalMemory;
import com.cburch.logisim.verilog.comp.auxiliary.ModulePort;
import com.cburch.logisim.verilog.comp.auxiliary.netconn.BitRef;
import com.cburch.logisim.verilog.comp.auxiliary.netconn.Const0;
import com.cburch.logisim.verilog.comp.impl.VerilogCell;
import com.cburch.logisim.verilog.comp.impl.VerilogModuleImpl;
import com.cburch.logisim.verilog.layout.MemoryIndex;
import com.cburch.logisim.verilog.layout.ModuleNetIndex;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.cburch.logisim.verilog.file.importer.VerilogJsonImporter.GRID;
import static com.cburch.logisim.verilog.std.AbstractComponentAdapter.setParsedByName;

public class ImporterUtils {
    static Circuit ensureCircuit(Project proj, String name) {
        LogisimFile file = proj.getLogisimFile();
        for (Circuit c : file.getCircuits()) {
            if (c.getName().equals(name)) return c;
        }

        Circuit c = new Circuit(name);
        proj.doAction(LogisimFileActions.addCircuit(c));
        return c;
    }

    static int snap(int v) { return (v/GRID)*GRID; }

    /* =========================
       Helpers de impresión
       ========================= */

    static void printModulePorts(VerilogModuleImpl mod) {
        if (mod.ports().isEmpty()) {
            System.out.println("  (sin puertos de módulo)");
            return;
        }
        System.out.println("  Puertos:");
        for (ModulePort p : mod.ports()) {
            String bits = Arrays.stream(p.netIds())
                    .mapToObj(i -> i == ModulePort.CONST_0 ? "0" :
                            i == ModulePort.CONST_1 ? "1" :
                                    i == ModulePort.CONST_X ? "x" : String.valueOf(i))
                    .collect(Collectors.joining(","));
            System.out.println("    - " + p.name() + " : " + p.direction()
                    + " [" + p.width() + "]  bits={" + bits + "}");
        }
    }

    static void printNets(VerilogModuleImpl mod, ModuleNetIndex idx) {
        System.out.println("  Nets:");
        for (int netId : idx.netIds()) {
            int[] refs = idx.endpointsOf(netId).stream().mapToInt(i -> i).toArray();

            var topStrs  = new ArrayList<String>();
            var cellStrs = new ArrayList<String>();

            for (int ref : refs) {
                int bit = ModuleNetIndex.bitIdx(ref);
                if (ModuleNetIndex.isTop(ref)) {
                    int portIdx = ModuleNetIndex.ownerIdx(ref);
                    ModulePort p = mod.ports().get(portIdx);
                    topStrs.add(p.name() + "[" + bit + "]");
                } else {
                    int cellIdx = ModuleNetIndex.ownerIdx(ref);
                    VerilogCell c = mod.cells().get(cellIdx);
                    cellStrs.add(c.name() + "[" + bit + "]");
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append("    net ").append(netId).append(": ");
            if (!topStrs.isEmpty())  sb.append("top=").append(topStrs).append(" ");
            if (!cellStrs.isEmpty()) sb.append("cells=").append(cellStrs);
            System.out.println(sb);
        }
    }

    static void printMemories(MemoryIndex memIndex) {
        var all = memIndex.memories();
        if (all == null || all.isEmpty()) return;

        System.out.println("  Memories:");
        for (LogicalMemory lm : all) {
            String meta = (lm.meta() == null)
                    ? ""
                    : (" width=" + lm.meta().width()
                    + " size=" + lm.meta().size()
                    + " offset=" + lm.meta().startOffset());

            System.out.println("    - MEMID=" + lm.memId()
                    + " arrayCellIdx=" + (lm.arrayCellIdx() < 0 ? "-" : lm.arrayCellIdx())
                    + " rdPorts=" + lm.readPortIdxs().size()
                    + " wrPorts=" + lm.writePortIdxs().size()
                    + " inits=" + lm.initIdxs().size()
                    + meta);
        }
    }

    /* ===== utilidades para Constant (copias seguras de tus helpers) ===== */

    /** Devuelve "0"/"1" si el BitRef es Const0/Const1; "x"/"z" si lo son; null si no es constante. */
    static String constKind(BitRef br) {
        if (br == null) return null;
        if (br instanceof Const0) return "0";
        String n = br.getClass().getSimpleName();
        return switch (n) {
            case "Const1" -> "1";
            case "ConstX" -> "x";
            case "ConstZ" -> "z";
            default -> null;
        };
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    static void setConstantValueFlexible(AttributeSet a, int width, int value) {
        int mask = (width >= 32) ? -1 : ((1 << Math.max(0, width)) - 1);
        int masked = value & mask;

        try {
            Attribute attr = Constant.ATTR_VALUE; // Integer en muchos builds
            a.setValue(attr, Integer.valueOf(masked));
            return;
        } catch (Throwable ignore) { }

        try {
            Attribute attr = Constant.ATTR_VALUE; // Value en otros builds
            Value val = Value.createKnown(BitWidth.create(width), masked);
            a.setValue(attr, val);
            return;
        } catch (Throwable ignore) { }

        String hex = "0x" + Integer.toHexString(masked);
        setParsedByName(a, "value", hex);
    }
}
