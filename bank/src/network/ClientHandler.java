package network;

import model.User;
import service.UserService;
import service.AuthService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private UserService userService;
    private AuthService authService;
    private ServerLogger logger;
    private User usuario;

    public ClientHandler(
            Socket socket,
            UserService userService,
            AuthService authService,
            ServerLogger logger
    ) {
        this.socket = socket;
        this.userService = userService;
        this.authService = authService;
        this.logger = logger;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            logger.log("Cliente conectado");

            String line;
            while ((line = in.readLine()) != null) {
                logger.log("Comando recibido: " + line);
                procesarComando(line);
            }

        } catch (Exception e) {
            logger.log("Cliente desconectado");
        } finally {
            cerrar();
        }
    }

    private void procesarComando(String input) {
        String[] parts = input.split(":", 2);
        String command = parts[0];
        String args = parts.length > 1 ? parts[1] : "";

        switch (command) {

            case "LOGIN":
                login(args);
                break;

            case "REGISTER":
                register(args);
                break;

            case "BALANCE":
                balance();
                break;

            case "DEPOSIT":
                deposit(args);
                break;

            case "WITHDRAW":
                withdraw(args);
                break;

            default:
                out.println("ERROR:Comando no reconocido");
                logger.log("Comando desconocido: " + command);
        }
    }

    private void login(String args) {
        String[] data = args.split(",");
        if (data.length < 2) {
            out.println("LOGIN_FAIL:Formato incorrecto");
            return;
        }

        User u = authService.autenticar(data[0], data[1]);
        if (u == null) {
            out.println("LOGIN_FAIL:Credenciales invalidas");
            logger.log("Login fallido para usuario " + data[0]);
        } else {
            usuario = u;
            out.println("LOGIN_OK:" + usuario.getRol() + ":" + usuario.getId());
            logger.log("Login exitoso: " + usuario.getUsername());
        }
    }

    private void register(String args) {
        boolean ok = userService.registrarDesdeComando(args);
        if (ok) {
            out.println("REGISTER_OK");
            logger.log("Usuario registrado");
        } else {
            out.println("REGISTER_FAIL");
            logger.log("Error registrando usuario");
        }
    }

    private void balance() {
        if (usuario == null) {
            out.println("ERROR:No autenticado");
            return;
        }
        out.println("BALANCE:" + usuario.getSaldo());
        logger.log("Consulta de saldo de " + usuario.getUsername());
    }

    private void deposit(String args) {
        if (usuario == null) {
            out.println("ERROR:No autenticado");
            return;
        }

        try {
            double amount = Double.parseDouble(args);
            userService.depositar(usuario, amount);
            out.println("DEPOSIT_OK");
            logger.log("Deposito de " + amount + " a " + usuario.getUsername());
        } catch (Exception e) {
            out.println("DEPOSIT_FAIL");
        }
    }

    private void withdraw(String args) {
        if (usuario == null) {
            out.println("ERROR:No autenticado");
            return;
        }

        try {
            double amount = Double.parseDouble(args);
            boolean ok = userService.retirar(usuario, amount);
            if (ok) {
                out.println("WITHDRAW_OK");
                logger.log("Retiro de " + amount + " por " + usuario.getUsername());
            } else {
                out.println("WITHDRAW_FAIL");
                logger.log("Retiro fallido por fondos insuficientes");
            }
        } catch (Exception e) {
            out.println("WITHDRAW_FAIL");
        }
    }

    private void cerrar() {
        try {
            if (socket != null) socket.close();
        } catch (Exception ignored) {
        }
    }
}
