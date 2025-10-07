package com.cburch.logisim.verilog.file.ui;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;

// Helper para el diálogo
public final class MissingModuleDialog {
    public enum Choice { IMPORT_THIS, SKIP_THIS, IMPORT_ALL }

    public static Choice ask(Component parent, String moduleName, Path filePath) {
        final String title = "Importar submódulo encontrado";
        final String message = """
                Se encontró un JSON que contiene el módulo requerido:
                Módulo: %s
                Archivo: %s

                ¿Deseas importarlo?
                """.formatted(moduleName, filePath.getFileName());

        final Object[] options = {
                "Importar este",
                "No importar",
                "Importar este y todos los siguientes"
        };

        // Asegura llamada en EDT
        final int[] result = new int[1];
        Runnable r = () -> result[0] = JOptionPane.showOptionDialog(
                parent,
                message,
                title,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        if (SwingUtilities.isEventDispatchThread()) r.run();
        else {
            try { SwingUtilities.invokeAndWait(r); } catch (Exception ignored) {}
        }

        return switch (result[0]) {
            case 0 -> Choice.IMPORT_THIS;
            case 1 -> Choice.SKIP_THIS;
            case 2 -> Choice.IMPORT_ALL;
            default -> Choice.SKIP_THIS;
        };
    }
}

