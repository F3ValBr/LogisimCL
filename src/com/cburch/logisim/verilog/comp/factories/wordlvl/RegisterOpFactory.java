package com.cburch.logisim.verilog.comp.factories.wordlvl;

import com.cburch.logisim.verilog.comp.AbstractVerilogCellFactory;
import com.cburch.logisim.verilog.comp.impl.VerilogCell;
import com.cburch.logisim.verilog.comp.VerilogCellFactory;
import com.cburch.logisim.verilog.comp.impl.WordLvlCellImpl;
import com.cburch.logisim.verilog.comp.auxiliary.CellType;
import com.cburch.logisim.verilog.comp.specs.RegisterAttribs;
import com.cburch.logisim.verilog.comp.specs.wordlvl.RegisterOp;
import com.cburch.logisim.verilog.comp.specs.wordlvl.RegisterOpParams;
import com.cburch.logisim.verilog.comp.specs.wordlvl.registerparams.*;
import com.cburch.logisim.verilog.comp.specs.wordlvl.registerparams.dffeparams.*;

import java.util.*;

/**
 * Factory for creating register operation Verilog cells.
 * Supports various register operations like DFF, DFFE, SDFF, etc.
 */
public class RegisterOpFactory extends AbstractVerilogCellFactory implements VerilogCellFactory {
    @Override
    public VerilogCell create(String name, String type,
                              Map<String,String> parameters,
                              Map<String,Object> attributes,
                              Map<String,String> portDirections,
                              Map<String, List<Object>> connections) {

        RegisterOp op = RegisterOp.fromYosys(type);

        // (Opcional) validación de parámetros según el op
        validateRequiredParams(op, type, parameters);

        RegisterOpParams params = getRegisterOpParams(op, parameters);

        var attribs = new RegisterAttribs(attributes);
        VerilogCell cell = new WordLvlCellImpl(name, CellType.fromYosys(type), params, attribs);

        buildEndpoints(cell, portDirections, connections);

        // Validaciones de puertos
        validatePorts(cell, op, params);

        return cell;
    }

    private static RegisterOpParams getRegisterOpParams(RegisterOp op, Map<String,String> parameters) {
        return switch (op) {
            // base
            case DFF    -> new DFFParams(parameters);
            case ADFF   -> new ADFFParams(parameters);
            case ALDFF  -> new AlDFFParams(parameters);
            case DFFSR  -> new DFFSRParams(parameters);
            case SDFF   -> new SDFFParams(parameters);

            // + enable
            case DFFE   -> new DFFEParams(parameters);
            case ADFFE  -> new ADFFEParams(parameters);
            case ALDFFE -> new AlDFFEParams(parameters);
            case DFFSRE -> new DFFSREParams(parameters);
            case SDFFE  -> new SDFFEParams(parameters);
            case SDFFCE -> new SDFFCEParams(parameters);

            default     -> new GenericRegisterParams(parameters); // fallback
        };
    }

    /* ==== VALIDACIONES ESPECÍFICAS ==== */

    private static void validateRequiredParams(RegisterOp op, String type,
                                               Map<String,String> params) {
        switch (op) {
            case DFF, ADFF, ALDFF, DFFSR, SDFF,
                 DFFE, ADFFE, ALDFFE, DFFSRE, SDFFE, SDFFCE ->
                    requireParams(type, params, "WIDTH");
            default -> { /* generic: no checks */ }
        }
    }

    private void validatePorts(VerilogCell cell, RegisterOp op, RegisterOpParams params) {
        int w = params.width();

        // -------- Validaciones comunes --------
        switch (op) {
            case SDFF, SDFFE, SDFFCE -> {
                requirePorts(cell, "CLK", "SRST", "D", "Q");
                requirePortWidth(cell, "CLK",  1);
                requirePortWidth(cell, "SRST", 1);
                requirePortWidth(cell, "D",    w);
                requirePortWidth(cell, "Q",    w);
            }

            // TODO: agregar aquí bloques extra para DFF / ADFF / etc.
            default -> { /* otros tipos pueden tener sus propias reglas */ }
        }

        // -------- Validaciones específicas de enable --------
        if (op == RegisterOp.SDFFE) {
            // EN o CE debe existir y ser de 1 bit
            String enPort = requireAnyPort(cell, "EN", "CE");
            requirePortWidth(cell, enPort, 1);
        }

        if (op == RegisterOp.SDFFCE) {
            String cePort = requireAnyPort(cell, "CE", "EN");
            requirePortWidth(cell, cePort, 1);
        }
    }
}
