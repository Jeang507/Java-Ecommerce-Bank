package model;

public class Producto {

    private String nombre;
    private double precio;
    private String imagen;
    private String descripcion;

    public Producto(String nombre, double precio, String imagen, String descripcion) {
        this.nombre = nombre;
        this.precio = precio;
        this.imagen = imagen;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public String getImagen() {
        return imagen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return nombre + " - $" + precio;
    }
}
