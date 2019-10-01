package protocol.connections;

import protocol.messages.MessageBase;

public interface Connection extends Runnable {

  void init();
  void shutdown();
  boolean isAlive();
  int send(MessageBase m);
  String getRemoteIp();
  int getRemotePort();
  void setRemoteIp(String remoteIp);
  void setRemoteServerPort(int remotePort);
}