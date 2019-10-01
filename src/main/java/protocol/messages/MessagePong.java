package protocol.messages;

import java.io.Serializable;

public class MessagePong extends MessageBase implements Serializable {

  private static final long serialVersionUID = 3L;

  /**
   * IP Original Host
   *
   * IP of responding host
   */
  private final String originalIp;

  /**
   * Port Original Host
   *
   * Port of responding host
   */
  private final int originalPort;

  /**
   * Number of files
   *
   * The number of files that the host is sharing on the network
   */
  private final long nFiles;

  /**
   * Number of Kbs shared
   *
   * The number of kilobytes of data that the host is sharing on the network
   */
  private final long nKBytes;

  public MessagePong(
          String guid,
          int type,
          int ttl,
          int hops,
          String senderId,
          String senderIp,
          int senderPort,
          String originalIp,
          int originalPort,
          long nFiles,
          long nKBytes) {

    super(guid, type, ttl, hops, senderId, senderIp, senderPort);

    this.originalIp = originalIp;
    this.originalPort = originalPort;
    this.nFiles = nFiles;
    this.nKBytes = nKBytes;
  }

  public String getOriginal_ip() {

    return this.originalIp;
  }

  public int getOriginalPort() {

    return this.originalPort;
  }

  public long getnFiles() {

    return this.nFiles;
  }

  public long getnKBytes() {

    return this.nKBytes;
  }
}