package com.cburch.logisim.verilog.file.ui;

import javax.swing.JOptionPane;
import java.awt.Component;

public final class ImportCompletionDialog {

    private ImportCompletionDialog() {}

    public enum Choice { GO_TO_MODULE, STAY_HERE }

    /**
     * Muestra un diálogo al finalizar la importación.
     * @param parent componente padre (usa el frame del proyecto)
     * @param moduleName nombre del módulo/circuito principal importado
     * @return elección del usuario
     */
    public static Choice show(Component parent, String moduleName) {
        String title = "Importación completada";
        String msg   = "La importación ha finalizado.\n"
                + "¿Quieres ir al módulo \"" + moduleName + "\" o quedarte en el circuito actual?";

        Object[] options = { "Ir al módulo", "Quedarme aquí" };
        int sel = JOptionPane.showOptionDialog(
                parent,
                msg,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );
        return (sel == JOptionPane.YES_OPTION) ? Choice.GO_TO_MODULE : Choice.STAY_HERE;
    }
}
