package com.cburch.logisim.verilog.file.ui;

import com.cburch.logisim.proj.Project;

import javax.swing.*;

// ===== 3) Capa de UI (separada) =====
public final class NameConflictUI {

    // Resultado devuelto a ensureCircuit
    public record NameConflictResult(Choice choice, String suggestedName) { }

    public enum Choice { REPLACE, CREATE_NEW, CANCEL }

    public static NameConflictResult askUser(Project proj, String baseName) {
        java.awt.Component parent = (proj != null && proj.getFrame() != null) ? proj.getFrame() : null;

        String msg = "Ya existe un módulo o circuito llamado '" + baseName + "'.\n\n"
                + "¿Qué deseas hacer?";
        String[] options = { "Reemplazar existente", "Crear nuevo (_new)", "Cancelar" };

        int sel = JOptionPane.showOptionDialog(
                parent,
                msg,
                "Conflicto de nombre de módulo",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        return switch (sel) {
            case 0 -> new NameConflictResult(Choice.REPLACE, null);
            case 1 -> {
                // Sugerimos baseName_new; ensureCircuit lo hará único con makeUniqueName(...)
                String suggested = baseName + "_new";
                yield new NameConflictResult(Choice.CREATE_NEW, suggested);
            }
            default -> new NameConflictResult(Choice.CANCEL, null);
        };
    }
}
