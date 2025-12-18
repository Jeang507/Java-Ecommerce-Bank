package app;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import ui.LoginCliente;

public class EcommerceApp {

    public static void main(String[] args) {

        // Se intenta aplicar el Look and Feel del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si falla, se continúa con el estilo por defecto
        }

        // Se inicia la interfaz gráfica dentro del hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            LoginCliente login = new LoginCliente();
            login.setLocationRelativeTo(null);
            login.setVisible(true);
        });
    }
}
