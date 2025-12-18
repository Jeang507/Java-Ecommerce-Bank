package ui;
import util.PasswordEncryption;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ATMInterface extends JFrame {

    // VARIABLES PRINCIPALES

    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Login
    private JPanel loginPanel;
    private JTextField txtUsuario;
    private JPasswordField txtPin;
    private JButton btnConectar, btnRegistrar, btnLogin;
    private JLabel lblIntentos;

    // ATM 
    private JPanel atmPanel;
    private JLabel lblBienvenida;
    private JTextArea areaDisplay;
    private JButton btnSaldo, btnRetiro, btnDeposito;
    private JButton btnCerrarSesion, btnSalirSistema, btnEditarPerfil;

    //  Admin 
    private JPanel adminPanel;
    private JTextArea areaAdmin;
    private JButton btnCrearUsuario, btnVerReporte, btnResetIntentos, btnTablaUsuarios;
    private JButton btnCerrarSesionAdmin, btnSalirSistemaAdmin;

    // Conexión Banco 
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private boolean conectado = false;
    private boolean logged = false;
    private String usuarioActual = "";
    private String rolActual = "";
    private int intentosRestantes = 3;
    private int idUsuarioActual = 0;

    private JFrame frameTablaUsuarios = null;
    private DefaultTableModel modeloTabla = null;
    private JTable tablaUsuarios = null;

    // Colores Corporativos Profesionales
    private final Color COLOR_PRIMARIO = new Color(0, 41, 84);       // Azul corporativo oscuro
    private final Color COLOR_SECUNDARIO = new Color(30, 70, 125);   // Azul medio
    private final Color COLOR_ACENTO = new Color(0, 122, 204);       // Azul acento
    private final Color COLOR_EXITO = new Color(40, 167, 69);        // Verde corporativo
    private final Color COLOR_ERROR = new Color(220, 53, 69);        // Rojo corporativo
    private final Color COLOR_ADVERTENCIA = new Color(255, 153, 0);  // Naranja
    private final Color COLOR_FONDO = new Color(248, 249, 250);      // Gris claro profesional
    private final Color COLOR_TEXTO = new Color(33, 37, 41);         // Gris oscuro para texto
    private final Color COLOR_BORDE = new Color(206, 212, 218);      // Gris borde
    private final Color COLOR_SOMBRA = new Color(0, 0, 0, 0.1f);     // Sombra sutil

    // Gradientes profesionales
    private final GradientPaint GRADIENTE_PRIMARIO = new GradientPaint(0, 0, new Color(0, 41, 84), 0, 50, new Color(0, 61, 114));
    private final GradientPaint GRADIENTE_SECUNDARIO = new GradientPaint(0, 0, new Color(30, 70, 125), 0, 50, new Color(40, 90, 150));

    // Fuentes corporativas
    private final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 28);
    private final Font FUENTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 16);
    private final Font FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FUENTE_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FUENTE_BOTON = new Font("Segoe UI Semibold", Font.BOLD, 14);
    private final Font FUENTE_MONOSPACE = new Font("Consolas", Font.PLAIN, 13);

    //     CONSTRUCTOR

    public ATMInterface() {
        try { 
            // Usar un tema más corporativo
            UIManager.setLookAndFeel(new javax.swing.plaf.nimbus.NimbusLookAndFeel());
        } catch (Exception e) {
            System.out.println("Usando Look and Feel por defecto: " + e.getMessage());
        }

        initComponents();
        mostrarPanelLogin();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarCierre();
            }
        });
    }

    //     CONFIG INICIAL

    private void initComponents() {
        setTitle("Sistema Bancario Nacional - Cajero Automático");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setResizable(true);

        // Configurar icono de la aplicación
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icono.png")));
        } catch (Exception e) {
            // Si no hay icono, continuar sin él
        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(COLOR_FONDO);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        crearPanelLogin();
        crearPanelATM();
        crearPanelAdmin();

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(atmPanel, "ATM");
        mainPanel.add(adminPanel, "ADMIN");

        add(mainPanel);
    }

    // PANEL LOGIN CORPORATIVO

    private void crearPanelLogin() {
        loginPanel = new JPanel(new BorderLayout(0, 0));
        loginPanel.setBackground(COLOR_FONDO);

        // Panel lateral izquierdo con branding
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(GRADIENTE_PRIMARIO);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Logo/texto del banco
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 32));
                String bankName = "BANCO NACIONAL";
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(bankName)) / 2;
                int y = getHeight() / 2 - 40;
                g2d.drawString(bankName, x, y);
                
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                String slogan = "Seguridad y Confianza";
                fm = g2d.getFontMetrics();
                x = (getWidth() - fm.stringWidth(slogan)) / 2;
                y += 40;
                g2d.drawString(slogan, x, y);
            }
        };
        leftPanel.setPreferredSize(new Dimension(350, 0));
        leftPanel.setLayout(new BorderLayout());
        
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        
        // Información de seguridad
        JLabel infoLabel = new JLabel("<html><center><font color='white' size='5'><b>Acceso Seguro</b></font><br><br>"
                + "<font size='3'>Sistema certificado bajo normas<br>"
                + "internacionales de seguridad bancaria</font></center></html>");
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(infoLabel, BorderLayout.CENTER);
        
        // Panel derecho de login
        JPanel rightPanel = new JPanel(new BorderLayout(0, 20));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        // Encabezado del login
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JLabel tituloLogin = new JLabel("Inicio de Sesión", SwingConstants.CENTER);
        tituloLogin.setFont(FUENTE_TITULO);
        tituloLogin.setForeground(COLOR_PRIMARIO);
        headerPanel.add(tituloLogin, BorderLayout.NORTH);
        
        JLabel subtituloLogin = new JLabel("Ingrese sus credenciales para acceder al sistema", SwingConstants.CENTER);
        subtituloLogin.setFont(FUENTE_SUBTITULO);
        subtituloLogin.setForeground(COLOR_TEXTO);
        headerPanel.add(subtituloLogin, BorderLayout.CENTER);
        
        rightPanel.add(headerPanel, BorderLayout.NORTH);

        // Formulario de login
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        // Campo Usuario
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(FUENTE_BOLD);
        lblUsuario.setForeground(COLOR_TEXTO);
        formPanel.add(lblUsuario, gbc);

        gbc.gridy++;
        txtUsuario = new JTextField(20);
        txtUsuario.setFont(FUENTE_NORMAL);
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        formPanel.add(txtUsuario, gbc);

        gbc.gridy++;
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)), gbc);

        // Campo PIN
        gbc.gridy++;
        JLabel lblPin = new JLabel("PIN de 4 dígitos:");
        lblPin.setFont(FUENTE_BOLD);
        lblPin.setForeground(COLOR_TEXTO);
        formPanel.add(lblPin, gbc);

        gbc.gridy++;
        txtPin = new JPasswordField(4);
        txtPin.setFont(FUENTE_NORMAL);
        txtPin.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        txtPin.setEchoChar('•');
        ((AbstractDocument) txtPin.getDocument())
            .setDocumentFilter(new DigitOnlyFilter(4));
        formPanel.add(txtPin, gbc);

        gbc.gridy++;
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)), gbc);

        // Contador de intentos
        gbc.gridy++;
        lblIntentos = new JLabel("Intentos restantes: 3", SwingConstants.CENTER);
        lblIntentos.setFont(FUENTE_BOLD);
        lblIntentos.setForeground(COLOR_EXITO);
        formPanel.add(lblIntentos, gbc);

        rightPanel.add(formPanel, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        btnConectar = crearBotonCorporativo("Conectar Servidor", COLOR_SECUNDARIO, 1);
        btnRegistrar = crearBotonCorporativo("Registrar Usuario", COLOR_ACENTO, 1);
        btnLogin = crearBotonCorporativo("Ingresar", COLOR_EXITO, 1.2f);

        btnConectar.addActionListener(e -> connectToServer());
        btnRegistrar.addActionListener(e -> doRegisterFromGUI());
        btnLogin.addActionListener(e -> doLoginFromGUI());

        buttonPanel.add(btnConectar);
        buttonPanel.add(btnRegistrar);
        buttonPanel.add(btnLogin);

        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Panel de teclado numérico
        JPanel keyboardPanel = crearTecladoNumericoCorporativo();
        rightPanel.add(keyboardPanel, BorderLayout.EAST);

        loginPanel.add(leftPanel, BorderLayout.WEST);
        loginPanel.add(rightPanel, BorderLayout.CENTER);

        // Agregar información de copyright
        JLabel copyright = new JLabel("© 2024 Banco Nacional - Todos los derechos reservados", SwingConstants.CENTER);
        copyright.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        copyright.setForeground(new Color(108, 117, 125));
        copyright.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        loginPanel.add(copyright, BorderLayout.SOUTH);
    }

    // TECLADO NUMÉRICO CORPORATIVO

private JPanel crearTecladoNumericoCorporativo() {
    JPanel panel = new JPanel(new GridLayout(4, 3, 12, 12));
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_BORDE, 1),
            "Teclado Virtual",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            FUENTE_BOLD,
            COLOR_PRIMARIO
        ),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)
    ));

    String[] buttons = {"7", "8", "9", "4", "5", "6", "1", "2", "3", "0", "C", "↲"};

    for (String text : buttons) {
        JButton btn = new JButton(text);

        // Botón estilo cajero moderno
        btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(12, 10, 12, 10)
        ));
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.DARK_GRAY);
        
        // Bordes redondeados visualmente
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);

        // Colores especiales
        if (text.equals("C")) {
            btn.setBackground(new Color(230, 70, 70));  // Rojo suave
            btn.setForeground(Color.WHITE);
            btn.addActionListener(e -> txtPin.setText(""));
        } else if (text.equals("↲")) {
            btn.setBackground(new Color(40, 150, 80)); // Verde moderno
            btn.setForeground(Color.WHITE);
            btn.addActionListener(e -> doLoginFromGUI());
        } else {
            final String num = text;
            btn.addActionListener(e -> {
                String pinActual = new String(txtPin.getPassword());
                if (pinActual.length() < 4)
                    txtPin.setText(pinActual + num);
            });
        }

        // Efecto hover (visual más moderno)
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(btn.getBackground().darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (text.equals("C")) btn.setBackground(new Color(230, 70, 70));
                else if (text.equals("↲")) btn.setBackground(new Color(40, 150, 80));
                else btn.setBackground(Color.WHITE);
            }
        });

        panel.add(btn);
    }

    return panel;
}

    // BOTÓN CORPORATIVO

    private JButton crearBotonCorporativo(String texto, Color color, float escala) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fondo con gradiente
                GradientPaint gradient = new GradientPaint(
                    0, 0, color,
                    0, getHeight(), color.darker()
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Borde
                g2.setColor(color.darker().darker());
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                
                super.paintComponent(g);
            }
        };
        
        btn.setFont(FUENTE_BOTON.deriveFont(FUENTE_BOTON.getSize() * escala));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        
        // Efecto hover
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color.brighter(), 2),
                    BorderFactory.createEmptyBorder(10, 23, 10, 23)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
            }
        });
        
        return btn;
    }

    // FILTRO DE NUMEROS

    class DigitOnlyFilter extends DocumentFilter {
        private final int max;

        public DigitOnlyFilter(int maxLength) { this.max = maxLength; }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {

            if (text == null) return;

            String nuevoTexto = fb.getDocument().getText(0, fb.getDocument().getLength());
            nuevoTexto = nuevoTexto.substring(0, offset) + text + nuevoTexto.substring(offset + length);

            if (nuevoTexto.matches("\\d*") && nuevoTexto.length() <= max)
                super.replace(fb, offset, length, text, attrs);
        }
    }

    // PANEL PRINCIPAL DEL ATM CORPORATIVO

    private void crearPanelATM() {
        atmPanel = new JPanel(new BorderLayout(0, 0));
        atmPanel.setBackground(COLOR_FONDO);

        // Header con gradiente
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setPaint(GRADIENTE_PRIMARIO);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(0, 120));
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel headerContent = new JPanel(new BorderLayout());
        headerContent.setOpaque(false);

        lblBienvenida = new JLabel("Bienvenido", JLabel.CENTER);
        lblBienvenida.setFont(FUENTE_TITULO.deriveFont(24f));
        lblBienvenida.setForeground(Color.WHITE);
        lblBienvenida.setBorder(BorderFactory.createEmptyBorder(20, 20, 5, 20));

        JLabel subtitulo = new JLabel("CAJERO AUTOMÁTICO - BANCO NACIONAL", JLabel.CENTER);
        subtitulo.setFont(FUENTE_SUBTITULO);
        subtitulo.setForeground(new Color(200, 200, 200));
        subtitulo.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        headerContent.add(lblBienvenida, BorderLayout.CENTER);
        headerContent.add(subtitulo, BorderLayout.SOUTH);

        header.add(headerContent, BorderLayout.CENTER);
        atmPanel.add(header, BorderLayout.NORTH);

        // Panel principal con operaciones
        JPanel mainContent = new JPanel(new BorderLayout(20, 20));
        mainContent.setBackground(COLOR_FONDO);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel de operaciones rápidas
        JPanel operacionesPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        operacionesPanel.setBackground(COLOR_FONDO);
        operacionesPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_BORDE, 1),
            "Operaciones Bancarias",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            FUENTE_BOLD,
            COLOR_PRIMARIO
        ));

        btnSaldo = crearBotonOperacionCorporativa("SALDO", new Color(52, 152, 219), "💰");
        btnDeposito = crearBotonOperacionCorporativa("DEPÓSITO", new Color(46, 204, 113), "⬆️");
        btnRetiro = crearBotonOperacionCorporativa("RETIRO", new Color(231, 76, 60), "⬇️");
        btnEditarPerfil = crearBotonOperacionCorporativa("PERFIL", new Color(155, 89, 182), "👤");
        btnCerrarSesion = crearBotonOperacionCorporativa("CERRAR SESIÓN", new Color(241, 196, 15), "🚪");
        btnSalirSistema = crearBotonOperacionCorporativa("SALIR", new Color(149, 165, 166), "⏻");

        btnSaldo.addActionListener(e -> requestBalance());
        btnDeposito.addActionListener(e -> doDeposit());
        btnRetiro.addActionListener(e -> doWithdraw());
        btnEditarPerfil.addActionListener(e -> editarPerfil());
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        btnSalirSistema.addActionListener(e -> confirmarCierre());

        operacionesPanel.add(btnSaldo);
        operacionesPanel.add(btnDeposito);
        operacionesPanel.add(btnRetiro);
        operacionesPanel.add(btnEditarPerfil);
        operacionesPanel.add(btnCerrarSesion);
        operacionesPanel.add(btnSalirSistema);

        mainContent.add(operacionesPanel, BorderLayout.CENTER);

        // Panel de información/display
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        areaDisplay = new JTextArea(8, 40);
        areaDisplay.setEditable(false);
        areaDisplay.setFont(FUENTE_MONOSPACE);
        areaDisplay.setBackground(new Color(250, 250, 250));
        areaDisplay.setForeground(COLOR_TEXTO);
        areaDisplay.setLineWrap(true);
        areaDisplay.setWrapStyleWord(true);
        areaDisplay.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(areaDisplay);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = COLOR_ACENTO;
                this.trackColor = COLOR_FONDO;
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });

        // Encabezado del display
        JPanel displayHeader = new JPanel(new BorderLayout());
        displayHeader.setBackground(Color.WHITE);
        displayHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE));
        
        JLabel displayTitle = new JLabel("Historial de Operaciones");
        displayTitle.setFont(FUENTE_BOLD);
        displayTitle.setForeground(COLOR_PRIMARIO);
        displayTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        
        JLabel timestamp = new JLabel(getCurrentTimestamp());
        timestamp.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        timestamp.setForeground(new Color(108, 117, 125));
        
        displayHeader.add(displayTitle, BorderLayout.WEST);
        displayHeader.add(timestamp, BorderLayout.EAST);
        
        infoPanel.add(displayHeader, BorderLayout.NORTH);
        infoPanel.add(scroll, BorderLayout.CENTER);

        mainContent.add(infoPanel, BorderLayout.SOUTH);

        atmPanel.add(mainContent, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(COLOR_FONDO);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDE));
        
        JLabel footerText = new JLabel("Para asistencia las 24 horas, contacte al 0800-BANCO-00");
        footerText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerText.setForeground(new Color(108, 117, 125));
        footer.add(footerText);
        
        atmPanel.add(footer, BorderLayout.SOUTH);
    }

    // BOTÓN DE OPERACIÓN CORPORATIVO

    private JButton crearBotonOperacionCorporativa(String texto, Color color, String icono) {
        JButton btn = new JButton("<html><center><span style='font-size:2px'>" + icono + "</span><br>" + texto + "</center></html>") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fondo con sombra
                g2.setColor(COLOR_SOMBRA);
                g2.fillRoundRect(3, 3, getWidth()-6, getHeight()-6, 12, 12);
                
                // Fondo principal
                GradientPaint gradient = new GradientPaint(
                    0, 0, color.brighter(),
                    0, getHeight(), color
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                
                // Borde
                g2.setColor(color.darker());
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                
                super.paintComponent(g);
            }
        };
        
        btn.setFont(FUENTE_BOTON);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        
        // Efecto hover
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color.brighter(), 2),
                    BorderFactory.createEmptyBorder(18, 8, 18, 8)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
            }
        });
        
        return btn;
    }

// PANEL ADMIN CORPORATIVO IGUAL AL CLIENTE

private void crearPanelAdmin() {

    adminPanel = new JPanel(new BorderLayout(0, 0));
    adminPanel.setBackground(COLOR_FONDO);

    // ================= HEADER =================
    JPanel header = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(GRADIENTE_SECUNDARIO);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    };
    header.setLayout(new BorderLayout());
    header.setPreferredSize(new Dimension(0, 120));

    JLabel titulo = new JLabel("Panel de Administración", SwingConstants.CENTER);
    titulo.setFont(FUENTE_TITULO.deriveFont(24f));
    titulo.setForeground(Color.WHITE);

    JLabel subtitulo = new JLabel("SISTEMA DE GESTIÓN BANCARIA", SwingConstants.CENTER);
    subtitulo.setFont(FUENTE_SUBTITULO);
    subtitulo.setForeground(new Color(200, 200, 200));

    JPanel headerContent = new JPanel(new BorderLayout());
    headerContent.setOpaque(false);
    headerContent.add(titulo, BorderLayout.CENTER);
    headerContent.add(subtitulo, BorderLayout.SOUTH);

    header.add(headerContent, BorderLayout.CENTER);
    adminPanel.add(header, BorderLayout.NORTH);

    // ================= MAIN CONTENT =================
    JPanel mainContent = new JPanel(new BorderLayout(20, 20));
    mainContent.setBackground(COLOR_FONDO);
    mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // ================= BOTONES ADMIN =================
    btnCrearUsuario      = crearBotonOperacionCorporativa("NUEVO USUARIO",     new Color(52,152,219), "👥");
    btnVerReporte        = crearBotonOperacionCorporativa("REPORTE",           new Color(46,204,113), "📊");
    btnResetIntentos     = crearBotonOperacionCorporativa("RESET INTENTOS",    new Color(241,196,15), "🔄");
    btnTablaUsuarios     = crearBotonOperacionCorporativa("USUARIOS",          new Color(155,89,182), "📋");
    btnCerrarSesionAdmin = crearBotonOperacionCorporativa("CERRAR SESIÓN",     new Color(231,76,60), "🚪");
    btnSalirSistemaAdmin = crearBotonOperacionCorporativa("SALIR SISTEMA",     new Color(149,165,166), "⏻");

    // 🔥 MISMO TAMAÑO EXACTO QUE LOS BOTONES DEL CLIENTE
    Dimension tamCliente = new Dimension(80, 50);
    btnCrearUsuario.setPreferredSize(tamCliente);
    btnVerReporte.setPreferredSize(tamCliente);
    btnResetIntentos.setPreferredSize(tamCliente);
    btnTablaUsuarios.setPreferredSize(tamCliente);
    btnCerrarSesionAdmin.setPreferredSize(tamCliente);
    btnSalirSistemaAdmin.setPreferredSize(tamCliente);

    // Acciones
    btnCrearUsuario.addActionListener(e -> crearUsuario());
    btnVerReporte.addActionListener(e -> obtenerReporte());
    btnResetIntentos.addActionListener(e -> resetearIntentos());
    btnTablaUsuarios.addActionListener(e -> mostrarTablaUsuarios());
    btnCerrarSesionAdmin.addActionListener(e -> cerrarSesion());
    btnSalirSistemaAdmin.addActionListener(e -> confirmarCierre());

    // ================= GRID 2 × 3 (igual al cliente) =================
    JPanel gridBotones = new JPanel(new GridLayout(2, 3, 15, 15));
    gridBotones.setOpaque(false);

    gridBotones.add(btnCrearUsuario);
    gridBotones.add(btnVerReporte);
    gridBotones.add(btnResetIntentos);
    gridBotones.add(btnTablaUsuarios);
    gridBotones.add(btnCerrarSesionAdmin);
    gridBotones.add(btnSalirSistemaAdmin);

    JPanel controlesPanel = new JPanel(new BorderLayout());
    controlesPanel.setBackground(COLOR_FONDO);
    controlesPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_BORDE, 1),
            "Controles Administrativos",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            FUENTE_BOLD,
            COLOR_PRIMARIO
    ));
    controlesPanel.add(gridBotones, BorderLayout.CENTER);

    mainContent.add(controlesPanel, BorderLayout.NORTH);

    // ================= CONSOLA ADMIN =================
    JPanel consolaPanel = new JPanel(new BorderLayout());
    consolaPanel.setBackground(Color.WHITE);
    consolaPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));

    areaAdmin = new JTextArea(12, 40);
    areaAdmin.setEditable(false);
    areaAdmin.setFont(FUENTE_MONOSPACE);
    areaAdmin.setBackground(new Color(250, 250, 250));
    areaAdmin.setForeground(COLOR_TEXTO);
    areaAdmin.setLineWrap(true);
    areaAdmin.setWrapStyleWord(true);

    JScrollPane scroll = new JScrollPane(areaAdmin);

    JPanel consolaHeader = new JPanel(new BorderLayout());
    consolaHeader.setBackground(Color.WHITE);

    JLabel consolaTitle = new JLabel("Consola Administrativa");
    consolaTitle.setFont(FUENTE_BOLD);
    consolaTitle.setForeground(COLOR_PRIMARIO);

    JButton btnClear = new JButton("Limpiar");
    btnClear.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    btnClear.addActionListener(e -> areaAdmin.setText(""));

    JPanel consolaControls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    consolaControls.setBackground(Color.WHITE);
    consolaControls.add(btnClear);

    consolaHeader.add(consolaTitle, BorderLayout.WEST);
    consolaHeader.add(consolaControls, BorderLayout.EAST);

    consolaPanel.add(consolaHeader, BorderLayout.NORTH);
    consolaPanel.add(scroll, BorderLayout.CENTER);

    mainContent.add(consolaPanel, BorderLayout.SOUTH);

    adminPanel.add(mainContent, BorderLayout.CENTER);

    // ================= FOOTER =================
    JPanel footer = new JPanel(new BorderLayout());
    footer.setBackground(COLOR_FONDO);
    footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDE));

    footer.add(new JLabel("Modo Administrador | Sistema Operativo"), BorderLayout.WEST);
    footer.add(new JLabel("v2.1.0  "), BorderLayout.EAST);

    adminPanel.add(footer, BorderLayout.SOUTH);
}

    // MÉTODOS AUXILIARES

    private String getCurrentTimestamp() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return LocalDate.now().format(dateFormatter) + " " + LocalTime.now().format(timeFormatter);
    }

    // MANEJO DE CONEXIÓN

    private void connectToServer() {
        if (conectado) {
            mostrarMensaje("Ya estás conectado.", COLOR_EXITO);
            return;
        }

        new Thread(() -> {
            try {
                mostrarMensaje("Conectando con el servidor...", new Color(0,85,150));

                socket = new Socket();
                socket.connect(new InetSocketAddress("localhost", 24000), 4000);

                entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                salida = new PrintWriter(socket.getOutputStream(), true);
                conectado = true;

                btnConectar.setText("Conectado");
                btnConectar.setBackground(COLOR_EXITO);

                mostrarMensaje("Conexión establecida correctamente.", COLOR_EXITO);

                new Thread(() -> {
                    try {
                        String msg;
                        while ((msg = entrada.readLine()) != null) {
                            procesarRespuestaServidor(msg);
                        }
                    } catch (Exception e) {
                        if (conectado) {
                            mostrarMensaje("Conexión perdida.", COLOR_ERROR);
                            resetConexion();
                        }
                    }
                }).start();

            } catch (IOException e) {
                mostrarMensaje("No se pudo conectar: " + e.getMessage(), COLOR_ERROR);
            }
        }).start();
    }

    // PROCESADOR DE RESPUESTAS

    private void procesarRespuestaServidor(String rsp) {
        SwingUtilities.invokeLater(() -> {

            if (rsp.equals("SERVER_DOWN")) {
                mostrarMensaje("El servidor está caído.", COLOR_ERROR);
                resetConexion();
                return;
            }

            if (rsp.startsWith("LOGIN_OK:")) {
                String[] p = rsp.split(":");
                rolActual = p[1];
                logged = true;
                usuarioActual = txtUsuario.getText().trim();

                intentosRestantes = 3;
                actualizarLabelIntentos();

                if (p.length > 5) {
                    idUsuarioActual = Integer.parseInt(p[5]);
                }

                if (rolActual.equals("admin")) mostrarPanelAdmin();
                else mostrarPanelATM();

                mostrarMensaje("Inicio de sesión exitoso.", COLOR_EXITO);
                return;
            }

            if (rsp.startsWith("LOGIN_FAIL")) {
                intentosRestantes--;
                actualizarLabelIntentos();
                mostrarMensaje("Credenciales incorrectas.", COLOR_ERROR);
                return;
            }

            if (rsp.startsWith("REGISTER_OK")) {
                mostrarMensaje("Usuario registrado exitosamente.", COLOR_EXITO);
                limpiarCampos();
                return;
            }

            if (rsp.startsWith("REGISTER_FAIL")) {
                mostrarMensaje("Error registrando usuario.", COLOR_ERROR);
                return;
            }

            if (rsp.startsWith("BALANCE:")) {
                mostrarMensaje("Saldo actual: $" + rsp.substring(8), COLOR_PRIMARIO);
                return;
            }

            if (rsp.startsWith("DEPOSIT_OK")) {
                mostrarMensaje("Depósito exitoso.", COLOR_EXITO);
                return;
            }

            if (rsp.startsWith("WITHDRAW_OK")) {
                mostrarMensaje("Retiro exitoso.", COLOR_EXITO);
                return;
            }

            if (rsp.startsWith("WITHDRAW_FAIL")) {
                mostrarMensaje("Fondos insuficientes.", COLOR_ERROR);
                return;
            }

            if (rsp.startsWith("REPORT:")) {
                mostrarReporte(rsp.substring(7));
                return;
            }

            if (rsp.startsWith("USERS_TABLE:")) {
                crearVentanaTablaUsuarios(rsp.substring(12));
                return;
            }

            mostrarMensaje("Servidor: " + rsp, new Color(60,0,120));
        });
    }

    // REGISTRO DE USUARIO

    private void doRegisterFromGUI() {
        if (!conectado) {
            JOptionPane.showMessageDialog(this,
                    "Debes conectarte al servidor primero.",
                    "Conectar primero",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String usuario = txtUsuario.getText().trim();
        String pin = new String(txtPin.getPassword()).trim();

        if (usuario.isEmpty()) {
            mostrarMensaje("El usuario no puede estar vacío.", COLOR_ERROR);
            return;
        }

        if (!pin.matches("\\d{4}")) {
            mostrarMensaje("El PIN debe ser de 4 dígitos.", COLOR_ERROR);
            return;
        }

        realizarProcesoRegistroCompleto(usuario, pin);
    }

    private void realizarProcesoRegistroCompleto(String usuario, String pin) {
        try {
            String nombre = JOptionPane.showInputDialog(this,
                    "Ingrese su NOMBRE:",
                    "Registro - Paso 1/4",
                    JOptionPane.QUESTION_MESSAGE);

            if (nombre == null || nombre.isBlank()) {
                mostrarMensaje("Registro cancelado (nombre vacío).", COLOR_ERROR);
                return;
            }

            String apellido = JOptionPane.showInputDialog(this,
                    "Ingrese su APELLIDO:",
                    "Registro - Paso 2/4",
                    JOptionPane.QUESTION_MESSAGE);

            if (apellido == null || apellido.isBlank()) {
                mostrarMensaje("Registro cancelado (apellido vacío).", COLOR_ERROR);
                return;
            }

            String cedula = JOptionPane.showInputDialog(this,
                    "Ingrese su CÉDULA:",
                    "Registro - Paso 3/4",
                    JOptionPane.QUESTION_MESSAGE);

            if (cedula == null || cedula.isBlank()) {
                mostrarMensaje("Registro cancelado (cédula vacía).", COLOR_ERROR);
                return;
            }

            String cuenta = JOptionPane.showInputDialog(this,
                    "Ingrese su NÚMERO DE CUENTA (8-12 dígitos):",
                    "Registro - Paso 4/4",
                    JOptionPane.QUESTION_MESSAGE);

            if (cuenta == null || !cuenta.matches("\\d{8,12}")) {
                mostrarMensaje("Número de cuenta inválido.", COLOR_ERROR);
                return;
            }

            String pinEnc = PasswordEncryption.encryptPassword(pin);

            String comando = "REGISTER:" +
                    usuario + "," +
                    nombre + "," +
                    apellido + "," +
                    cedula + "," +
                    cuenta + "," +
                    pinEnc;

            salida.println(comando);

            mostrarMensaje("Enviando registro...\n" +
                    "Usuario: " + usuario + "\n" +
                    "Nombre: " + nombre + " " + apellido + "\n" +
                    "Cuenta: " + cuenta, COLOR_PRIMARIO);

        } catch (Exception ex) {
            mostrarMensaje("Error registrando: " + ex.getMessage(), COLOR_ERROR);
        }
    }

    // LOGIN

    private void doLoginFromGUI() {
        String usuario = txtUsuario.getText().trim();
        String pin = new String(txtPin.getPassword()).trim();

        if (usuario.isEmpty() || pin.isEmpty()) {
            mostrarMensaje("Usuario o PIN vacío.", COLOR_ERROR);
            return;
        }

        if (!pin.matches("\\d{4}")) {
            mostrarMensaje("El PIN debe ser de 4 dígitos.", COLOR_ERROR);
            return;
        }

        String pinEnc = PasswordEncryption.encryptPassword(pin);

        if (!conectado) {
            conectarPrimero(() -> salida.println("LOGIN:" + usuario + "," + pinEnc));
        } else {
            salida.println("LOGIN:" + usuario + "," + pinEnc);
        }
    }

    // OPERACIONES ATM

    private void requestBalance() {
        if (!verificarSesion()) return;
        salida.println("BALANCE:");
    }

    private void doDeposit() {
        if (!verificarSesion()) return;

        String montoStr = JOptionPane.showInputDialog(this,
                "Monto a depositar:",
                "Depósito",
                JOptionPane.QUESTION_MESSAGE);

        if (montoStr == null) return;

        try {
            double monto = Double.parseDouble(montoStr);

            if (monto <= 0) {
                mostrarMensaje("El monto debe ser positivo.", COLOR_ERROR);
                return;
            }

            salida.println("DEPOSIT:" + monto);

        } catch (Exception e) {
            mostrarMensaje("Monto inválido.", COLOR_ERROR);
        }
    }

    private void doWithdraw() {
        if (!verificarSesion()) return;

        String montoStr = JOptionPane.showInputDialog(this,
                "Monto a retirar:",
                "Retiro",
                JOptionPane.QUESTION_MESSAGE);

        if (montoStr == null) return;

        try {
            double monto = Double.parseDouble(montoStr);

            if (monto <= 0) {
                mostrarMensaje("Monto debe ser positivo.", COLOR_ERROR);
                return;
            }

            salida.println("WITHDRAW:" + monto);

        } catch (Exception e) {
            mostrarMensaje("Monto inválido.", COLOR_ERROR);
        }
    }

    // CREAR USUARIO ADMIN

    private void crearUsuario() {
        JTextField txtNombre = new JTextField();
        JTextField txtApellido = new JTextField();
        JTextField txtUsername = new JTextField();
        JTextField txtCedula = new JTextField();
        JTextField txtCuenta = new JTextField();
        JPasswordField txtPinDialog = new JPasswordField(4);
        JComboBox<String> cmbRol = new JComboBox<>(new String[]{"cliente", "admin"});

        Object[] msg = {
                "Nombre:", txtNombre,
                "Apellido:", txtApellido,
                "Usuario:", txtUsername,
                "Cédula:", txtCedula,
                "Cuenta (8-12 dígitos):", txtCuenta,
                "PIN (4 dígitos):", txtPinDialog,
                "Rol:", cmbRol
        };

        int op = JOptionPane.showConfirmDialog(this, msg,
                "Crear Usuario",
                JOptionPane.OK_CANCEL_OPTION);

        if (op != JOptionPane.OK_OPTION) return;

        if (txtNombre.getText().isBlank() ||
                txtApellido.getText().isBlank() ||
                txtUsername.getText().isBlank() ||
                txtCedula.getText().isBlank() ||
                txtCuenta.getText().isBlank()) {

            mostrarMensajeAdmin("Todos los campos son obligatorios.", COLOR_ERROR);
            return;
        }

        String pin = new String(txtPinDialog.getPassword()).trim();
        if (!pin.matches("\\d{4}")) {
            mostrarMensajeAdmin("PIN inválido.", COLOR_ERROR);
            return;
        }

        if (!txtCuenta.getText().matches("\\d{8,12}")) {
            mostrarMensajeAdmin("Cuenta inválida.", COLOR_ERROR);
            return;
        }

        String pinEnc = PasswordEncryption.encryptPassword(pin);

        String datos =
                txtUsername.getText() + "," +
                        txtNombre.getText() + "," +
                        txtApellido.getText() + "," +
                        txtCedula.getText() + "," +
                        txtCuenta.getText() + "," +
                        pinEnc + "," +
                        cmbRol.getSelectedItem();

        salida.println("CREATE_USER:" + datos);
    }

    // RESETEAR INTENTOS
    private void resetearIntentos() {
        if (!verificarSesion() || !rolActual.equals("admin")) {
            mostrarMensajeAdmin("Solo administradores pueden resetear intentos", COLOR_ERROR);
            return;
        }
        
        String usuarioReset = JOptionPane.showInputDialog(this,
            "Ingrese el nombre de usuario para resetear intentos:",
            "Resetear Intentos",
            JOptionPane.QUESTION_MESSAGE);
        
        if (usuarioReset != null && !usuarioReset.trim().isEmpty()) {
            salida.println("RESET_ATTEMPTS:" + usuarioReset.trim());
            mostrarMensajeAdmin("Solicitando reset de intentos para: " + usuarioReset, COLOR_PRIMARIO);
        }
    }

    // REPORTE (sin cambios)

    private void obtenerReporte() {
        salida.println("GET_REPORT:");
    }

    private void mostrarReporte(String reporte) {
        StringBuilder sb = new StringBuilder();

        sb.append("REPORTE DE USUARIOS\n\n");

        String[] usuarios = reporte.split(";");

        for (String r : usuarios) {
            if (r.isBlank()) continue;

            String[] d = r.split(",");

            if (d.length < 11) continue;

            sb.append("ID: ").append(d[0]).append("\n");
            sb.append("Usuario: ").append(d[1]).append("\n");
            sb.append("Nombre: ").append(d[2]).append(" ").append(d[3]).append("\n");
            sb.append("Rol: ").append(d[4]).append("\n");
            sb.append("Saldo: $").append(d[5]).append("\n");
            sb.append("Intentos: ").append(d[6]).append("\n");
            sb.append("Cédula: ").append(d[7]).append("\n");
            sb.append("Cuenta: ").append(d[8]).append("\n");
            sb.append("PIN: ").append(d[9]).append("\n");
            sb.append("Expira: ").append(d[10]).append("\n");
            sb.append("--------------------------------\n");
        }

        areaAdmin.setText(sb.toString());
    }

    // TABLA USUARIOS (ADMIN)
    private void mostrarTablaUsuarios() {
        if (!verificarSesion()) return;
        if (!rolActual.equals("admin")) return;

        salida.println("GET_USERS_TABLE:");
    }

    private void crearVentanaTablaUsuarios(String datos) {
        if (frameTablaUsuarios != null && frameTablaUsuarios.isVisible()) {
            actualizarDatosTabla(datos);
            return;
        }

        frameTablaUsuarios = new JFrame("Usuarios del Banco");
        frameTablaUsuarios.setSize(1200, 550);
        frameTablaUsuarios.setLocationRelativeTo(this);

        String[] columnas = {
                "ID", "Usuario", "Nombre", "Apellido", "Cédula",
                "Cuenta", "PIN", "Expira", "Rol", "Saldo", "Intentos", "Acciones"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int col) {
                return col == 11; // solo botón editar
            }
        };

        tablaUsuarios = new JTable(modeloTabla);
        tablaUsuarios.setRowHeight(25);

        tablaUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = tablaUsuarios.rowAtPoint(e.getPoint());
                int col = tablaUsuarios.columnAtPoint(e.getPoint());
                if (col == 11) editarUsuarioDesdeTabla(modeloTabla, row);
            }
        });

        JScrollPane scroll = new JScrollPane(tablaUsuarios);

        actualizarDatosTabla(datos);

        frameTablaUsuarios.add(scroll, BorderLayout.CENTER);
        frameTablaUsuarios.setVisible(true);
    }

    private void actualizarDatosTabla(String datos) {
        modeloTabla.setRowCount(0);

        String[] usuarios = datos.split(";");

        for (String u : usuarios) {
            if (u.isBlank()) continue;

            String[] d = u.split(",");

            if (d.length < 11) continue;

            modeloTabla.addRow(new Object[]{
                    d[0], d[1], d[2], d[3], d[7], d[8], d[9], d[10], d[4], "$" + d[5], d[6], "Editar"
            });
        }
    }

    // EDITAR USUARIO DESDE TABLA 

    private void editarUsuarioDesdeTabla(DefaultTableModel model, int row) {
        String id = model.getValueAt(row, 0).toString();
        String usuarioActual = model.getValueAt(row, 1).toString();
        String nombreActual = model.getValueAt(row, 2).toString();
        String apellidoActual = model.getValueAt(row, 3).toString();
        String cedulaActual = model.getValueAt(row, 4).toString();
        String cuentaActual = model.getValueAt(row, 5).toString();
        String pinActual = model.getValueAt(row, 6).toString();
        String expiracionActual = model.getValueAt(row, 7).toString();
        String rolActualTabla = model.getValueAt(row, 8).toString();
        String saldoActual = model.getValueAt(row, 9).toString().replace("$", "");
        String intentosActual = model.getValueAt(row, 10).toString();

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));

        JTextField txtUsuario = new JTextField(usuarioActual);
        JTextField txtNombre = new JTextField(nombreActual);
        JTextField txtApellido = new JTextField(apellidoActual);
        JTextField txtCedula = new JTextField(cedulaActual);
        JTextField txtCuenta = new JTextField(cuentaActual);
        txtCuenta.setEditable(false);

        JPasswordField txtPin = new JPasswordField();
        txtPin.setEchoChar('•');

        JTextField txtExpiracion = new JTextField(expiracionActual);
        JTextField txtSaldo = new JTextField(saldoActual);
        JTextField txtIntentos = new JTextField(intentosActual);

        JComboBox<String> cmbRol = new JComboBox<>(new String[]{"cliente", "admin"});
        cmbRol.setSelectedItem(rolActualTabla);

        panel.add(new JLabel("Usuario:"));
        panel.add(txtUsuario);
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Apellido:"));
        panel.add(txtApellido);
        panel.add(new JLabel("Cédula:"));
        panel.add(txtCedula);
        panel.add(new JLabel("Número de Cuenta:"));
        panel.add(txtCuenta);
        panel.add(new JLabel("Nuevo PIN (opcional):"));
        panel.add(txtPin);
        panel.add(new JLabel("Expiración:"));
        panel.add(txtExpiracion);
        panel.add(new JLabel("Rol:"));
        panel.add(cmbRol);
        panel.add(new JLabel("Saldo:"));
        panel.add(txtSaldo);
        panel.add(new JLabel("Intentos:"));
        panel.add(txtIntentos);

        int result = JOptionPane.showConfirmDialog(this,
                panel,
                "Editar Usuario - ID " + id,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return;

        String nuevoPin = new String(txtPin.getPassword()).trim();

        if (!nuevoPin.isEmpty() && !nuevoPin.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(this,
                    "El PIN debe ser de 4 dígitos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String pinEnviar = nuevoPin.isEmpty()
                ? pinActual
                : PasswordEncryption.encryptPassword(nuevoPin);

        String datos =
                usuarioActual + "," +
                        txtUsuario.getText().trim() + "," +
                        txtNombre.getText().trim() + "," +
                        txtApellido.getText().trim() + "," +
                        cmbRol.getSelectedItem() + "," +
                        txtSaldo.getText().trim() + "," +
                        txtIntentos.getText().trim() + "," +
                        txtCedula.getText().trim() + "," +
                        pinEnviar + "," +
                        txtExpiracion.getText().trim() + "," +
                        cuentaActual;

        salida.println("UPDATE_USER_ADMIN:" + datos);
        mostrarMensajeAdmin("Solicitud de actualización enviada...", COLOR_PRIMARIO);
    }

    // EDITAR PERFIL DEL USUARIO


    private void editarPerfil() {
        if (!verificarSesion()) return;

        JTextField txtNuevoUsuario = new JTextField(usuarioActual);
        JTextField txtNuevoNombre = new JTextField();
        JTextField txtNuevoApellido = new JTextField();
        JPasswordField txtNuevoPin = new JPasswordField(4);
        txtNuevoPin.setEchoChar('•');

        Object[] msg = {
                "Nuevo usuario (vacío = no cambiar):", txtNuevoUsuario,
                "Nuevo nombre (vacío = no cambiar):", txtNuevoNombre,
                "Nuevo apellido (vacío = no cambiar):", txtNuevoApellido,
                "Nuevo PIN (vacío = no cambiar):", txtNuevoPin
        };

        int op = JOptionPane.showConfirmDialog(this,
                msg,
                "Editar Perfil",
                JOptionPane.OK_CANCEL_OPTION);

        if (op != JOptionPane.OK_OPTION) return;

        String nuevoUsuario = txtNuevoUsuario.getText().trim();
        String nuevoNombre = txtNuevoNombre.getText().trim();
        String nuevoApellido = txtNuevoApellido.getText().trim();
        String nuevoPin = new String(txtNuevoPin.getPassword()).trim();

        if (!nuevoPin.isEmpty() && !nuevoPin.matches("\\d{4}")) {
            mostrarMensaje("El PIN debe ser de 4 dígitos.", COLOR_ERROR);
            return;
        }

        String pinEnviar = nuevoPin.isEmpty() ? "" : PasswordEncryption.encryptPassword(nuevoPin);
        String datos =
                nuevoUsuario + "," +
                        nuevoNombre + "," +
                        nuevoApellido + "," +
                        pinEnviar;

        salida.println("UPDATE_PROFILE:" + datos);

        mostrarMensaje("Solicitud enviada...", COLOR_PRIMARIO);
    }

    // CONTADORES DE USUARIOS

    private int contarUsuariosActivos() {
        int count = 0;

        if (modeloTabla != null) {
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                int intentos = Integer.parseInt(modeloTabla.getValueAt(i, 10).toString());
                if (intentos < 3) count++;
            }
        }

        return count;
    }

    private int contarUsuariosBloqueados() {
        int count = 0;

        if (modeloTabla != null) {
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                int intentos = Integer.parseInt(modeloTabla.getValueAt(i, 10).toString());
                if (intentos >= 3) count++;
            }
        }

        return count;
    }

    private int contarUsuariosPorRol(String rol) {
        int count = 0;

        if (modeloTabla != null) {
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                if (modeloTabla.getValueAt(i, 8).toString().equals(rol)) {
                    count++;
                }
            }
        }

        return count;
    }

    // EXPORTAR TABLA A ARCHIVO TXT 

    private void exportarTablaAArchivo() {
        if (modeloTabla == null || modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay datos para exportar.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar tabla...");
        chooser.setSelectedFile(new File("tabla_usuarios.txt"));

        int ok = chooser.showSaveDialog(this);

        if (ok != JFileChooser.APPROVE_OPTION) return;

        File f = chooser.getSelectedFile();

        try (PrintWriter pw = new PrintWriter(f)) {
            pw.println("TABLA DE USUARIOS — SISTEMA BANCARIO");
            pw.println("=====================================");
            pw.println("Fecha: " + java.time.LocalDate.now());
            pw.println("Hora: " + java.time.LocalTime.now());
            pw.println();

            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                pw.println("ID: " + modeloTabla.getValueAt(i, 0));
                pw.println("Usuario: " + modeloTabla.getValueAt(i, 1));
                pw.println("Nombre: " + modeloTabla.getValueAt(i, 2));
                pw.println("Apellido: " + modeloTabla.getValueAt(i, 3));
                pw.println("Cédula: " + modeloTabla.getValueAt(i, 4));
                pw.println("Cuenta: " + modeloTabla.getValueAt(i, 5));
                pw.println("Rol: " + modeloTabla.getValueAt(i, 8));
                pw.println("Saldo: " + modeloTabla.getValueAt(i, 9));
                pw.println("Intentos: " + modeloTabla.getValueAt(i, 10));
                pw.println("----------------------------------------");
            }

            pw.println("Usuarios activos: " + contarUsuariosActivos());
            pw.println("Usuarios bloqueados: " + contarUsuariosBloqueados());
            pw.println("Clientes: " + contarUsuariosPorRol("cliente"));
            pw.println("Admins: " + contarUsuariosPorRol("admin"));

            JOptionPane.showMessageDialog(this,
                    "Archivo guardado:\n" + f.getAbsolutePath(),
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error exportando: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    // EXPORTAR REPORTE COMPLETO


    private void exportarReporteCompleto() {
        salida.println("EXPORT_USERS:");
        mostrarMensajeAdmin("Solicitando exportación...", COLOR_PRIMARIO);
    }

    // AUXILIARES


    private boolean verificarSesion() {
        if (!logged) {
            mostrarMensaje("Debe iniciar sesión.", COLOR_ERROR);
            return false;
        }

        return true;
    }

    private void conectarPrimero(Runnable accion) {
        if (conectado) {
            accion.run();
            return;
        }

        int op = JOptionPane.showConfirmDialog(this,
                "No estás conectado. ¿Conectar ahora?",
                "Conectar",
                JOptionPane.YES_NO_OPTION);

        if (op != JOptionPane.YES_OPTION) return;

        connectToServer();

        new Timer(600, e -> {
            if (conectado) {
                accion.run();
                ((Timer) e.getSource()).stop();
            }
        }).start();
    }

    private void limpiarCampos() {
        txtUsuario.setText("");
        txtPin.setText("");
    }

    private void mostrarMensaje(String msg, Color c) {
        areaDisplay.setForeground(c);
        areaDisplay.setText(msg);
        areaDisplay.setCaretPosition(0);
    }

    private void mostrarMensajeAdmin(String msg, Color c) {
        areaAdmin.setForeground(c);
        areaAdmin.setText(msg);
    }

    private void confirmarCierre() {
        int op = JOptionPane.showConfirmDialog(this,
                "¿Cerrar aplicación?",
                "Salir",
                JOptionPane.YES_NO_OPTION);

        if (op == JOptionPane.YES_OPTION) dispose();
    }

    // RESET CONEXIÓN COMPLETA

    private void resetConexion() {
        conectado = false;
        logged = false;
        usuarioActual = "";
        rolActual = "";
        idUsuarioActual = 0;

        try {
            if (socket != null) socket.close();
        } catch (Exception ignored) {}

        socket = null;
        entrada = null;
        salida = null;

        btnConectar.setBackground(COLOR_PRIMARIO);
        btnConectar.setText("Conectar");

        mostrarPanelLogin();
    }

    // SESSION Y NAVEGACIÓN

    private void mostrarPanelATM() {
        cardLayout.show(mainPanel, "ATM");

        lblBienvenida.setText("Bienvenido: " + usuarioActual +
                " (ID " + idUsuarioActual + ")");

        mostrarMensaje(
                "Seleccione una operación.\n" +
                        "— Consultar Saldo\n" +
                        "— Depósito\n" +
                        "— Retiro\n" +
                        "— Editar Perfil",
                COLOR_PRIMARIO
        );
    }

    private void mostrarPanelLogin() {
        cardLayout.show(mainPanel, "LOGIN");

        limpiarCampos();
        intentosRestantes = 3;
        actualizarLabelIntentos();

        mostrarMensaje(
                "Bienvenido al Sistema Bancario.\n" +
                        "Credenciales admin por defecto:\n" +
                        "usuario: admin\n" +
                        "pin: 1234",
                COLOR_PRIMARIO
        );
    }

    private void mostrarPanelAdmin() {
        cardLayout.show(mainPanel, "ADMIN");

        areaAdmin.setText(
                "Bienvenido administrador.\n\n" +
                        "— Crear usuarios\n" +
                        "— Ver reportes\n" +
                        "— Resetear intentos\n" +
                        "— Tabla de usuarios"
        );
    }

    private void cerrarSesion() {
        logged = false;
        usuarioActual = "";
        rolActual = "";
        idUsuarioActual = 0;

        mostrarPanelLogin();
        mostrarMensaje("Sesión cerrada.", COLOR_EXITO);
    }

    private void actualizarLabelIntentos() {
        lblIntentos.setText("Intentos restantes: " + intentosRestantes);

        if (intentosRestantes == 3) lblIntentos.setForeground(COLOR_EXITO);
        if (intentosRestantes == 2) lblIntentos.setForeground(COLOR_ADVERTENCIA);
        if (intentosRestantes <= 1) lblIntentos.setForeground(COLOR_ERROR);
    }

    // MÉTODO MAIN

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ATMInterface ui = new ATMInterface();
            ui.setVisible(true);
        });
    }
}
