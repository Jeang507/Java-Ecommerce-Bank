package ui;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

import service.UserStoreEcommerce;
import util.PasswordEncryption;

public class RegisterCliente extends JFrame {

    private JTextField txtUsuario;
    private JTextField txtCorreo;
    private JPasswordField txtPass;
    private JPasswordField txtPass2;

    private JButton btnRegistrar;
    private JButton btnCancelar;

    public RegisterCliente() {

        FlatLightLaf.setup();

        setTitle("Crear Cuenta – YulianaApp");
        setMinimumSize(new Dimension(820, 520));
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());
        add(crearContenido(), BorderLayout.CENTER);
    }

    private JPanel crearContenido() {

        JPanel master = new JPanel(new GridBagLayout());
        master.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        // Panel izquierdo
        JPanel left = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(255, 150, 180),
                        getWidth(), getHeight(), new Color(125, 195, 255)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        left.setLayout(new GridBagLayout());
        left.setPreferredSize(new Dimension(420, 520));

        JLabel brand = new JLabel("Crear Cuenta");
        brand.setFont(new Font("SansSerif", Font.BOLD, 42));
        brand.setForeground(Color.WHITE);

        JLabel slogan = new JLabel(
                "<html><center>Únete a YulianaApp<br>tu tienda de confianza</center></html>"
        );
        slogan.setFont(new Font("SansSerif", Font.BOLD, 24));
        slogan.setForeground(Color.WHITE);

        GridBagConstraints gl = new GridBagConstraints();
        gl.insets = new Insets(10, 10, 10, 10);
        gl.gridx = 0;

        gl.gridy = 0; left.add(brand, gl);
        gl.gridy = 1; left.add(slogan, gl);

        // Panel derecho
        JPanel rightWrapper = new JPanel(new GridBagLayout());
        rightWrapper.setBackground(Color.WHITE);

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));

                g2.setColor(new Color(220, 220, 220));
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));

                super.paintComponent(g);
            }
        };

        card.setOpaque(false);
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(360, 430));

        GridBagConstraints gr = new GridBagConstraints();
        gr.insets = new Insets(8, 35, 8, 35);
        gr.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Registro de Usuario", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(60, 60, 60));
        gr.gridy = 0;
        card.add(title, gr);

        JLabel lblUser = new JLabel("Usuario");
        gr.gridy = 1;
        card.add(lblUser, gr);

        txtUsuario = new JTextField();
        gr.gridy = 2;
        card.add(txtUsuario, gr);

        JLabel lblCorreo = new JLabel("Correo electrónico");
        gr.gridy = 3;
        card.add(lblCorreo, gr);

        txtCorreo = new JTextField();
        gr.gridy = 4;
        card.add(txtCorreo, gr);

        JLabel lblPass1 = new JLabel("Contraseña");
        gr.gridy = 5;
        card.add(lblPass1, gr);

        txtPass = new JPasswordField();
        gr.gridy = 6;
        card.add(txtPass, gr);

        JLabel lblPass2 = new JLabel("Repetir contraseña");
        gr.gridy = 7;
        card.add(lblPass2, gr);

        txtPass2 = new JPasswordField();
        gr.gridy = 8;
        card.add(txtPass2, gr);

        JPanel botones = new JPanel(new GridLayout(1, 2, 20, 10));
        botones.setOpaque(false);

        btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBackground(new Color(60, 140, 255));
        btnRegistrar.setForeground(Color.WHITE);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(200, 200, 200));
        btnCancelar.setForeground(Color.WHITE);

        botones.add(btnRegistrar);
        botones.add(btnCancelar);

        gr.gridy = 9;
        card.add(botones, gr);

        rightWrapper.add(card);

        gbc.gridx = 0;
        master.add(left, gbc);

        gbc.gridx = 1;
        master.add(rightWrapper, gbc);

        btnCancelar.addActionListener(e -> dispose());
        btnRegistrar.addActionListener(e -> registrar());

        return master;
    }

    private void registrar() {

        String user = txtUsuario.getText().trim();
        String correo = txtCorreo.getText().trim();
        String p1 = new String(txtPass.getPassword());
        String p2 = new String(txtPass2.getPassword());

        if (user.isEmpty() || correo.isEmpty() || p1.isEmpty() || p2.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos");
            return;
        }

        if (!p1.equals(p2)) {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden");
            return;
        }

        String passwordEncriptado = PasswordEncryption.encryptPassword(p1);
        String rol = "cliente";

        boolean ok = UserStoreEcommerce.registrar(
                user,
                passwordEncriptado,
                rol,
                correo
        );

        if (ok) {
            JOptionPane.showMessageDialog(this, "Cuenta creada correctamente");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "El usuario ya existe");
        }
    }
}
