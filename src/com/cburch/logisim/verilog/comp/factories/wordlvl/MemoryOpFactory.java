package com.cburch.logisim.verilog.comp.factories.wordlvl;

import com.cburch.logisim.verilog.comp.AbstractVerilogCellFactory;
import com.cburch.logisim.verilog.comp.impl.VerilogCell;
import com.cburch.logisim.verilog.comp.VerilogCellFactory;
import com.cburch.logisim.verilog.comp.impl.WordLvlCellImpl;
import com.cburch.logisim.verilog.comp.auxiliary.CellType;
import com.cburch.logisim.verilog.comp.specs.GenericCellAttribs;
import com.cburch.logisim.verilog.comp.specs.wordlvl.MemoryOp;
import com.cburch.logisim.verilog.comp.specs.wordlvl.MemoryOpParams;
import com.cburch.logisim.verilog.comp.specs.wordlvl.memoryparams.memarrayparams.MemParams;
import com.cburch.logisim.verilog.comp.specs.wordlvl.memoryparams.memarrayparams.MemV2Params;
import com.cburch.logisim.verilog.comp.specs.wordlvl.memoryparams.meminitparams.MemInitParams;
import com.cburch.logisim.verilog.comp.specs.wordlvl.memoryparams.meminitparams.MemInitV2Params;
import com.cburch.logisim.verilog.comp.specs.wordlvl.memoryparams.memreadparams.MemRDParams;
import com.cburch.logisim.verilog.comp.specs.wordlvl.memoryparams.memreadparams.MemRDV2Params;
import com.cburch.logisim.verilog.comp.specs.wordlvl.memoryparams.memwriteparams.MemWRParams;
import com.cburch.logisim.verilog.comp.specs.wordlvl.memoryparams.memwriteparams.MemWRV2Params;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Factory for creating memory operation Verilog cells.
 * Supports various memory operations like $mem, $mem_v2, $meminit, $memrd, $memwr, etc.
 */
public class MemoryOpFactory extends AbstractVerilogCellFactory implements VerilogCellFactory {

    @Override
    public VerilogCell create(String name,
                              String type,
                              Map<String, String> parameters,
                              Map<String, Object> attributes,
                              Map<String, String> portDirections,
                              Map<String, List<Object>> connections) {

        final MemoryOp op = MemoryOp.fromYosys(type);
        final CellType ct = CellType.fromYosys(type);
        final GenericCellAttribs attribs = new GenericCellAttribs(attributes);

        // 0) Normalizaciones previas a construir endpoints
        /* fixme: esta función corrige el detalle de que un puerto WR_EN sea multibit en el json, pero esta medio hardcodeada corregir en la medida de lo posible, hace lo que debe hacer por ahora */
        normalizeWriteEnable(op, connections);

        // 1) Specific params by type
        final MemoryOpParams params = newParams(op, parameters);

        // 2) Cell creation
        final VerilogCell cell = newCell(name, ct, params, attribs);

        // 3) Endpoints
        buildEndpoints(cell, portDirections, connections);

        // 4) Validations
        validatePorts(cell, op, params);

        return cell;
    }

    /* ============================
       Normalizaciones
       ============================ */

    /**
     * Colapsa señales de write-enable por-bit a 1 bit cuando corresponde,
     * preservando el tipo de los elementos (Integer para nets, "0"/"1" para constantes).
     * - $mem / $mem_v2 usan "WR_EN" (ancho WIDTH*WR_PORTS)
     * - $memwr / $memwr_v2 usan "EN"   (ancho WIDTH)
     */
    private static void normalizeWriteEnable(MemoryOp op, Map<String, List<Object>> conns) {
        if (conns == null) return;

        switch (op) {
            case MEM, MEM_V2 -> collapseKeyToSingleBit(conns, "WR_EN");
            case MEMWR, MEMWR_V2 -> collapseKeyToSingleBit(conns, "EN");
            default -> { /* no-op */ }
        }
    }

    /**
     * Reduce una lista de bits a un único bit:
     * - Si la lista está vacía → "0"
     * - Si todos los elementos son exactamente iguales → ese mismo objeto
     * - Si todos son constantes "0"/"1" → OR lógico → "1" si hay algún "1", si no "0"
     * - Mezcla (nets + constantes, etc.) → toma el primero tal cual (degradación aceptable)
     * Siempre deja el resultado como una lista de un solo elemento, sin cambiar tipos.
     */
    private static void collapseKeyToSingleBit(Map<String, List<Object>> conns, String key) {
        if (!conns.containsKey(key)) return;

        List<Object> lst = conns.get(key);
        if (lst == null || lst.isEmpty()) {
            conns.put(key, List.of("0"));
            return;
        }

        // ¿todos exactamente iguales?
        boolean allSame = true;
        Object first = lst.get(0);
        for (int i = 1; i < lst.size(); i++) {
            if (!Objects.equals(first, lst.get(i))) { allSame = false; break; }
        }
        if (allSame) {
            conns.put(key, List.of(first));
            return;
        }

        // ¿todas constantes 0/1? -> OR
        boolean allConst01 = true;
        boolean anyOne = false;
        for (Object o : lst) {
            if (o instanceof String s) {
                String t = s.trim();
                if ("1".equals(t)) anyOne = true;
                if (!"0".equals(t) && !"1".equals(t)) { allConst01 = false; break; }
            } else {
                allConst01 = false; break;
            }
        }
        if (allConst01) {
            conns.put(key, List.of(anyOne ? "1" : "0"));
            return;
        }

        // Mezcla de nets y/o constantes no puras → tomar el primero preservando el tipo
        conns.put(key, List.of(first));
    }

    /* ============================
       Build helpers
       ============================ */

    private MemoryOpParams newParams(MemoryOp op, Map<String, String> parameters) {
        return switch (op) {
            case MEM        -> new MemParams(parameters);      // $mem (v1)
            case MEM_V2     -> new MemV2Params(parameters);    // $mem_v2
            case MEMINIT    -> new MemInitParams(parameters);
            case MEMINIT_V2 -> new MemInitV2Params(parameters);
            case MEMRD      -> new MemRDParams(parameters);
            case MEMRD_V2   -> new MemRDV2Params(parameters);
            case MEMWR      -> new MemWRParams(parameters);
            case MEMWR_V2   -> new MemWRV2Params(parameters);
        };
    }

    /** Creates a new WordLvlCellImpl with the given parameters.
     *
     * @param name Name of the cell
     * @param ct CellType of the cell
     * @param params Specific parameters for the memory operation
     * @param attribs Generic attributes for the cell
     * @return A new VerilogCell instance with the specified configuration
     */
    private VerilogCell newCell(String name, CellType ct, MemoryOpParams params, GenericCellAttribs attribs) {
        return new WordLvlCellImpl(name, ct, params, attribs);
    }

    /* ============================
       Validations by type
       ============================ */

    private void validatePorts(VerilogCell cell, MemoryOp op, MemoryOpParams p) {
        switch (op) {
            case MEM, MEM_V2 -> validateMemArray(cell, p);
            case MEMRD       -> validateMemRd(cell, p, false);
            case MEMRD_V2    -> validateMemRd(cell, p, true);
            case MEMWR, MEMWR_V2 -> validateMemWr(cell, p);
            case MEMINIT, MEMINIT_V2 -> { /* Only params, no wiring */ }
            default -> throw new IllegalArgumentException("MemoryOpFactory: unsupported type: " + op);
        }
    }

    private void validateMemArray(VerilogCell cell, MemoryOpParams p) {
        requirePortWidthOptional(cell, "RD_ADDR", p.abits() * p.rdPorts());
        requirePortWidthOptional(cell, "RD_DATA", p.width()  * p.rdPorts());
        requirePortWidthOptional(cell, "WR_ADDR", p.abits() * p.wrPorts());
        requirePortWidthOptional(cell, "WR_DATA", p.width()  * p.wrPorts());
        // Tras la normalización, si existe WR_EN debe ser de 1 bit por puerto de escritura real.
        if (hasPort(cell, "WR_EN")) {
            requirePortWidth(cell, "WR_EN", 1);
        }
        // TODO: check buses organization (contiguous, same order)
    }

    private void validateMemRd(VerilogCell cell, MemoryOpParams p, boolean v2) {
        requirePortWidth(cell, "ADDR", p.abits());
        requirePortWidth(cell, "DATA", p.width());
        if (hasPort(cell, "EN"))  requirePortWidth(cell, "EN", 1);
        if (p.clkEnable() && hasPort(cell, "CLK")) requirePortWidth(cell, "CLK", 1);
        if (v2) { // only v2 exposes ARST/SRST
            if (hasPort(cell, "ARST")) requirePortWidth(cell, "ARST", 1);
            if (hasPort(cell, "SRST")) requirePortWidth(cell, "SRST", 1);
        }
    }

    private void validateMemWr(VerilogCell cell, MemoryOpParams p) {
        requirePortWidth(cell, "ADDR", p.abits());
        requirePortWidth(cell, "DATA", p.width());
        if (hasPort(cell, "EN")) requirePortWidth(cell, "EN", 1);
        if (p.clkEnable() && hasPort(cell, "CLK")) requirePortWidth(cell, "CLK", 1);
    }

    private static void requirePortWidth(VerilogCell cell, String port, int expected) {
        int got = cell.portWidth(port);
        if (got != expected) {
            throw new IllegalStateException(cell.name() + ": port " + port +
                    " width mismatch. expected=" + expected + " got=" + got);
        }
    }

    private static void requirePortWidthOptional(VerilogCell cell, String port, int expected) {
        if (hasPort(cell, port)) requirePortWidth(cell, port, expected);
    }

    private static boolean hasPort(VerilogCell cell, String port) {
        return cell.getPortNames().contains(port);
    }
}
