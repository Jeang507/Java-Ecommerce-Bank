package ui;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import model.Producto;
import service.CarritoService;
import ui.layout.WrapLayout;

public class Tienda extends JFrame {

    private String usuario;

    private ArrayList<Producto> productos = new ArrayList<>();
    private ArrayList<Producto> carrito = new ArrayList<>();
    private CarritoService carritoService;

    private JPanel panelProductos;
    private JButton btnCarrito;
    private JButton btnCuenta;

    public Tienda(String usuario, Object conexionNoUsada) {

        FlatLightLaf.setup();

        this.usuario = usuario;
        this.carritoService = new CarritoService(carrito);

        setTitle("YulianaApp - Bienvenido " + usuario);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cargarProductos();
        initUI();
    }

    private void initUI() {

        setLayout(new BorderLayout());

        // Barra superior
        JPanel nav = new JPanel(new BorderLayout());
        nav.setPreferredSize(new Dimension(100, 70));
        nav.setBackground(Color.WHITE);
        nav.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, new Color(230, 230, 230))
        );

        JLabel logo = new JLabel("YulianaApp");
        logo.setFont(new Font("SansSerif", Font.BOLD, 26));
        logo.setBorder(new EmptyBorder(10, 20, 10, 20));
        nav.add(logo, BorderLayout.WEST);

        JTextField txtBuscar = new JTextField();
        txtBuscar.putClientProperty(
                "JTextField.placeholderText", "Buscar productos..."
        );
        txtBuscar.setPreferredSize(new Dimension(300, 35));

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(new EmptyBorder(15, 30, 15, 30));
        searchPanel.setOpaque(false);
        searchPanel.add(txtBuscar, BorderLayout.CENTER);

        nav.add(searchPanel, BorderLayout.CENTER);

        JPanel userActions = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 20, 15)
        );
        userActions.setOpaque(false);

        btnCuenta = new JButton("Mi cuenta");
        btnCarrito = new JButton("Carrito (0)");

        userActions.add(btnCuenta);
        userActions.add(btnCarrito);

        nav.add(userActions, BorderLayout.EAST);

        add(nav, BorderLayout.NORTH);

        // Panel de productos
        panelProductos = new JPanel(
                new WrapLayout(FlowLayout.LEFT, 25, 25)
        );
        panelProductos.setBackground(new Color(245, 245, 245));

        JScrollPane scroll = new JScrollPane(panelProductos);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(20);

        for (Producto p : productos) {
            panelProductos.add(crearTarjetaProducto(p));
        }

        add(scroll, BorderLayout.CENTER);

        // Acciones
        btnCuenta.addActionListener(e -> abrirCuenta());
        btnCarrito.addActionListener(e -> abrirCarrito());

        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                filtrarProductos(txtBuscar.getText());
            }
            public void removeUpdate(DocumentEvent e) {
                filtrarProductos(txtBuscar.getText());
            }
            public void changedUpdate(DocumentEvent e) {
                filtrarProductos(txtBuscar.getText());
            }
        });
    }

    private JPanel crearTarjetaProducto(Producto p) {

        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(220, 330));
        card.setBackground(Color.WHITE);
        card.setBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230))
        );

        card.setToolTipText(
                "<html><b>" + p.getNombre() + "</b><br>"
                        + p.getDescripcion() + "</html>"
        );

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBorder(
                        BorderFactory.createLineBorder(
                                new Color(120, 160, 255), 2
                        )
                );
            }

            public void mouseExited(MouseEvent e) {
                card.setBorder(
                        BorderFactory.createLineBorder(
                                new Color(230, 230, 230)
                        )
                );
            }
        });

        ImageIcon icon = null;
        try {
            Image img = new ImageIcon(p.getImagen()).getImage();
            Image scaled = img.getScaledInstance(
                    200, 160, Image.SCALE_SMOOTH
            );
            icon = new ImageIcon(scaled);
        } catch (Exception e) {
            // Imagen no encontrada
        }

        JLabel imgLabel = new JLabel(icon, SwingConstants.CENTER);

        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setPreferredSize(new Dimension(200, 160));
        imagePanel.setBackground(new Color(240, 240, 240));
        imagePanel.add(imgLabel, BorderLayout.CENTER);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Color.WHITE);
        info.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel nombre = new JLabel(p.getNombre());
        nombre.setFont(new Font("SansSerif", Font.BOLD, 15));

        JLabel precio = new JLabel("$" + p.getPrecio());
        precio.setFont(new Font("SansSerif", Font.BOLD, 18));
        precio.setForeground(new Color(60, 110, 255));

        JButton btnAgregar = new JButton("Agregar al carrito");
        btnAgregar.setBackground(new Color(90, 150, 255));
        btnAgregar.setForeground(Color.WHITE);

        btnAgregar.addActionListener(e -> {
            carritoService.agregar(p);
            actualizarCarritoUI();
            JOptionPane.showMessageDialog(
                    this, p.getNombre() + " agregado al carrito"
            );
        });

        info.add(nombre);
        info.add(Box.createVerticalStrut(5));
        info.add(precio);
        info.add(Box.createVerticalStrut(15));
        info.add(btnAgregar);

        card.add(imagePanel, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);

        return card;
    }

    private void filtrarProductos(String texto) {

        texto = texto.toLowerCase();
        panelProductos.removeAll();

        for (Producto p : productos) {
            if (p.getNombre().toLowerCase().contains(texto)
                    || p.getDescripcion().toLowerCase().contains(texto)) {
                panelProductos.add(crearTarjetaProducto(p));
            }
        }

        panelProductos.revalidate();
        panelProductos.repaint();
    }

    private void actualizarCarritoUI() {
        btnCarrito.setText("Carrito (" + carritoService.cantidad() + ")");
    }

    private void abrirCarrito() {
        Carrito ventana = new Carrito(usuario, carritoService);
        ventana.setVisible(true);
    }

    private void abrirCuenta() {
        CuentaUsuario ventana = new CuentaUsuario(usuario);
        ventana.setVisible(true);
    }

    private void cargarProductos() {

    productos.add(new Producto(
            "Traje NASA para bebé",
            18.00,
            "img/11.jpeg",
            "Enterizo blanco inspirado en la NASA, suave y cómodo."
    ));

    productos.add(new Producto(
            "Conjunto Celeste Niño & Niña",
            25.00,
            "img/4.jpeg",
            "Conjunto elegante de dos piezas ideal para eventos."
    ));

    productos.add(new Producto(
            "Enterizo Goku - Dragon Ball",
            15.00,
            "img/7.jpeg",
            "Enterizo fresco inspirado en Goku, muy llamativo."
    ));

    productos.add(new Producto(
            "Enterizo Racing Bebé",
            17.00,
            "img/3.jpeg",
            "Traje de corredor para bebé con colores vibrantes."
    ));

    productos.add(new Producto(
            "Vestido Elegante Floral",
            22.00,
            "img/13.jpeg",
            "Vestido elegante con flor y moño, perfecto para ocasiones especiales."
    ));

    productos.add(new Producto(
            "Traje Tanjiro - Demon Slayer",
            16.00,
            "img/2.jpeg",
            "Traje inspirado en Tanjiro Kamado, tela suave y cómoda."
    ));

    productos.add(new Producto(
            "Enterizo Buzz Lightyear",
            18.00,
            "img/8.jpeg",
            "Enterizo de Buzz Lightyear con detalles bien definidos."
    ));

    productos.add(new Producto(
            "Vestido Gingerbread",
            20.00,
            "img/6.jpeg",
            "Vestido navideño con diseño de galleta de jengibre."
    ));

    productos.add(new Producto(
            "Vestido Santa Claus Niña",
            20.00,
            "img/5.jpeg",
            "Vestido rojo navideño con detalles blancos."
    ));

    productos.add(new Producto(
            "Vestido elegante negro y beige",
            24.00,
            "img/12.jpeg",
            "Vestido elegante con lazo y diseño floral, ideal para eventos."
    ));

    productos.add(new Producto(
            "Vestido azul con margaritas",
            23.00,
            "img/10.jpeg",
            "Vestido fresco con diseño de margaritas e incluye sombrero."
    ));
}


}
