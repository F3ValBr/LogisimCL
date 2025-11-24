package com.cburch.logisim.verilog.comp.factories.gatelvl;

import com.cburch.logisim.verilog.comp.AbstractVerilogCellFactory;
import com.cburch.logisim.verilog.comp.VerilogCellFactory;
import com.cburch.logisim.verilog.comp.auxiliary.CellType;
import com.cburch.logisim.verilog.comp.impl.GateLvlCellImpl;
import com.cburch.logisim.verilog.comp.impl.VerilogCell;
import com.cburch.logisim.verilog.comp.specs.CellAttribs;
import com.cburch.logisim.verilog.comp.specs.CellParams;
import com.cburch.logisim.verilog.comp.specs.GenericCellAttribs;
import com.cburch.logisim.verilog.comp.specs.gatelvl.RegisterGateOpParams;
import com.cburch.logisim.verilog.comp.specs.gatelvl.RegisterGateUtils;

import java.util.List;
import java.util.Map;

public class RegisterGateOpFactory extends AbstractVerilogCellFactory implements VerilogCellFactory {
    @Override
    public VerilogCell create(
            String name,
            String type,
            Map<String, String> params,
            Map<String, Object> attribs,
            Map<String, String> ports,
            Map<String, List<Object>> connections
    ) {
        // 1) Parseo robusto del tipo
        RegisterGateUtils.RegGateConfig cfg = RegisterGateUtils.FFNameParser.parse(type);
        if (cfg == null) throw new IllegalArgumentException("Cannot parse FF type: " + type);

        // 2) Params/Attribs estándar
        CellParams parameters = new RegisterGateOpParams(cfg);
        CellAttribs attributes = new GenericCellAttribs(attribs);

        // 3) Instancia “genérica” de FF
        GateLvlCellImpl cell = new GateLvlCellImpl(
                name,
                CellType.fromYosys(type),
                parameters,
                attributes
        );

        // 4) Endpoints
        buildEndpoints(cell, ports, connections);

        return cell;
    }
}
