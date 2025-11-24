package com.cburch.logisim.verilog.comp;

import com.cburch.logisim.verilog.comp.auxiliary.PortEndpoint;
import com.cburch.logisim.verilog.comp.auxiliary.PortSignature;
import com.cburch.logisim.verilog.comp.auxiliary.netconn.*;
import com.cburch.logisim.verilog.comp.impl.VerilogCell;

import java.util.*;

public abstract class AbstractVerilogCellFactory implements VerilogCellFactory {

    protected int parseBin(String s) {
        if (s == null || s.isEmpty()) return 0;
        return Integer.parseUnsignedInt(s, 2);
    }

    protected PortDirection toDirection(String d) {
        if (d == null) return PortDirection.UNKNOWN;
        return switch (d.toLowerCase()) {
            case "input" -> PortDirection.INPUT;
            case "output" -> PortDirection.OUTPUT;
            case "inout"  -> PortDirection.INOUT;
            default       -> PortDirection.UNKNOWN;
        };
    }

    /** Convert element from JSON (Integer or "0"/"1") to BitRef
     *
     * @param raw The raw value to convert, can be Integer or String.
     */
    protected BitRef toBitRef(Object raw) {
        if (raw instanceof Integer i) return new NetBit(i);
        if (raw instanceof String s) {
            return switch (s) {
                case "0" -> Const0.getInstance();
                case "1" -> Const1.getInstance();
                case "x" -> ConstX.getInstance();
                case "z" -> ConstZ.getInstance();
                default  -> throw new IllegalArgumentException("Bit desconocido: " + raw);
            };
        }
        throw new IllegalArgumentException("Bit no soportado: " + raw);
    }

    /** Creates PortEndpoint objects for each port in the cell.
     *
     * @param cell The VerilogCell to which the endpoints belong.
     * @param portDirections Map of port names to their directions (INPUT/OUTPUT/INOUT).
     * @param connections Map of port names to lists of bits (NetBit, Const0, Const1).
     */
    protected void buildEndpoints(
            VerilogCell cell,
            Map<String, String> portDirections,
            Map<String, List<Object>> connections
    ) {
        // Unir nombres de puertos presentes en directions y/o en connections
        Set<String> portNames = new LinkedHashSet<>();
        portNames.addAll(portDirections.keySet());
        portNames.addAll(connections.keySet());

        for (String portName : portNames) {
            PortDirection dir = toDirection(portDirections.get(portName)); // UNKNOWN si falta o inválido
            List<Object> rawBits = connections.getOrDefault(portName, List.of());

            PortSignature signature = new PortSignature(
                    cell,       // celda dueña del puerto
                    portName,   // nombre del puerto
                    dir         // dirección del puerto (INPUT/OUTPUT/INOUT/UNKNOWN)
            );

            // Endpoints bit a bit (índice == posición en la lista; LSB primero)
            for (int i = 0; i < rawBits.size(); i++) {
                BitRef bit = toBitRef(rawBits.get(i));

                PortEndpoint ep = new PortEndpoint(
                        signature,   // PortSignature (celda, nombre de puerto, dirección)
                        i,           // índice del bit en el bus (LSB = 0)
                        bit          // BitRef (NetBit | Const0 | Const1)
                );
                cell.addPortEndpoint(ep);
            }
        }
    }

    /* ==== VALIDATIONS: widths y presencia ==== */

    /** Checker for port expected widths */
    protected static void requirePortWidth(VerilogCell cell, String port, int expected) {
        int got = cell.portWidth(port);
        if (got != expected) {
            throw new IllegalStateException(cell.name() + ": port " + port +
                    " width mismatch. expected=" + expected + " got=" + got);
        }
    }

    /** Igual que requirePortWidth, pero solo si el puerto existe. */
    protected static void requirePortWidthOptional(VerilogCell cell, String port, int expected) {
        if (hasPort(cell, port)) {
            requirePortWidth(cell, port, expected);
        }
    }

    protected static boolean hasPort(VerilogCell cell, String port) {
        return cell.getPortNames().contains(port);
    }

    /** Exige que existan todos los puertos listados. */
    protected static void requirePorts(VerilogCell cell, String... ports) {
        for (String p : ports) {
            if (!hasPort(cell, p)) {
                throw new IllegalStateException(cell.name() + ": missing required port '" + p + "'");
            }
        }
    }

    /**
     * Exige que exista al menos UN puerto de la lista.
     * Devuelve el nombre del puerto encontrado.
     */
    protected static String requireAnyPort(VerilogCell cell, String... alternatives) {
        for (String p : alternatives) {
            if (hasPort(cell, p)) {
                return p;
            }
        }
        throw new IllegalStateException(
                cell.name() + ": missing required port among alternatives " +
                        Arrays.toString(alternatives)
        );
    }

    /**
     * Exige que existan ciertos parámetros en el mapa de parámetros.
     * cellLabel es solo para mejorar el mensaje (puede ser type o name).
     */
    protected static void requireParams(String cellLabel,
                                        Map<String, String> params,
                                        String... keys) {
        for (String k : keys) {
            if (params == null || !params.containsKey(k)) {
                throw new IllegalStateException(
                        cellLabel + ": missing required parameter '" + k + "'"
                );
            }
        }
    }
}
