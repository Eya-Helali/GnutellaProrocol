package protocol.connections;

import GUI.config.AppConfig;
import protocol.config.ProtocolConfig;
import protocol.messages.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TCPConnection extends ConnectionBase {

  //private Thread senderWorkerTCP;
  //private Thread receiverWorkerTCP;
  private ObjectOutputStream os = null;
  private ObjectInputStream is = null;
  private Socket socket;
  private String remoteIp;
  private int remoteServerPort;

  private final Lock lock = new ReentrantLock();
  private final Condition isAcked = lock.newCondition();
  private int ack = ProtocolConfig.PAYLOAD_DESCRIPTOR_OK;

  protected TCPConnection(
          BlockingQueue<MessageBase> rcvMessageQueue,
          Socket socket) {

    super(rcvMessageQueue);
    this.socket = socket;
  }

  @Override
  public void init() {

    try {

      //timeout socket
      this.socket.setSoTimeout(AppConfig.DEFAULT_TIMEOUT_SOCKET);

      //streams
      this.os = new ObjectOutputStream(socket.getOutputStream());
      this.is = new ObjectInputStream(socket.getInputStream());

    }catch(Exception e) {
      e.printStackTrace();
    }

    //initWorkers();
    Thread t = new Thread(this);
    t.setName("Connection "+ socket.getLocalSocketAddress().toString() +
            " <--> "+ socket.getRemoteSocketAddress().toString());
    t.start();
  }

  @Override
  public void shutdown() {
    try {
      is.close();
      os.close();
      socket.close();
      //senderWorkerTCP.interrupt();
      //receiverWorkerTCP.interrupt();
    }catch(Exception e) {
      //
    }
    Thread.currentThread().interrupt();
  }

  @Override
  public boolean isAlive() {
    try {
      return socket.getKeepAlive();
    }catch(SocketException e) {
      //
      return false;
    }
  }

  @Override
  public int send(MessageBase msg) {
    try {

        switch (msg.getType()) {

          case ProtocolConfig.PAYLOAD_DESCRIPTOR_PING:

            os.writeInt(ProtocolConfig.PAYLOAD_DESCRIPTOR_PING);
            break;

          case ProtocolConfig.PAYLOAD_DESCRIPTOR_PONG:

            os.writeInt(ProtocolConfig.PAYLOAD_DESCRIPTOR_PONG);
            break;

          case ProtocolConfig.PAYLOAD_DESCRIPTOR_WELCOME:

            os.writeInt(ProtocolConfig.PAYLOAD_DESCRIPTOR_WELCOME);
            break;
        }

        os.writeObject(msg);
        os.flush();

        //wait ack (come from readInt in run() method)
      lock.lock();
        isAcked.await();
        lock.unlock();

      return ack;

    }catch(Exception e) {
      e.printStackTrace();
    }

    return -1;
  }

  public String getRemoteIp() {

    return this.remoteIp;
  }

  public void setRemoteIp(String remoteIp) {

    this.remoteIp = remoteIp;
  }

  public int getRemotePort() {

    return this.remoteServerPort;
  }

  public void setRemoteServerPort(int remoteServerPort) {

    this.remoteServerPort = remoteServerPort;
  }

  private void initWorkers() {

//
//      try {
//
//        while (!Thread.currentThread().isInterrupted()) {
//          MessageBase msg = getSndMessageQueue().take();
//          os.writeObject(msg);
//          os.flush();
//        }
//
//      } catch (Exception e) {
//        Thread.currentThread().interrupt();
//      }
//    });
//    senderWorkerTCP.start();

//    receiverWorkerTCP = new Thread(() -> {
//
//      try {
//
//        while (!Thread.currentThread().isInterrupted()) {
//          MessageBase msg = (MessageBase) is.readObject();
//          getRcvMessageQueue().put(msg);
//        }
//
//      } catch (Exception e) {
//        Thread.currentThread().interrupt();
//      }
//    });
//    receiverWorkerTCP.start();
  }

  @Override
  public void run() {

    while(!Thread.currentThread().isInterrupted()) {

      try {

          int msgType = is.readInt();

          if (msgType == ProtocolConfig.PAYLOAD_DESCRIPTOR_PING) {

            MessagePing messagePing = (MessagePing) is.readObject();
            getRcvMessageQueue().put(messagePing);

            //send ack
            os.writeInt(ProtocolConfig.PAYLOAD_DESCRIPTOR_OK);
            os.writeObject(new MessageHandshake(
                    "",
                    ProtocolConfig.PAYLOAD_DESCRIPTOR_OK,
                    ProtocolConfig.TTL_DEFAULT,
                    ProtocolConfig.HOPS_DEFAULT,
                    "",
                    socket.getInetAddress().getHostAddress(),
                    socket.getLocalPort()
            ));
            os.flush();

          } else if (msgType == ProtocolConfig.PAYLOAD_DESCRIPTOR_PONG) {

            MessagePong messagePong = (MessagePong) is.readObject();
            getRcvMessageQueue().put(messagePong);

            //send ack
            os.writeInt(ProtocolConfig.PAYLOAD_DESCRIPTOR_OK);
            os.writeObject(new MessageHandshake(
                    "",
                    ProtocolConfig.PAYLOAD_DESCRIPTOR_OK,
                    ProtocolConfig.TTL_DEFAULT,
                    ProtocolConfig.HOPS_DEFAULT,
                    "",
                    socket.getInetAddress().getHostAddress(),
                    socket.getLocalPort()
            ));
            os.flush();

          } else if (msgType == ProtocolConfig.PAYLOAD_DESCRIPTOR_WELCOME) {

            MessageHandshake messageHandshake = (MessageHandshake) is.readObject();
            this.remoteIp = messageHandshake.getSenderIp();
            this.remoteServerPort = messageHandshake.getSenderPort();
            //getRcvMessageQueue().put(messageHandshake);

            //send ack
            os.writeInt(ProtocolConfig.PAYLOAD_DESCRIPTOR_OK);
            os.writeObject(new MessageHandshake(
                    "",
                    ProtocolConfig.PAYLOAD_DESCRIPTOR_OK,
                    ProtocolConfig.TTL_DEFAULT,
                    ProtocolConfig.HOPS_DEFAULT,
                    "",
                    socket.getInetAddress().getHostAddress(),
                    socket.getLocalPort()
            ));
            os.flush();

          } else if( msgType == ProtocolConfig.PAYLOAD_DESCRIPTOR_OK) {

            MessageHandshake messageHandshake = (MessageHandshake) is.readObject();
            this.remoteIp = messageHandshake.getSenderIp();
            this.remoteServerPort = messageHandshake.getSenderPort();

            lock.lock();
            isAcked.signal();
            lock.unlock();
          }


      }catch(InterruptedException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();

      }catch (IOException e) {
        e.printStackTrace();

      }catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
  }
}