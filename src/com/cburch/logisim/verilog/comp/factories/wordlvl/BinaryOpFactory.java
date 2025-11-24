package com.cburch.logisim.verilog.comp.factories.wordlvl;

import com.cburch.logisim.verilog.comp.*;
import com.cburch.logisim.verilog.comp.auxiliary.CellType;
import com.cburch.logisim.verilog.comp.impl.VerilogCell;
import com.cburch.logisim.verilog.comp.impl.WordLvlCellImpl;
import com.cburch.logisim.verilog.comp.specs.CommonOpAttribs;
import com.cburch.logisim.verilog.comp.specs.wordlvl.BinaryOp;
import com.cburch.logisim.verilog.comp.specs.wordlvl.BinaryOpParams;

import java.util.List;
import java.util.Map;

/**
 * Factory class for creating binary operation Verilog cells.
 * Supports operations like AND, OR, XOR, ADD, SUB, etc.
 */
public class BinaryOpFactory extends AbstractVerilogCellFactory {
    @Override
    public VerilogCell create(
            String name,
            String type,
            Map<String, String> params,
            Map<String, Object> attribs,
            Map<String, String> ports,
            Map<String, List<Object>> connections
    ) {
        BinaryOp op = BinaryOp.fromYosys(type);

        // 1) Validación de parámetros requeridos según op
        validateRequiredParams(op, type, params);

        // 2) Construcción de parámetros y atributos
        BinaryOpParams parameters = new BinaryOpParams(op, params);
        CommonOpAttribs attributes = new CommonOpAttribs(attribs);

        // 3) Crear celda
        WordLvlCellImpl cell = new WordLvlCellImpl(
                name,
                CellType.fromYosys(type),
                parameters,
                attributes
        );

        // 4) Endpoints (puertos + conexiones)
        buildEndpoints(cell, ports, connections);

        // 5) Validaciones de puertos/ancho según tipo de operación
        validatePorts(cell, op, parameters);

        return cell;
    }

    /* ============================
       VALIDACIONES DE PARÁMETROS
       ============================ */

    private static void validateRequiredParams(BinaryOp op,
                                               String type,
                                               Map<String, String> params) {
        switch (op) {
            // Operaciones básicas palabra-a-palabra
            case ADD, SUB, AND, OR, XOR, XNOR,
                 MUL, SHL, SHR, SSHR -> {
                // Normalmente al menos Y_WIDTH
                requireParams(type, params, "Y_WIDTH");
            }

            // Comparadores: a veces Y_WIDTH también es param, pero
            // casi siempre Y es de 1 bit.
            case EQ, NE, LT, LE, GT, GE -> {
                requireParams(type, params, "Y_WIDTH");
            }

            default -> {
                // en caso de haber op rara, ver lógica aqui
            }
        }
    }

    /* ============================
       VALIDACIONES DE PUERTOS
       ============================ */

    private void validatePorts(VerilogCell cell,
                               BinaryOp op,
                               BinaryOpParams params) {

        int w = params.yWidth();  // ancho típico de Y

        switch (op) {
            /* ==== Operaciones aritméticas/lógicas estándar ==== */
            case ADD, SUB, AND, OR, XOR, XNOR, MUL -> {
                // Puertos mínimos
                requirePorts(cell, "A", "B", "Y");
                requirePortWidth(cell, "Y", w);
            }

            /* ==== Comparadores ==== */
            case EQ, NE, LT, LE, GT, GE -> {
                requirePorts(cell, "A", "B", "Y");
                // Casi siempre Y es 1 bit
                requirePortWidth(cell, "Y", 1);
            }

            /* ==== Shifts ==== */
            case SHL, SHR, SSHR -> {
                requirePorts(cell, "A", "B", "Y");
                requirePortWidth(cell, "Y", w);
            }

            default -> {
                // validar aqui mas puertos
            }
        }
    }
}


