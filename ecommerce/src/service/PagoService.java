package service;

import model.Producto;
import network.ConexionBanco;
import util.FacturaTXTGenerator;
import util.PasswordEncryption;

import java.util.List;

public class PagoService {

    // Procesa el pago completo y retorna un código de resultado
    public static String procesarPago(
            String usuarioBanco,
            String pin,
            double total,
            List<Producto> productos
    ) {

        ConexionBanco conexion = new ConexionBanco();

        if (!conexion.conectar()) {
            return "ERROR_CONEXION";
        }

        String pinEncriptado = PasswordEncryption.encryptPassword(pin);
        String login = conexion.login(usuarioBanco, pinEncriptado);

        if (login == null || !login.startsWith("LOGIN_OK")) {
            return "ERROR_LOGIN";
        }

        String respuesta = conexion.retirar(total);

        if (respuesta == null) {
            return "ERROR_SERVIDOR";
        }

        String r = respuesta.toUpperCase();

        if (r.contains("FAIL") || r.contains("INSUF")) {
            return "FONDOS_INSUFICIENTES";
        }

        FacturaTXTGenerator.generarFactura(usuarioBanco, productos, total);
        return "OK";
    }
}
