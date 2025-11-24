package com.cburch.logisim.verilog.comp.factories.wordlvl;

import com.cburch.logisim.verilog.comp.AbstractVerilogCellFactory;
import com.cburch.logisim.verilog.comp.impl.VerilogCell;
import com.cburch.logisim.verilog.comp.VerilogCellFactory;
import com.cburch.logisim.verilog.comp.impl.WordLvlCellImpl;
import com.cburch.logisim.verilog.comp.auxiliary.CellType;
import com.cburch.logisim.verilog.comp.specs.CommonOpAttribs;
import com.cburch.logisim.verilog.comp.specs.wordlvl.MuxOp;
import com.cburch.logisim.verilog.comp.specs.wordlvl.MuxOpParams;
import com.cburch.logisim.verilog.comp.specs.wordlvl.muxparams.*;

import java.util.List;
import java.util.Map;

/**
 * Factory for creating multiplexer operation Verilog cells.
 * Supports various mux operations like MUX, PMUX, TRIBUF, BMUX, BWMUX, DEMUX.
 */
public class MuxOpFactory extends AbstractVerilogCellFactory implements VerilogCellFactory {

    @Override
    public VerilogCell create(
            String name,
            String type,
            Map<String, String> params,
            Map<String, Object> attribs,
            Map<String, String> ports,
            Map<String, List<Object>> connections
    ) {
        // 1) Classification by enum
        final MuxOp op = MuxOp.fromYosys(type);

        // 2) (Opcional) validación de parámetros requeridos por op
        validateRequiredParams(op, type, params);

        // 3) Specific parameters by op
        final MuxOpParams parameters = getMuxOpParams(op, params);

        // 4) Attribs + cell creation
        final CommonOpAttribs attributes = new CommonOpAttribs(attribs);
        final WordLvlCellImpl cell = new WordLvlCellImpl(
                name,
                CellType.fromYosys(type),
                parameters,
                attributes
        );

        // 5) Endpoints
        buildEndpoints(cell, ports, connections);

        // 6) Validaciones de puertos por op
        validatePorts(cell, op, parameters);

        return cell;
    }

    private static MuxOpParams getMuxOpParams(MuxOp op, Map<String, String> params) {
        return switch (op) {
            case MUX   -> new MuxParams(params);
            case PMUX  -> new PMuxParams(params);
            case TRIBUF-> new TribufParams(params);
            case BMUX  -> new BMuxParams(params);
            case BWMUX -> new BWMuxParams(params);
            case DEMUX -> new DemuxParams(params);
        };
    }

    /* ==== VALIDACIONES ESPECÍFICAS ==== */

    /** Parámetros obligatorios según el tipo de mux (ejemplo, adapta a tu realidad). */
    private static void validateRequiredParams(MuxOp op, String type, Map<String, String> params) {
        // Ojo: solo si de verdad necesitas chequear explícitamente
        switch (op) {
            case MUX, TRIBUF, BMUX, BWMUX, DEMUX ->
                    requireParams(type, params, "WIDTH");
            case PMUX ->
                    requireParams(type, params, "WIDTH", "S_WIDTH"); // ejemplo
        }
    }

    private void validatePorts(VerilogCell cell, MuxOp op, MuxOpParams parameters) {
        final int w = parameters.width();

        switch (op) {
            case MUX -> {
                // Puertos básicos
                requirePorts(cell, "A", "B", "Y", "S");
                // Anchos
                requirePortWidth(cell, "A", w);
                requirePortWidth(cell, "B", w);
                requirePortWidth(cell, "Y", w);
                requirePortWidth(cell, "S", 1);
            }
            case PMUX -> {
                PMuxParams p = (PMuxParams) parameters;
                requirePorts(cell, "A", "B", "Y", "S");
                requirePortWidth(cell, "A", w);
                requirePortWidth(cell, "Y", w);
                requirePortWidth(cell, "S", p.sWidth());
                requirePortWidth(cell, "B", p.bTotalWidth());
            }
            case TRIBUF -> {
                requirePorts(cell, "A", "Y", "EN");
                requirePortWidth(cell, "A", w);
                requirePortWidth(cell, "Y", w);
                requirePortWidth(cell, "EN", 1);
            }
            case BMUX -> {
                requirePorts(cell, "A", "Y");
                requirePortWidth(cell, "A", w);
                requirePortWidth(cell, "Y", w);
                // TODO: validar S según BMuxParams
            }
            case BWMUX -> {
                // TODO: validar puertos y anchos según BWMuxParams
            }
            case DEMUX -> {
                DemuxParams p = (DemuxParams) parameters;
                requirePorts(cell, "A", "Y", "S");
                requirePortWidth(cell, "A", w);
                requirePortWidth(cell, "S", p.sWidth());
                // requirePortWidth(cell, "Y", w * (1 << p.sWidth()));
            }
        }
    }
}
