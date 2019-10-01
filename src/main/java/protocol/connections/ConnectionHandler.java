package protocol.connections;

import protocol.messages.MessageBase;

public interface ConnectionHandler extends Runnable {

    void init();
    void shutdown();
    boolean connect(String ip, int port);
    boolean disconnect(String ip, int port);
    int send(String ip, int port, MessageBase m);
    void sendAllExceptSender(String senderIp, int senderPort, MessageBase m);
}