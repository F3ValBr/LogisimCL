package com.cburch.logisim.std.memory;

import com.cburch.logisim.data.*;
import com.cburch.logisim.instance.*;
import com.cburch.logisim.util.GraphicsUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class Timer extends InstanceFactory {

    public static final String _ID = "Timer";

    // Modo de medición: real vs virtual (clock externo)
    private static final AttributeOption MODE_REAL =
            new AttributeOption("real", "real", Strings.getter("timerModeReal"));
    private static final AttributeOption MODE_VIRTUAL =
            new AttributeOption("virtual", "virtual", Strings.getter("timerModeVirtual"));

    // Atributo para elegir modo
    public static final Attribute<AttributeOption> ATTR_MODE =
            Attributes.forOption("mode", Strings.getter("timerModeAttr"),
                    new AttributeOption[]{ MODE_REAL, MODE_VIRTUAL });

    // Índices lógicos de puertos
    private static final int OUT_TIME = 0; // salida T[31:0]
    private static final int IN_SEL   = 1; // entrada “select / trigger”
    private static final int IN_CLK   = 2; // entrada “clock” (sólo existe en modo virtual)

    private static final BitWidth BW32 = BitWidth.create(32);

    public Timer() {
        super(_ID, Strings.getter("timerComponent"));
        setOffsetBounds(Bounds.create(-30, -20, 30, 40));
        setIconName("timer.gif"); // opcional

        setAttributes(
                new Attribute[] {
                        StdAttr.FACING,
                        ATTR_MODE,
                        StdAttr.LABEL,
                        StdAttr.LABEL_FONT
                },
                new Object[] {
                        Direction.EAST,
                        MODE_REAL,
                        "",
                        StdAttr.DEFAULT_LABEL_FONT
                }
        );
        setFacingAttribute(StdAttr.FACING);

        // Definimos algo por defecto; luego cada instancia se ajusta en configureNewInstance
        Port[] ps = new Port[3];
        ps[OUT_TIME] = new Port(  0,   0, Port.OUTPUT, 32);
        ps[IN_SEL]   = new Port(-30,   0, Port.INPUT,  1);
        ps[IN_CLK]   = new Port(-30,  10, Port.INPUT,  1);
        setPorts(ps);
    }

    /* ==================== PUERTOS DINÁMICOS ==================== */

    @Override
    protected void configureNewInstance(Instance instance) {
        // Ajustar puertos según modo inicial
        recomputePorts(instance);

        // Campo de texto para label
        Bounds bds = instance.getBounds();
        instance.setTextField(StdAttr.LABEL, StdAttr.LABEL_FONT,
                bds.getX() + bds.getWidth() / 2, bds.getY() - 3,
                GraphicsUtil.H_CENTER, GraphicsUtil.V_BASELINE);

        // Escuchar cambios de atributos (p.ej. cambio de modo)
        instance.addAttributeListener();
    }

    @Override
    public void instanceAttributeChanged(Instance instance, Attribute<?> attr) {
        if (attr == ATTR_MODE || attr == StdAttr.FACING) {
            // Cambia el modo → reconstruir puertos (añadir/quitar CK)
            recomputePorts(instance);
        }
    }

    private void recomputePorts(Instance instance) {
        AttributeOption mode = instance.getAttributeValue(ATTR_MODE);

        List<Port> list = new ArrayList<>();

        // 0: OUT_TIME (T)
        Port pOut = new Port(  0,   0, Port.OUTPUT, 32);
        pOut.setToolTip(Strings.getter("timerOutTip"));
        list.add(pOut);

        // 1: IN_SEL (S)
        Port pSel = new Port(-30,   0, Port.INPUT, 1);
        pSel.setToolTip(Strings.getter("timerSelTip"));
        list.add(pSel);

        // 2: IN_CLK (CK) SOLO si modo virtual
        if (mode == MODE_VIRTUAL) {
            Port pClk = new Port(-30, 10, Port.INPUT, 1);
            pClk.setToolTip(Strings.getter("timerClkTip"));
            list.add(pClk);
        }

        instance.setPorts(list.toArray(new Port[0]));
        instance.recomputeBounds();

        // Recolocar label según nuevos bounds
        Bounds bds = instance.getBounds();
        instance.setTextField(StdAttr.LABEL, StdAttr.LABEL_FONT,
                bds.getX() + bds.getWidth() / 2, bds.getY() - 3,
                GraphicsUtil.H_CENTER, GraphicsUtil.V_BASELINE);
    }

    /* ==================== DIBUJO ==================== */

    @Override
    public void paintInstance(InstancePainter painter) {
        Graphics g = painter.getGraphics();
        Bounds b   = painter.getBounds();

        painter.drawBounds();
        painter.drawLabel();

        AttributeOption mode = painter.getAttributeValue(ATTR_MODE);

        // Puertos
        painter.drawPort(OUT_TIME, "T", Direction.WEST);
        g.setColor(Color.GRAY);
        painter.drawPort(IN_SEL,  "S", Direction.EAST);
        g.setColor(Color.BLACK);

        if (mode == MODE_VIRTUAL) {
            // Sólo existe si hemos creado el puerto CK en modo virtual
            painter.drawClock(IN_CLK, Direction.EAST);
        }

        // ==== Dibujar "reloj de arena" (X de esquina a esquina) ====
        int x1 = b.getX() + 4;
        int y1 = b.getY() + 4;
        int x2 = b.getX() + b.getWidth()  - 4;
        int y2 = b.getY() + b.getHeight() - 4;

        g.drawLine(x1, y1, x2, y2); // diagonal ↘
        g.drawLine(x1, y2, x2, y1); // diagonal ↗

        // Tag de modo ("R" o "V") al centro
        String tag = (mode == MODE_VIRTUAL) ? "V" : "R";
        g.setColor(Color.DARK_GRAY);
        GraphicsUtil.drawText(g, tag,
                b.getX() + b.getWidth() / 2,
                b.getY() + b.getHeight() / 8,
                GraphicsUtil.H_CENTER, GraphicsUtil.V_CENTER);
        g.setColor(Color.BLACK);
    }

    /* ==================== SIMULACIÓN ==================== */

    @Override
    public void propagate(InstanceState state) {
        TimerData data = (TimerData) state.getData();
        if (data == null) {
            data = new TimerData();
            state.setData(data);
        }

        AttributeOption mode = state.getAttributeValue(ATTR_MODE);

        // S siempre existe (índice 1)
        Value sel = state.getPort(IN_SEL);
        boolean selNow  = (sel == Value.TRUE);
        boolean selPrev = (data.lastSelect == Value.TRUE);
        boolean selRise = selNow && !selPrev;

        if (mode == MODE_VIRTUAL) {
            // ===== MODO VIRTUAL =====
            // CK existe sólo en modo virtual (índice 2)
            Value clk = state.getPort(IN_CLK);
            boolean clkNow  = (clk == Value.TRUE);
            boolean clkPrev = (data.lastClock == Value.TRUE);
            boolean clkRise = clkNow && !clkPrev;

            // Contamos flancos de subida de CK
            if (clkRise) {
                data.virtualTicks++;
            }

            // En flanco de S, entregamos la diferencia de ticks
            if (selRise) {
                long delta = data.virtualTicks - data.lastVirtualAtTrigger;
                if (delta < 0) delta = 0;
                int out = (int) delta; // recorte a 32 bits
                state.setPort(OUT_TIME, Value.createKnown(BW32, out), 0);
                data.lastVirtualAtTrigger = data.virtualTicks;
            }

            data.lastClock = clk;

        } else {
            // ===== MODO REAL =====
            long now = System.currentTimeMillis();

            if (selRise) {
                long delta;
                if (data.lastRealTime == 0L) {
                    // Primera medición: dejamos 0
                    delta = 0L;
                } else {
                    delta = now - data.lastRealTime;
                    if (delta < 0) delta = 0;
                }
                int out = (int) delta;
                state.setPort(OUT_TIME, Value.createKnown(BW32, out), 0);
                data.lastRealTime = now;
            } else {
                // Si quieres, puedes usar esto como “inicio” al pasar a TRUE sin flanco previo
                if (data.lastRealTime == 0L && selNow) {
                    data.lastRealTime = now;
                }
            }
        }

        data.lastSelect = sel;
    }
}
