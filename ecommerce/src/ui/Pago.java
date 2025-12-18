package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

import service.CarritoService;
import service.PagoService;

public class Pago extends JFrame {

    private String usuario;
    private CarritoService carritoService;

    public Pago(String usuario, CarritoService carritoService) {

        this.usuario = usuario;
        this.carritoService = carritoService;

        setTitle("Procesar Pago");
        setSize(1050, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        initUI();
    }

    private void initUI() {

        Color fondo = new Color(245, 245, 245);
        Color azul = new Color(125, 196, 255);
        Color grisBoton = new Color(210, 210, 210);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(fondo);
        add(wrapper);

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(
                        0, 0, getWidth(), getHeight(), 35, 35
                ));

                g2.setColor(new Color(220, 220, 220));
                g2.draw(new RoundRectangle2D.Double(
                        0, 0, getWidth(), getHeight(), 35, 35
                ));

                super.paintComponent(g);
            }
        };

        card.setOpaque(false);
        card.setPreferredSize(new Dimension(530, 520));
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        wrapper.add(card);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(18, 12, 18, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        JLabel titulo = new JLabel("Confirmar Pago", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 34));
        titulo.setForeground(new Color(50, 50, 50));
        gbc.gridy = 0;
        card.add(titulo, gbc);

        JLabel lblTotal = new JLabel(
                "Total a pagar: $" + String.format(
                        "%.2f", carritoService.calcularTotal()
                ),
                SwingConstants.CENTER
        );
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTotal.setForeground(new Color(70, 70, 70));
        gbc.gridy = 1;
        card.add(lblTotal, gbc);

        JLabel lblUser = new JLabel("Usuario bancario:");
        lblUser.setFont(new Font("SansSerif", Font.PLAIN, 19));
        gbc.gridy = 2;
        card.add(lblUser, gbc);

        JTextField txtUser = new JTextField();
        txtUser.setFont(new Font("SansSerif", Font.PLAIN, 19));
        gbc.gridy = 3;
        card.add(txtUser, gbc);

        JLabel lblPin = new JLabel("PIN bancario (4 dígitos):");
        lblPin.setFont(new Font("SansSerif", Font.PLAIN, 19));
        gbc.gridy = 4;
        card.add(lblPin, gbc);

        JPasswordField txtPin = new JPasswordField();
        txtPin.setFont(new Font("SansSerif", Font.PLAIN, 19));
        gbc.gridy = 5;
        card.add(txtPin, gbc);

        JPanel botonera = new JPanel(new GridBagLayout());
        botonera.setOpaque(false);
        gbc.gridy = 6;
        card.add(botonera, gbc);

        GridBagConstraints gbcBot = new GridBagConstraints();
        gbcBot.insets = new Insets(0, 25, 0, 25);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(170, 50));
        btnCancelar.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnCancelar.setBackground(grisBoton);

        JButton btnPagar = new JButton("Pagar");
        btnPagar.setPreferredSize(new Dimension(170, 50));
        btnPagar.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnPagar.setBackground(azul);
        btnPagar.setForeground(Color.WHITE);

        gbcBot.gridx = 0;
        botonera.add(btnCancelar, gbcBot);

        gbcBot.gridx = 1;
        botonera.add(btnPagar, gbcBot);

        btnCancelar.addActionListener(e -> dispose());

        btnPagar.addActionListener(e -> {

            String usuarioBanco = txtUser.getText().trim();
            String pinBanco = new String(txtPin.getPassword()).trim();

            if (usuarioBanco.isEmpty() || pinBanco.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Debe ingresar usuario y PIN"
                );
                return;
            }

            if (!pinBanco.matches("\\d{4}")) {
                JOptionPane.showMessageDialog(
                        this,
                        "El PIN debe tener 4 dígitos"
                );
                return;
            }

            procesarPago(usuarioBanco, pinBanco);
        });
    }

    private void procesarPago(String usuarioBanco, String pinBanco) {

        String resultado = PagoService.procesarPago(
                usuarioBanco,
                pinBanco,
                carritoService.calcularTotal(),
                carritoService.getProductos()
        );

        switch (resultado) {

            case "OK":
                carritoService.limpiar();
                JOptionPane.showMessageDialog(
                        this,
                        "Pago realizado con éxito"
                );
                dispose();
                break;

            case "FONDOS_INSUFICIENTES":
                JOptionPane.showMessageDialog(
                        this,
                        "Fondos insuficientes"
                );
                break;

            case "ERROR_LOGIN":
                JOptionPane.showMessageDialog(
                        this,
                        "Credenciales bancarias incorrectas"
                );
                break;

            default:
                JOptionPane.showMessageDialog(
                        this,
                        "Error al procesar el pago"
                );
                break;
        }
    }
}
