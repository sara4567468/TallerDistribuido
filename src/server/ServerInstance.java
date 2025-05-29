package server;

import java.io.*;
import java.net.*;

public class ServerInstance implements Runnable {
    private int port;
    private volatile boolean running = true;
    private long shutdownAfterMs;

    public ServerInstance(int port, long shutdownAfterMs) {
        this.port = port;
        this.shutdownAfterMs = shutdownAfterMs;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor en puerto " + port + " iniciado.");
            long startTime = System.currentTimeMillis();
            while (running && !Thread.currentThread().isInterrupted()) {
                if (shutdownAfterMs > 0 && System.currentTimeMillis() - startTime > shutdownAfterMs) {
                    System.out.println("Nodo " + port + " apagándose para simular caída.");
                    break;
                }
                try {
                    serverSocket.setSoTimeout(1000);
                    Socket clientSocket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String message = in.readLine();
                    System.out.println("Recibido en puerto " + port + ": " + message);
                    clientSocket.close();
                } catch (SocketTimeoutException e) {

                } catch (IOException e) {
                    System.out.println("Error en el servidor de puerto " + port + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Servidor en puerto " + port + " detenido.");
        }
    }
}
