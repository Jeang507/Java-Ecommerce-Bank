package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConexionBanco {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean conectado = false;

    // Intenta establecer conexión con el servidor del banco
    public boolean conectar() {
        try {
            socket = new Socket("localhost", 24000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            conectado = true;
            return true;

        } catch (Exception e) {
            System.out.println("Error conectando al banco: " + e.getMessage());
            conectado = false;
            return false;
        }
    }

    public boolean isConectado() {
        return conectado;
    }

    // Envía credenciales al banco para autenticación
    public String login(String usuario, String pinEncriptado) {
        try {
            out.println("LOGIN:" + usuario + "," + pinEncriptado);
            return in.readLine();
        } catch (Exception e) {
            return "ERROR:No fue posible enviar login";
        }
    }

    // Solicita el saldo de la cuenta
    public String consultarSaldo() {
        try {
            out.println("BALANCE:");
            return in.readLine();
        } catch (Exception e) {
            return "ERROR:No se pudo obtener saldo";
        }
    }

    // Solicita un retiro de dinero
    public String retirar(double monto) {
        try {
            out.println("WITHDRAW:" + monto);
            return in.readLine();
        } catch (Exception e) {
            return "ERROR:No se pudo procesar el retiro";
        }
    }

    // Permite enviar comandos genéricos al servidor
    public String enviarComando(String cmd) {
        try {
            out.println(cmd);
            return in.readLine();
        } catch (Exception e) {
            return "ERROR:No se pudo procesar comando";
        }
    }

    // Cierra la conexión con el banco
    public void cerrar() {
        try {
            conectado = false;
            if (socket != null) socket.close();
        } catch (IOException e) {
            // Ignorado
        }
    }

    // Métodos simples usados por Pago si se requieren
    public void enviar(String mensaje) {
        out.println(mensaje);
        out.flush();
    }

    public String recibir() {
        try {
            return in.readLine();
        } catch (IOException e) {
            return null;
        }
    }
}
