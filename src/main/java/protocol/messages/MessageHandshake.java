package protocol.messages;

import java.io.Serializable;

public class MessageHandshake extends MessageBase implements Serializable{

  private static final long serialVersionUID = 1L;

  public MessageHandshake(
          String guid,
          int type,
          int ttl,
          int hops,
          String senderId,
          String senderIp,
          int senderPort) {

    super(guid, type, ttl, hops, senderId, senderIp, senderPort);
  }
}