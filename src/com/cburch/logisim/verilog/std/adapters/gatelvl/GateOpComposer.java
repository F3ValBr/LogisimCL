package com.cburch.logisim.verilog.std.adapters.gatelvl;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.verilog.comp.impl.VerilogCell;
import com.cburch.logisim.verilog.comp.specs.gatelvl.GateOp;
import com.cburch.logisim.verilog.comp.specs.gatelvl.GateOpParams;
import com.cburch.logisim.verilog.std.InstanceHandle;
import com.cburch.logisim.verilog.std.adapters.BaseComposer;
import com.cburch.logisim.verilog.std.macrocomponents.ComposeCtx;
import com.cburch.logisim.verilog.std.macrocomponents.MacroSubcktKit;

import java.util.List;
import java.util.function.BiConsumer;

import static com.cburch.logisim.verilog.std.adapters.ComponentComposer.attrsWithWidthAndLabel;


public class GateOpComposer extends BaseComposer {
    private final MacroSubcktKit sub = new MacroSubcktKit();

    public InstanceHandle buildAOI3AsSubckt(ComposeCtx ctx, VerilogCell cell, Location where) {
        require(ctx.fx.andF, "AND Gate"); require(ctx.fx.orF, "OR Gate"); require(ctx.fx.notF, "NOT Gate");
        final String name = MacroSubcktKit.macroName("aoi3");

        GateOpParams p = new GateOpParams(GateOp.AOI3, cell.params().asMap());

        BiConsumer<ComposeCtx, Circuit> populate = (in, macro) -> {
            Location andLoc = Location.create(200,120);

            Component pinA = addPin(in, "A", false, 1, andLoc.translate(-40, -10));
            Component pinB = addPin(in, "B", false, 1, andLoc.translate(-40, 10));
            Component pinC = addPin(in, "C", false, 1, andLoc.translate(-40, 30));
            Component pinY = addPin(in, "Y", true, 1, andLoc.translate(90, 10));

            Component and = add(in, ctx.fx.andF, andLoc,
                    attrsWithWidthAndLabel(ctx.fx.andF, 1, "AND"));

            addWire(in, pinA.getLocation(), and.getLocation().translate(-30, -10));
            addWire(in, pinB.getLocation(), and.getLocation().translate(-30, 10));

            Location orLoc = Location.create(and.getLocation().getX() + 40, and.getLocation().getY() + 10);

            Component or = add(in, ctx.fx.orF, orLoc,
                    attrsWithWidthAndLabel(ctx.fx.orF, 1, "OR"));

            addWire(in, and.getLocation(), and.getLocation().translate(10, 0));
            addWire(in, pinC.getLocation(), pinC.getLocation().translate(40, 0));
            addWire(in, pinC.getLocation().translate(40, 0), or.getLocation().translate(-40, 10));
            addWire(in, or.getLocation().translate(-40, 10), or.getLocation().translate(-30, 10));

            Location notLoc = Location.create(or.getLocation().getX() + 40, or.getLocation().getY());

            Component not = add(in, ctx.fx.notF, notLoc,
                    attrsWithWidthAndLabel(ctx.fx.notF, 1, "NOT"));

            addWire(in, or.getLocation(), not.getLocation().translate(-30, 0));
            addWire(in, not.getLocation(), pinY.getLocation());
        };
        return sub.ensureAndInstantiate(ctx, name, populate, where, lbl(cell),
                List.of("A", "B", "C", "Y"));
    }

    public InstanceHandle buildAOI4AsSubckt(ComposeCtx ctx, VerilogCell cell, Location where) {
        require(ctx.fx.andF, "AND Gate"); require(ctx.fx.orF, "OR Gate"); require(ctx.fx.notF, "NOT Gate");
        final String name = MacroSubcktKit.macroName("aoi4");

        GateOpParams p = new GateOpParams(GateOp.AOI4, cell.params().asMap());

        BiConsumer<ComposeCtx, Circuit> populate = (in, macro) -> {

        };
        return sub.ensureAndInstantiate(ctx, name, populate, where, lbl(cell),
                List.of("A", "B", "C", "D", "Y"));
    }

    public InstanceHandle buildOAI3AsSubckt(ComposeCtx ctx, VerilogCell cell, Location where) {
        require(ctx.fx.andF, "AND Gate"); require(ctx.fx.orF, "OR Gate"); require(ctx.fx.notF, "NOT Gate");
        final String name = MacroSubcktKit.macroName("oai3");

        GateOpParams p = new GateOpParams(GateOp.OAI3, cell.params().asMap());

        BiConsumer<ComposeCtx, Circuit> populate = (in, macro) -> {
            Location orLoc = Location.create(200,120);

            Component pinA = addPin(in, "A", false, 1, orLoc.translate(-40, -10));
            Component pinB = addPin(in, "B", false, 1, orLoc.translate(-40, 10));
            Component pinC = addPin(in, "C", false, 1, orLoc.translate(-40, 30));
            Component pinY = addPin(in, "Y", true, 1, orLoc.translate(90, 10));

            Component or = add(in, ctx.fx.orF, orLoc,
                    attrsWithWidthAndLabel(ctx.fx.orF, 1, "OR"));

            addWire(in, pinA.getLocation(), or.getLocation().translate(-30, -10));
            addWire(in, pinB.getLocation(), or.getLocation().translate(-30, 10));

            Location andLoc = Location.create(or.getLocation().getX() + 40, or.getLocation().getY() + 10);

            Component and = add(in, ctx.fx.andF, andLoc,
                    attrsWithWidthAndLabel(ctx.fx.orF, 1, "AND"));

            addWire(in, or.getLocation(), or.getLocation().translate(10, 0));
            addWire(in, pinC.getLocation(), pinC.getLocation().translate(40, 0));
            addWire(in, pinC.getLocation().translate(40, 0), and.getLocation().translate(-40, 10));
            addWire(in, and.getLocation().translate(-40, 10), and.getLocation().translate(-30, 10));

            Location notLoc = Location.create(and.getLocation().getX() + 40, and.getLocation().getY());

            Component not = add(in, ctx.fx.notF, notLoc,
                    attrsWithWidthAndLabel(ctx.fx.notF, 1, "NOT"));

            addWire(in, and.getLocation(), not.getLocation().translate(-30, 0));
            addWire(in, not.getLocation(), pinY.getLocation());
        };
        return sub.ensureAndInstantiate(ctx, name, populate, where, lbl(cell),
                List.of("A", "B", "C", "Y"));
    }

    public InstanceHandle buildOAI4AsSubckt(ComposeCtx ctx, VerilogCell cell, Location where) {
        require(ctx.fx.andF, "AND Gate"); require(ctx.fx.orF, "OR Gate"); require(ctx.fx.notF, "NOT Gate");
        final String name = MacroSubcktKit.macroName("oai4");

        GateOpParams p = new GateOpParams(GateOp.OAI4, cell.params().asMap());

        BiConsumer<ComposeCtx, Circuit> populate = (in, macro) -> {

        };
        return sub.ensureAndInstantiate(ctx, name, populate, where, lbl(cell),
                List.of("A", "B", "C", "D", "Y"));
    }

    public InstanceHandle buildNMuxAsSubckt(ComposeCtx ctx, VerilogCell cell, Location where) {
        require(ctx.fx.andF, "Multiplexer"); require(ctx.fx.notF, "NOT Gate");
        final String name = MacroSubcktKit.macroName("nmux");

        GateOpParams p = new GateOpParams(GateOp.NMUX, cell.params().asMap());

        BiConsumer<ComposeCtx, Circuit> populate = (in, macro) -> {

        };
        return sub.ensureAndInstantiate(ctx, name, populate, where, lbl(cell),
                List.of("A", "B", "S", "Y"));

    }
}
