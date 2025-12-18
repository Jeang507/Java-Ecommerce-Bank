package service;

import model.User;

public class AuthService {

    private UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public synchronized User autenticar(String usuarioOCuenta, String pinEncriptado) {
        User u = userService.buscarPorUsernameOCuenta(usuarioOCuenta);

        if (u == null) return null;
        if (u.estaBloqueado()) return null;

        if (u.getPin().equals(pinEncriptado)) {
            u.resetearIntentos();
            userService.guardarUsuarios();
            return u;
        } else {
            u.incrementarIntentosFallidos();
            userService.guardarUsuarios();
            return null;
        }
    }
}
