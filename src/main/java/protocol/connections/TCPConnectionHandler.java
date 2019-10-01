package protocol.connections;

import protocol.config.ProtocolConfig;
import protocol.messages.MessageBase;
import protocol.messages.MessageHandshake;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class TCPConnectionHandler extends ConnectionHandlerBase {

  private ServerSocket ss;

  private List<Integer> activeConnections;
  //private Thread timeoutSeeker;

  public TCPConnectionHandler(
          BlockingQueue<MessageBase> rcvMessageQueue,
          String peerId,
          String localIp,
          int localPort,
          int maxConnections) {

    super(rcvMessageQueue, peerId, localIp, localPort, maxConnections);
  }

  @Override
  public void init() {

    try {
      ss = new ServerSocket(getLocalPort());
    } catch (IOException e) {
      e.printStackTrace();
    }

    //periodically check timeouts
//    timeoutSeeker = new Thread(() -> {
//      while (!Thread.currentThread().isInterrupted()) {
//        try {
//          //remove from list connections
//          Thread.sleep(2000);
//            int index = 0;
//            while (index < connections.size()) {
//              TCPConnection conn = connections.get(index);
//              if (!conn.isAlive()) {
//                connections.remove(conn);
//              }
//              index++;
//            }
//        } catch (InterruptedException e) {
//          //
//        }
//
//      }
//    });
//    timeoutSeeker.init();
  }

  @Override
  public void shutdown() {
    //timeoutSeeker.interrupt();

    //close active sockets
    getConnections().forEach(c -> {
      c.shutdown();
    });

    //close server socket
    try {
      ss.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    Thread.currentThread().interrupt();
  }

  @Override
  public boolean connect(String ip, int port) {

    //check max connections and if there is already a connection with target host
    if (getConnections().size() < getMaxConnections()) {
      try {
        InetAddress ia = InetAddress.getByName(ip);
        Socket socket = null;
        try {
          socket = new Socket(ia, port);
        } catch (Exception e) {
          e.printStackTrace();
        }

        //make a connection wrapper
        TCPConnection tcpConnection = new TCPConnection(
                getRcvMessageQueue(),
                socket);

        tcpConnection.init();
        getConnections().add(tcpConnection);

        //send welcome
        int ack = tcpConnection.send(
                new MessageHandshake(
                        "",
                        ProtocolConfig.PAYLOAD_DESCRIPTOR_WELCOME,
                        ProtocolConfig.TTL_DEFAULT,
                        ProtocolConfig.HOPS_DEFAULT,
                        getPeerId(),
                        getLocalIp(),
                        getLocalPort()
                )
        );

        if(ack == ProtocolConfig.PAYLOAD_DESCRIPTOR_OK)
          return true;

      } catch (IOException e) {
        e.printStackTrace();
        return false;
      }
    }
    return false;
  }

  @Override
  public boolean disconnect(String ip, int port) {
    Connection conn = findConnection(ip, port);
    if (conn != null) {
      conn.shutdown();
      getConnections().remove(conn);
      return true;
    } else return false;
  }

  @Override
  public int send(String ip, int port, MessageBase m) {
    if (m != null) {
      Connection conn = findConnection(ip, port);
      if (conn != null) {
        int ack = conn.send(m);
        return ack;
      }
    }
    return -1;
  }

  @Override
  public void sendAllExceptSender(String senderIp, int senderPort, MessageBase m) {
    if (m != null) {
      getConnections().forEach(c -> {
        if(c.getRemoteIp() != senderIp && c.getRemotePort() != senderPort) {
          c.send(m);
        }
      });
    }
  }

  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        Socket soc = ss.accept();
        if(getConnections().size() < getMaxConnections()) {

          //make a connection wrapper
          TCPConnection conn = new TCPConnection(
                  getRcvMessageQueue(),
                  soc);

          conn.init();
          getConnections().add(conn);
        }
        else {
          soc.close();
        }
      }catch (Exception e) {
        Thread.currentThread().interrupt();
      }
    }
  }
}