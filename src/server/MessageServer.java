package server;

public class MessageServer {
    public static void main(String[] args) {
        Thread s1 = new Thread(new ServerInstance(5001, 0));
        Thread s2 = new Thread(new ServerInstance(5002, 0));
        Thread s3 = new Thread(new ServerInstance(5003, 0));

        s1.start();
        s2.start();
        s3.start();
    }
}
