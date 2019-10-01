package protocol.peers;

import GUI.controllers.GUIController;
import protocol.config.ProtocolConfig;
import protocol.connections.Connection;
import protocol.messages.*;
import protocol.utils.RandomizeUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class PeerStandard extends PeerBase {

  public PeerStandard(
          String peerId,
          String localIp,
          int localPort,
          int maxConnections,
          GUIController guiController) {

    super(peerId, localIp, localPort, maxConnections, guiController);
  }

  @Override
  public void init() {

    //init connection handler
    getTcpConnectionHandler().init();

    //start connection handler
    Thread connectionHandlerThread = new Thread(getTcpConnectionHandler());
    connectionHandlerThread.setName("Peer "+getPeerId()+" Connection Handler Thread");
    connectionHandlerThread.start();
  }

  @Override
  public void shutdown() {

    //shutdown connection handler
    getTcpConnectionHandler().shutdown();

    Thread.currentThread().interrupt();
  }

  @Override
  public void sendAllExceptSender(MessageBase message) {

    String senderId = message.getSenderId();
    String senderIp = message.getSenderIp();
    int senderPort = message.getSenderPort();

    //put current peer informations inside message
    message.setSenderId(getPeerId());
    message.setSenderIp(getLocalIp());
    message.setSenderPort(getLocalPort());

    if(message.getTtl() > 0) {

      //sendAllExceptSender on all connections except the sender
      getTcpConnectionHandler().sendAllExceptSender(senderIp, senderPort, message);
    }
  }

  @Override
  public void receivePing(MessagePing messagePing) {

    try {

      if (messagePing != null) {

        //increase hop
        messagePing.increaseHops();

        //decrease ttl
        messagePing.decreaseTTL();

        //check TTL
        if (messagePing.getTtl() == 0) return;

        //check if hash map already contains ping
        if(getMessagesConnectionsMap().containsKey(messagePing.getGuid()))

          return;

        else {

          //add ping guid to hash map with connection (peerId@ip@port)
          String connection =
                  messagePing.getSenderId()+"@" +
                  messagePing.getSenderIp()+"@" +
                  messagePing.getSenderPort();

          getMessagesConnectionsMap().put(
                  messagePing.getGuid(),
                  connection
          );
        }

        /* answer with a pong */

        //generate pong
        MessagePong messagePong = new MessagePong(
                messagePing.getGuid(),
                ProtocolConfig.PAYLOAD_DESCRIPTOR_PONG,
                ProtocolConfig.TTL_DEFAULT,
                ProtocolConfig.HOPS_DEFAULT,
                getPeerId(),
                getLocalIp(),
                getLocalPort(),
                getLocalIp(),
                getLocalPort(),
                -1,
                -1
        );

        //send back pong
//        send(
//                messagePing.getSenderIp(),
//                messagePing.getSenderPort(),
//                messagePong
//        );

        //old routing info
        String oldPeerIp = messagePing.getSenderIp();
        int oldPeerPort = messagePing.getSenderPort();

        //change routing info
        messagePing.setSenderId(getPeerId());
        messagePing.setSenderIp(getLocalIp());
        messagePing.setSenderPort(getLocalPort());

        //redirect ping to neighbors
        redirectMessageToNeighbors(messagePing, oldPeerIp, oldPeerPort);
      }

    }catch(Exception e) {
      //
    }

  }

  /**
   * Redirect Ping to neighbors
   *
   * protocol max k neighbors for redirecting
   *
   * @param message
   */
  private void redirectMessageToNeighbors(MessageBase message, String oldPeerIp, int oldPeerPort) {

    if(message == null) return;

    //thread-safe with connections
    List<Connection> connectionsList =
            new CopyOnWriteArrayList<>(getTcpConnectionHandler().getConnections());

    //remove sender
    int i = 0;
    while(i < connectionsList.size()) {
      Connection connection = connectionsList.get(i);
      if(connection.getRemoteIp().equals(oldPeerIp) &&
              connection.getRemotePort() == oldPeerPort) break;
      else
        i++;
    }
    if(i == connectionsList.size()) return;
    else {
      connectionsList.remove(i);
    }

    //select randomly max k connections
    int idx = 0;
    while(idx < ProtocolConfig.MAX_REDIRECTS_NEIGHBORS && idx < connectionsList.size()) {

      Connection c = RandomizeUtils
              .getInstance()
              .getRandomElementFromList(connectionsList);

      //send message
      int ack = send(c.getRemoteIp(),c.getRemotePort(),message);

      try {
        TimeUnit.MILLISECONDS.sleep(500);
      }catch(Exception e) {
        //
      }

      //remove element from copy list
      connectionsList.remove(c);

      idx++;
    }
  }

  @Override
  public void receivePong(MessagePong messagePong) {


    //quando ricevo il pong che fo ?
    //
  }
}












