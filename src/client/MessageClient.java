package client;

import server.LoadBalancer;
import utils.CircuitBreakerBackoff;

import java.util.Arrays;
import java.util.List;

public class MessageClient {
    public static void main(String[] args) throws InterruptedException {
        List<Integer> ports = Arrays.asList(5001, 5002, 5003);
        CircuitBreakerBackoff circuitBreaker = new CircuitBreakerBackoff();
        LoadBalancer lb = new LoadBalancer(ports, circuitBreaker);

        for (int clientId = 1; clientId <= 3; clientId++) {
            int finalClientId = clientId;
            new Thread(() -> {
                for (int i = 1; i <= 10; i++) {
                    int port = lb.getNextServer();
                    if (port == -1) {
                        System.out.println("🚫 No hay servidores disponibles para enviar.");
                        return;
                    }
                    String message = "Cliente " + finalClientId + " - Mensaje #" + i;
                    circuitBreaker.sendWithRetry("localhost", port, message);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException ignored) {
                    }
                }
            }).start();
        }
    }
}
