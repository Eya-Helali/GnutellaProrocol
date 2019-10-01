package protocol.peers;

import protocol.connections.ConnectionHandler;
import protocol.connections.TCPConnection;
import protocol.connections.TCPConnectionHandler;
import protocol.messages.*;

public interface Peer extends Runnable {

    void init();
    void shutdown();
    void setStatus(boolean status);
    int getActiveConnections();
    TCPConnectionHandler getTcpConnectionHandler();
    String getLocalIp();
    int getLocalPort();
    boolean connect(String peerId, String ip, int port);
    void connectToBootstrap(String ip, int port);
    void sendAllExceptSender(MessageBase message);
    int send(String ip, int port, MessageBase message);
    void receivePing(MessagePing messagePing);
    void receivePong(MessagePong messagePong);

}