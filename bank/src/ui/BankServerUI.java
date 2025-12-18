package ui;

import network.BankServer;
import service.UserService;
import service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BankServerUI extends JFrame {

    private JTextArea logArea;
    private JButton btnIniciar;
    private JButton btnDetener;

    private BankServer server;
    private Thread serverThread;

    public BankServerUI() {
        setTitle("Servidor Bancario");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        initUI();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrar();
            }
        });
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        logArea = new JTextArea();
        logArea.setEditable(false);

        JScrollPane scroll = new JScrollPane(logArea);

        btnIniciar = new JButton("Iniciar Servidor");
        btnDetener = new JButton("Detener Servidor");
        btnDetener.setEnabled(false);

        btnIniciar.addActionListener(e -> iniciarServidor());
        btnDetener.addActionListener(e -> detenerServidor());

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnIniciar);
        panelBotones.add(btnDetener);

        add(scroll, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void iniciarServidor() {
        btnIniciar.setEnabled(false);
        btnDetener.setEnabled(true);

        UserService userService = new UserService();
        AuthService authService = new AuthService(userService);

        // AQUI ESTA LA CLAVE (EL ERROR QUE TENIAS)
        server = new BankServer(
                userService,
                authService,
                msg -> log(msg)
        );

        serverThread = new Thread(server::start);
        serverThread.start();
    }

    private void detenerServidor() {
        if (server != null) {
            server.stop();
            log("Servidor detenido");
        }

        btnIniciar.setEnabled(true);
        btnDetener.setEnabled(false);
    }

    private void cerrar() {
        int op = JOptionPane.showConfirmDialog(
                this,
                "Desea cerrar el servidor",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (op == JOptionPane.YES_OPTION) {
            detenerServidor();
            dispose();
        }
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(msg + "\n");
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BankServerUI().setVisible(true);
        });
    }
}
