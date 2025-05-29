package main;

import server.ServerInstance;
import client.MessageClient;

public class MainApp {
    public static void main(String[] args) throws InterruptedException {

        ServerInstance server1 = new ServerInstance(5001, 0);
        ServerInstance server2 = new ServerInstance(5002, 10000);
        ServerInstance server3 = new ServerInstance(5003, 0);

        Thread t1 = new Thread(server1);
        Thread t2 = new Thread(server2);
        Thread t3 = new Thread(server3);

        t1.start();
        t2.start();
        t3.start();

        Thread.sleep(2000);

        System.out.println("? Servidores corriendo. El servidor 5002 se apagará a los 10 segundos...");

        Thread.sleep(12000);

        System.out.println("? Ejecutando cliente para enviar mensajes balanceados con retry y circuit breaker...");

        MessageClient.main(null);

        Thread.sleep(2000);

        t1.interrupt();
        t3.interrupt();

        System.out.println("Programa terminado.");
    }
}
