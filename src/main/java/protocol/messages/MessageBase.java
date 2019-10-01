package protocol.messages;

import java.io.Serializable;

public abstract class MessageBase implements Message, Serializable {

  private static final long serialVersionUID = 5950169519310163575L;

  /**
   * Descriptor ID
   *
   * A string uniquely identifying the descriptor on the network
   */
  private final String guid;

  /**
   * Payload Descriptor
   *
   * 0 PING
   * 1 PONG
   * 2 WELCOME
   * 3 OK
   */
  private final int type;

  /**
   * Time To Live
   *
   * The number of times the descriptor will be forwarded
   */
  private int ttl;

  /**
   * Hops
   *
   * The number of times the descriptor has been forwarded
   */
  private int hops;

  /**
   * ID Sender (Simulation)
   *
   * ID of who routed the message
   */
  private String senderId;

  /**
   * IP Sender (Simulation)
   *
   * IP of who routed the message
   */
  private String senderIp;

  /**
   * Port Sender (Simulation)
   *
   * Port of who routed the message
   */
  private int senderPort;

  public MessageBase(
          String guid,
          int type,
          int ttl,
          int hops,
          String senderId,
          String senderIp,
          int senderPort) {

    this.guid = guid;
    this.type = type;
    this.ttl = ttl;
    this.hops = hops;
    this.senderId = senderId;
    this.senderIp = senderIp;
    this.senderPort = senderPort;
  }

  public String getGuid() {
    return this.guid;
  }

  public int getType() { return type; }

  public void decreaseTTL() {
    --this.ttl;
  }

  public void increaseHops() {
    ++this.hops;
  }

  public int getTtl() {
    return this.ttl;
  }

  public int getHops() {
    return this.hops;
  }
  public String getSenderId() { return senderId; }
  public String getSenderIp() { return senderIp; }
  public int getSenderPort() { return senderPort; }

  public void setSenderId(String senderId) {
    this.senderId = senderId;
  }
  public void setSenderIp(String senderIp) {
    this.senderIp = senderIp;
  }
  public void setSenderPort(int senderPort) {
    this.senderPort = senderPort;
  }
}