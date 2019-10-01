package protocol.connections;

import protocol.messages.MessageBase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public abstract class ConnectionHandlerBase implements ConnectionHandler {

    private BlockingQueue<MessageBase> rcvMessageQueue;
    private final String peerId;
    private final String localIp;
    private final int localPort;
    private final int maxConnections;

    private List<Connection> connections;

    public ConnectionHandlerBase(
            BlockingQueue<MessageBase> rcvMessageQueue,
            String peerId,
            String localIp,
            int localPort,
            int maxConnections) {

        this.rcvMessageQueue = rcvMessageQueue;
        this.peerId = peerId;
        this.localIp = localIp;
        this.localPort = localPort;
        this.maxConnections = maxConnections;

        this.connections = new ArrayList<>();
    }

    public BlockingQueue<MessageBase> getRcvMessageQueue() {
        return this.rcvMessageQueue;
    }

    public String getPeerId() { return this.peerId; }

    public String getLocalIp() {
        return this.localIp;
    }

    public int getLocalPort() {
        return this.localPort;
    }

    public int getMaxConnections() {
        return this.maxConnections;
    }

    public List<Connection> getConnections() {
        return this.connections;
    }

    public Connection findConnection(String ip, int port) {

        Connection conn = null;

            int idx = 0;
            while (idx < connections.size()) {
                conn = connections.get(idx);
                if (conn.getRemoteIp().equals(ip) &&
                        conn.getRemotePort() == port) {
                    break;
                } else
                    ++idx;
            }
            return conn;
    }

    //bootstrap servers

    //create connections
}