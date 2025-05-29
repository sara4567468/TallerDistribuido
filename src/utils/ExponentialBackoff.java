package utils;

import java.io.*;
import java.net.*;

public class ExponentialBackoff {
    public static void sendWithRetry(String host, int port, String message) {
        int retries = 0;
        int maxRetries = 5;
        int backoff = 1000;

        while (retries < maxRetries) {
            try (Socket socket = new Socket(host, port);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println(message);
                System.out.println("✅ Enviado a puerto " + port + ": " + message);
                return;
            } catch (IOException e) {
                retries++;
                System.out.println("⚠️ Fallo al enviar a puerto " + port + ". Reintentando...");
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException ignored) {
                }
                backoff *= 2;
            }
        }
        System.out.println("❌ Falló el envío a " + port + " tras " + retries + " intentos.");
    }
}
