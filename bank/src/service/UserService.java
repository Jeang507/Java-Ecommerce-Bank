package service;

import model.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UserService {

    private final String ARCHIVO_USUARIOS = "users_secure.txt";

    private Map<String, User> usuariosPorUsername = Collections.synchronizedMap(new HashMap<>());
    private Map<String, User> usuariosPorCuenta = Collections.synchronizedMap(new HashMap<>());

    public UserService() {
        cargarUsuarios();
    }

    public synchronized boolean registrarDesdeComando(String args) {
        try {
            String[] d = args.split(",");
            if (d.length < 6) return false;

            String username = d[0];
            String nombre = d[1];
            String apellido = d[2];
            String cedula = d[3];
            String cuenta = d[4];
            String pin = d[5];

            if (usuariosPorUsername.containsKey(username)) return false;
            if (usuariosPorCuenta.containsKey(cuenta)) return false;

            User u = new User(username, nombre, apellido, cedula, pin, "cliente");
            u.setNumeroCuenta(cuenta);

            usuariosPorUsername.put(username, u);
            usuariosPorCuenta.put(cuenta, u);

            guardarUsuarios();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public synchronized void guardarUsuarios() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_USUARIOS))) {
            for (User u : usuariosPorUsername.values()) {
                pw.println(u.toFileString());
            }
        } catch (Exception ignored) {
        }
    }

    private void cargarUsuarios() {
        File f = new File(ARCHIVO_USUARIOS);

        if (!f.exists()) {
            crearAdminPorDefecto();
            return;
        }

        int maxId = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                User u = User.fromFileString(line);
                if (u != null) {
                    usuariosPorUsername.put(u.getUsername(), u);
                    usuariosPorCuenta.put(u.getNumeroCuenta(), u);
                    if (u.getId() > maxId) maxId = u.getId();
                }
            }
            User.inicializarIdCounter(maxId);
        } catch (Exception ignored) {
        }
    }

    private void crearAdminPorDefecto() {
        User admin = new User("admin", "Administrador", "Sistema", "0000", "", "admin");
        admin.setNumeroCuenta("00000000");
        usuariosPorUsername.put("admin", admin);
        usuariosPorCuenta.put("00000000", admin);
        guardarUsuarios();
    }

    public User buscarPorUsernameOCuenta(String valor) {
        if (usuariosPorUsername.containsKey(valor)) {
            return usuariosPorUsername.get(valor);
        }
        return usuariosPorCuenta.get(valor);
    }

    public synchronized void depositar(User u, double monto) {
        if (monto <= 0) return;
        u.setSaldo(u.getSaldo() + monto);
        guardarUsuarios();
    }

    public synchronized boolean retirar(User u, double monto) {
        if (monto <= 0) return false;
        if (u.getSaldo() < monto) return false;

        u.setSaldo(u.getSaldo() - monto);
        guardarUsuarios();
        return true;
    }
}
