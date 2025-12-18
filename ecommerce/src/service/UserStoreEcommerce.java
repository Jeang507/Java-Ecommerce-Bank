package service;

import model.UsuarioEcommerce;
import util.PasswordEncryption;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserStoreEcommerce {

    private static final String FILE = "usuarios_ecommerce.txt";
    private static final Map<String, UsuarioEcommerce> usuarios = new HashMap<>();
    private static int idCounter = 1;

    static {
        cargar();
    }

    public static void cargar() {
        usuarios.clear();
        File f = new File(FILE);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                UsuarioEcommerce u = UsuarioEcommerce.fromString(line);
                if (u != null) {
                    usuarios.put(u.getUsername(), u);
                    if (u.getId() >= idCounter) {
                        idCounter = u.getId() + 1;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error cargando usuarios: " + e.getMessage());
        }
    }

    public static synchronized boolean registrar(
            String username,
            String passwordEncriptado,
            String rol,
            String correo
    ) {

        if (usuarios.containsKey(username)) return false;

        UsuarioEcommerce u = new UsuarioEcommerce(
                idCounter++, username, passwordEncriptado, rol, correo
        );

        usuarios.put(username, u);
        guardar();
        return true;
    }

    public static void guardar() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            for (UsuarioEcommerce u : usuarios.values()) {
                pw.println(u.toString());
            }
        } catch (Exception e) {
            System.out.println("Error guardando usuarios: " + e.getMessage());
        }
    }

    public static UsuarioEcommerce login(String username, String password) {

        if (!usuarios.containsKey(username)) return null;

        UsuarioEcommerce u = usuarios.get(username);
        String encrypted = PasswordEncryption.encryptPassword(password);

        if (encrypted.equals(u.getPassword())) {
            return u;
        }
        return null;
    }

    public static UsuarioEcommerce getUsuario(String username) {
        return usuarios.get(username);
    }
}
