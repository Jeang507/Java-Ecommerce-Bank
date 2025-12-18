package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class User {

    private int id;
    private String username;
    private String nombre;
    private String apellido;
    private String rol;
    private double saldo;
    private int intentosFallidos;
    private String cedula;
    private String numeroCuenta;
    private String pin;               // PIN ya encriptado
    private String fechaExpiracion;
    private boolean activo;
    private LocalDate fechaCreacion;

    // CONTADOR GLOBAL DE IDs
    private static final AtomicInteger idCounter = new AtomicInteger(1);

    // CONSTRUCTORES

    public User(int id, String username, String nombre, String apellido,
                String rol, double saldo, int intentosFallidos,
                String cedula, String numeroCuenta,
                String pin, String fechaExpiracion) {

        this.id = id;
        this.username = username;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = rol;
        this.saldo = saldo;
        this.intentosFallidos = intentosFallidos;
        this.cedula = cedula;
        this.numeroCuenta = numeroCuenta;
        this.pin = pin;
        this.fechaExpiracion = fechaExpiracion;
        this.activo = true;
        this.fechaCreacion = LocalDate.now();
    }

    public User(String username, String nombre, String apellido,
                String cedula, String pin, String rol) {

        this.id = generarNuevoID();
        this.username = username;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedula = cedula;
        this.pin = pin;
        this.rol = rol;
        this.saldo = 0.0;
        this.intentosFallidos = 0;
        this.fechaExpiracion = generarFechaExpiracion();
        this.activo = true;
        this.fechaCreacion = LocalDate.now();
    }

    // GETTERS 
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getRol() { return rol; }
    public double getSaldo() { return saldo; }
    public int getIntentosFallidos() { return intentosFallidos; }
    public String getCedula() { return cedula; }
    public String getNumeroCuenta() { return numeroCuenta; }
    public String getPin() { return pin; }
    public String getFechaExpiracion() { return fechaExpiracion; }
    public boolean isActivo() { return activo; }
    public LocalDate getFechaCreacion() { return fechaCreacion; }

    // SETTERS 

    public void setUsername(String username) { this.username = username; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setRol(String rol) { this.rol = rol; }
    public void setSaldo(double saldo) { this.saldo = saldo; }
    public void setIntentosFallidos(int intentosFallidos) { this.intentosFallidos = intentosFallidos; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }
    public void setPin(String pin) { this.pin = pin; }
    public void setFechaExpiracion(String fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }
    public void setActivo(boolean activo) { this.activo = activo; }

    // MÉTODOS DE NEGOCIO BÁSICOS 

    public boolean estaBloqueado() {
        return intentosFallidos >= 3;
    }

    public void incrementarIntentosFallidos() {
        intentosFallidos++;
    }

    public void resetearIntentos() {
        intentosFallidos = 0;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    // SERIALIZACIÓN

    public String toFileString() {
        return id + "," + username + "," + nombre + "," + apellido + "," +
               rol + "," + saldo + "," + intentosFallidos + "," + cedula + "," +
               numeroCuenta + "," + pin + "," + fechaExpiracion;
    }

    public static User fromFileString(String linea) {
        String[] p = linea.split(",");
        if (p.length < 11) return null;

        try {
            return new User(
                    Integer.parseInt(p[0]),
                    p[1], p[2], p[3],
                    p[4],
                    Double.parseDouble(p[5]),
                    Integer.parseInt(p[6]),
                    p[7], p[8], p[9], p[10]
            );
        } catch (Exception e) {
            return null;
        }
    }

    // UTILIDADES ESTÁTICAS 

    public static int generarNuevoID() {
        return idCounter.getAndIncrement();
    }

    public static void inicializarIdCounter(int maxId) {
        idCounter.set(maxId + 1);
    }

    private static String generarFechaExpiracion() {
        return LocalDate.now()
                .plusYears(5)
                .format(DateTimeFormatter.ofPattern("MM/yy"));
    }

    // DEBUG

    @Override
    public String toString() {
        return String.format(
                "ID:%d | Usuario:%s | Nombre:%s %s | Cuenta:%s | Rol:%s | Saldo:$%.2f",
                id, username, nombre, apellido, numeroCuenta, rol, saldo
        );
    }
}
