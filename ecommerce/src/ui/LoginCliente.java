package ui;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

import model.UsuarioEcommerce;
import service.UserStoreEcommerce;

public class LoginCliente extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPass;
    private JButton btnLogin;
    private JButton btnRegistrar;

    public LoginCliente() {

        FlatLightLaf.setup();

        setTitle("YulianaApp – Inicio de Sesión");
        setMinimumSize(new Dimension(820, 480));
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
        left.setPreferredSize(new Dimension(420, 480));

        JLabel brand = new JLabel("YulianaApp");
        brand.setFont(new Font("SansSerif", Font.BOLD, 42));
        brand.setForeground(Color.WHITE);

        JLabel slogan = new JLabel("<html><center>La tienda perfecta<br>para tu bebé</center></html>");
        slogan.setFont(new Font("SansSerif", Font.BOLD, 26));
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
        card.setPreferredSize(new Dimension(380, 360));

        GridBagConstraints gr = new GridBagConstraints();
        gr.insets = new Insets(10, 40, 10, 40);
        gr.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Iniciar Sesión");
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(new Color(60, 60, 60));
        gr.gridy = 0;
        card.add(title, gr);

        JLabel lblUser = new JLabel("Usuario");
        gr.gridy = 1;
        card.add(lblUser, gr);

        txtUsuario = new JTextField();
        gr.gridy = 2;
        card.add(txtUsuario, gr);

        JLabel lblPass = new JLabel("Contraseña");
        gr.gridy = 3;
        card.add(lblPass, gr);

        txtPass = new JPasswordField();
        gr.gridy = 4;
        card.add(txtPass, gr);

        JPanel botones = new JPanel(new GridLayout(1, 2, 20, 10));
        botones.setOpaque(false);

        btnLogin = new JButton("Ingresar");
        btnLogin.setBackground(new Color(60, 110, 255));
        btnLogin.setForeground(Color.WHITE);

        btnRegistrar = new JButton("Crear Cuenta");
        btnRegistrar.setBackground(new Color(150, 200, 255));
        btnRegistrar.setForeground(Color.WHITE);

        botones.add(btnLogin);
        botones.add(btnRegistrar);

        gr.gridy = 5;
        card.add(botones, gr);

        rightWrapper.add(card);

        gbc.gridx = 0;
        master.add(left, gbc);

        gbc.gridx = 1;
        master.add(rightWrapper, gbc);

        btnLogin.addActionListener(e -> iniciarSesion());
        btnRegistrar.addActionListener(e -> new RegisterCliente().setVisible(true));

        return master;
    }

    private void iniciarSesion() {

        String user = txtUsuario.getText().trim();
        String pass = new String(txtPass.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos");
            return;
        }

        UsuarioEcommerce u = UserStoreEcommerce.login(user, pass);

        if (u == null) {
            JOptionPane.showMessageDialog(this, "Credenciales incorrectas");
            return;
        }

        JOptionPane.showMessageDialog(this, "Bienvenido " + u.getUsername());
        dispose();

        Tienda tienda = new Tienda(u.getUsername(), null);
        tienda.setVisible(true);
    }
}
