package server;

import utils.CircuitBreakerBackoff;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadBalancer {
    private List<Integer> serverPorts;
    private AtomicInteger index = new AtomicInteger(0);
    private CircuitBreakerBackoff circuitBreaker;

    public LoadBalancer(List<Integer> serverPorts, CircuitBreakerBackoff circuitBreaker) {
        this.serverPorts = serverPorts;
        this.circuitBreaker = circuitBreaker;
    }

    public int getNextServer() {
        int tries = 0;
        while (tries < serverPorts.size()) {
            int current = index.getAndIncrement() % serverPorts.size();
            int port = serverPorts.get(current);
            if (!circuitBreaker.isBlocked(port)) {
                return port;
            }
            tries++;
        }

        return -1;
    }
}
