package model;

public class UsuarioEcommerce {

    private int id;
    private String username;
    private String password; // contraseña encriptada
    private String rol;      // cliente o admin
    private String correo;

    public UsuarioEcommerce(int id, String username, String password, String rol, String correo) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.correo = correo;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRol() {
        return rol;
    }

    public String getCorreo() {
        return correo;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    @Override
    public String toString() {
        return id + "," + username + "," + password + "," + rol + "," + correo;
    }

    public static UsuarioEcommerce fromString(String line) {
        String[] p = line.split(",");
        if (p.length < 5) return null;

        int id = Integer.parseInt(p[0]);
        String username = p[1];
        String password = p[2];
        String rol = p[3];
        String correo = p[4];

        return new UsuarioEcommerce(id, username, password, rol, correo);
    }
}
