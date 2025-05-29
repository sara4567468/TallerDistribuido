package utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class CircuitBreakerBackoff {
    private static final int MAX_FAILURES = 3;
    private static final long BLOCK_TIME_MS = 10000;
    private static final int MAX_RETRIES = 5;

    private Map<Integer, Integer> failureCount = new HashMap<>();
    private Map<Integer, Long> blockUntil = new HashMap<>();

    public synchronized void sendWithRetry(String host, int port, String message) {
        if (isBlocked(port)) {
            System.out.println("❌ Servidor " + port + " bloqueado. Mensaje alternativo enviado.");
            return;
        }

        int retries = 0;
        int backoff = 1000;

        while (retries < MAX_RETRIES) {
            try (Socket socket = new Socket(host, port);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println(message);
                System.out.println("✅ Enviado a puerto " + port + ": " + message);
                failureCount.put(port, 0);
                return;
            } catch (IOException e) {
                retries++;
                System.out.println("⚠️ Fallo al enviar a puerto " + port + ". Reintentando (" + retries + ")...");
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException ignored) {
                }
                backoff *= 2;
            }
        }

        System.out.println("❌ Falló el envío a " + port + " tras " + retries + " intentos.");
        incrementFailures(port);
    }

    public synchronized boolean isBlocked(int port) {
        if (!blockUntil.containsKey(port))
            return false;
        if (System.currentTimeMillis() > blockUntil.get(port)) {
            blockUntil.remove(port);
            failureCount.put(port, 0);
            System.out.println("🔓 Servidor " + port + " desbloqueado.");
            return false;
        }
        return true;
    }

    private synchronized void incrementFailures(int port) {
        int fails = failureCount.getOrDefault(port, 0) + 1;
        failureCount.put(port, fails);
        if (fails >= MAX_FAILURES) {
            blockUntil.put(port, System.currentTimeMillis() + BLOCK_TIME_MS);
            System.out.println("⛔ Servidor " + port + " bloqueado por " + (BLOCK_TIME_MS / 1000) + " segundos.");
        }
    }
}
