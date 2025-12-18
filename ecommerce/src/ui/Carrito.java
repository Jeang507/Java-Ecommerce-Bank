package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

import service.CarritoService;
import model.Producto;

public class Carrito extends JFrame {

    private String usuario;
    private CarritoService carritoService;

    private JTable tablaCarrito;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotal;

    public Carrito(String usuario, CarritoService carritoService) {

        this.usuario = usuario;
        this.carritoService = carritoService;

        setTitle("Carrito de Compras - " + usuario);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
        cargarCarritoEnTabla();
        actualizarTotal();
    }

    private void initUI() {

        Color grisFondo = new Color(245, 245, 245);
        Color azul = new Color(125, 196, 255);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(grisFondo);
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
                        0, 0, getWidth(), getHeight(), 22, 22
                ));

                g2.setColor(new Color(220, 220, 220));
                g2.draw(new RoundRectangle2D.Double(
                        0, 0, getWidth(), getHeight(), 22, 22
                ));

                super.paintComponent(g);
            }
        };

        card.setOpaque(false);
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        card.setLayout(new BorderLayout(20, 20));
        card.setPreferredSize(new Dimension(820, 500));

        wrapper.add(card);

        JLabel titulo = new JLabel("Carrito de Compras");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 32));
        titulo.setForeground(new Color(60, 60, 60));
        card.add(titulo, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
                new String[]{"Producto", "Precio ($)"},
                0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaCarrito = new JTable(modeloTabla);
        tablaCarrito.setRowHeight(28);
        tablaCarrito.setSelectionMode(
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        );

        JScrollPane scroll = new JScrollPane(tablaCarrito);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        card.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        lblTotal = new JLabel();
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTotal.setForeground(new Color(50, 50, 50));
        footer.add(lblTotal, BorderLayout.WEST);

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 15, 5)
        );
        botones.setOpaque(false);

        JButton btnEliminar = new JButton("Eliminar");
        JButton btnPagar = new JButton("Pagar");
        JButton btnCerrar = new JButton("Cerrar");

        btnEliminar.setBackground(new Color(230, 80, 80));
        btnEliminar.setForeground(Color.WHITE);

        btnPagar.setBackground(azul);
        btnPagar.setForeground(Color.WHITE);

        botones.add(btnEliminar);
        botones.add(btnCerrar);
        botones.add(btnPagar);

        footer.add(botones, BorderLayout.EAST);
        card.add(footer, BorderLayout.SOUTH);

        btnEliminar.addActionListener(e -> eliminarSeleccionados());
        btnCerrar.addActionListener(e -> dispose());
        btnPagar.addActionListener(e -> abrirPago());
    }

    private void cargarCarritoEnTabla() {

        modeloTabla.setRowCount(0);

        for (Producto p : carritoService.getProductos()) {
            modeloTabla.addRow(new Object[]{
                    p.getNombre(),
                    String.format("%.2f", p.getPrecio())
            });
        }
    }

    private void actualizarTotal() {
        lblTotal.setText(
                "Total: $" + String.format(
                        "%.2f", carritoService.calcularTotal()
                )
        );
    }

    private void eliminarSeleccionados() {

        int[] filas = tablaCarrito.getSelectedRows();

        if (filas.length == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione uno o más productos para eliminar"
            );
            return;
        }

        carritoService.eliminarPorIndices(filas);
        cargarCarritoEnTabla();
        actualizarTotal();
    }

    private void abrirPago() {

        if (carritoService.estaVacio()) {
            JOptionPane.showMessageDialog(
                    this,
                    "El carrito está vacío"
            );
            return;
        }

        Pago pago = new Pago(usuario, carritoService);
        pago.setVisible(true);
    }
}
