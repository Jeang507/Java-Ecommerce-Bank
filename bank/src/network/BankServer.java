package network;

import service.UserService;
import service.AuthService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BankServer {

    private final int PORT = 24000;
    private boolean running;
    private ServerSocket serverSocket;
    private ExecutorService pool;

    private UserService userService;
    private AuthService authService;
    private ServerLogger logger;

    public BankServer(UserService userService,
                      AuthService authService,
                      ServerLogger logger) {

        this.userService = userService;
        this.authService = authService;
        this.logger = logger;
        this.pool = Executors.newCachedThreadPool();
    }

    public void start() {
        running = true;

        try {
            serverSocket = new ServerSocket(PORT);
            logger.log("Servidor escuchando en puerto " + PORT);

            while (running) {
                Socket client = serverSocket.accept();
                logger.log("Cliente conectado desde " + client.getInetAddress());
                pool.execute(
                    new ClientHandler(client, userService, authService, logger)
                );
            }

        } catch (IOException e) {
            if (running) {
                logger.log("Error del servidor: " + e.getMessage());
            }
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
            pool.shutdown();
            logger.log("Servidor detenido");
        } catch (IOException e) {
            logger.log("Error cerrando servidor: " + e.getMessage());
        }
    }
}
