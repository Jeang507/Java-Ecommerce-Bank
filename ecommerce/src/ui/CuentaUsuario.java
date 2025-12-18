package ui;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import model.UsuarioEcommerce;
import service.UserStoreEcommerce;

public class CuentaUsuario extends JFrame {

    private UsuarioEcommerce usuario;

    public CuentaUsuario(String username) {

        FlatLightLaf.setup();

        this.usuario = UserStoreEcommerce.getUsuario(username);

        if (this.usuario == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error cargando la información del usuario",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            dispose();
            return;
        }

        setTitle("Mi Cuenta - " + username);
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        initUI();
    }

    private void initUI() {

        Color fondo = new Color(245, 245, 245);
        Color azul = new Color(125, 196, 255);
        Color texto = new Color(60, 60, 60);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(fondo);
        add(wrapper);

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(550, 480));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(30, 30, 30, 30)
        ));
        card.setLayout(new BorderLayout(20, 20));

        wrapper.add(card);

        JLabel titulo = new JLabel(
                "Mi Información Personal",
                SwingConstants.CENTER
        );
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        titulo.setForeground(texto);
        card.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);

        // Avatar simple con inicial
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.setColor(azul);
                g.fillOval(10, 10, 120, 120);

                g.setColor(Color.WHITE);
                g.setFont(new Font("SansSerif", Font.BOLD, 50));
                g.drawString(
                        usuario.getUsername()
                                .substring(0, 1)
                                .toUpperCase(),
                        53,
                        85
                );
            }
        };

        avatarPanel.setPreferredSize(new Dimension(140, 140));
        avatarPanel.setOpaque(false);

        centro.add(avatarPanel, BorderLayout.WEST);

        JPanel datos = new JPanel();
        datos.setLayout(new BoxLayout(datos, BoxLayout.Y_AXIS));
        datos.setOpaque(false);
        datos.setBorder(new EmptyBorder(0, 20, 0, 0));

        datos.add(crearDato("Usuario:", usuario.getUsername()));
        datos.add(crearDato("Correo:", usuario.getCorreo()));
        datos.add(crearDato("Rol:", usuario.getRol()));
        datos.add(crearDato("ID:", String.valueOf(usuario.getId())));

        centro.add(datos, BorderLayout.CENTER);
        card.add(centro, BorderLayout.CENTER);

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 20, 10)
        );
        botones.setOpaque(false);

        JButton btnEditar = new JButton("Editar Perfil");
        JButton btnCambiarPass = new JButton("Cambiar Contraseña");
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");

        btnEditar.setPreferredSize(new Dimension(180, 45));
        btnCambiarPass.setPreferredSize(new Dimension(180, 45));
        btnCerrarSesion.setPreferredSize(new Dimension(180, 45));

        btnEditar.setBackground(new Color(90, 150, 255));
        btnEditar.setForeground(Color.WHITE);

        btnCambiarPass.setBackground(new Color(255, 180, 90));
        btnCambiarPass.setForeground(Color.WHITE);

        btnCerrarSesion.setBackground(new Color(220, 70, 70));
        btnCerrarSesion.setForeground(Color.WHITE);

        botones.add(btnEditar);
        botones.add(btnCambiarPass);
        botones.add(btnCerrarSesion);

        card.add(botones, BorderLayout.SOUTH);

        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        btnEditar.addActionListener(e -> editarPerfil());
        btnCambiarPass.addActionListener(e -> cambiarPassword());
    }

    private JPanel crearDato(String label, String valor) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel val = new JLabel(valor);
        val.setFont(new Font("SansSerif", Font.PLAIN, 18));

        panel.add(lbl, BorderLayout.WEST);
        panel.add(val, BorderLayout.EAST);

        return panel;
    }

    private void cerrarSesion() {

        JOptionPane.showMessageDialog(
                this,
                "Sesión cerrada"
        );

        dispose();
        new LoginCliente().setVisible(true);
    }

    private void editarPerfil() {
        JOptionPane.showMessageDialog(
                this,
                "Función de edición de perfil en desarrollo"
        );
    }

    private void cambiarPassword() {
        JOptionPane.showMessageDialog(
                this,
                "Función de cambio de contraseña en desarrollo"
        );
    }
}
