package service;

import model.Producto;
import java.util.ArrayList;

public class CarritoService {

    private final ArrayList<Producto> productos;

    public CarritoService(ArrayList<Producto> productos) {
        this.productos = productos;
    }

    public ArrayList<Producto> getProductos() {
        return productos;
    }

    public void agregar(Producto producto) {
        productos.add(producto);
    }

    public void eliminarPorIndices(int[] indices) {
        for (int i = indices.length - 1; i >= 0; i--) {
            productos.remove(indices[i]);
        }
    }

    public double calcularTotal() {
        return productos.stream()
                .mapToDouble(Producto::getPrecio)
                .sum();
    }

    public boolean estaVacio() {
        return productos.isEmpty();
    }

    public void limpiar() {
        productos.clear();
    }

    public int cantidad() {
        return productos.size();
    }
}
