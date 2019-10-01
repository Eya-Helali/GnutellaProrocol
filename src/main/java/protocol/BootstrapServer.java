package protocol;

import protocol.utils.RandomizeUtils;

import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BootstrapServer implements Runnable {

  private static final int MAX_LOAD = 10;

  private final int port;
  private List<String> superPeersList;

  private ServerSocket ss;
  private ExecutorService exec;

  public BootstrapServer(int port) {

    this.port = port;
    this.superPeersList = new ArrayList<>();
    this.exec = Executors.newFixedThreadPool(MAX_LOAD);
  }

  public boolean registerPeer(String peerInfo) {
    if(!this.superPeersList.contains(peerInfo)) {
      this.superPeersList.add(peerInfo);
      return true;
    }
    else
      return false;
  }

  public String getBootstrapIp() {
    return ss.getInetAddress().getHostAddress();
  }

  public int getBootstrapPort() {
    return ss.getLocalPort();
  }

  @Override
  public void run() {

    try {

      ss = new ServerSocket(port);

      while (!Thread.currentThread().isInterrupted()) {

        Socket soc = ss.accept();
        exec.submit(() -> {

          DataOutputStream dOut = null;

          try {

            dOut = new DataOutputStream(soc.getOutputStream());

            //sendAllExceptSender peer informations
            String msg = RandomizeUtils.getInstance().getRandomElementFromList(superPeersList);

            dOut.writeUTF(msg);
            dOut.flush();

          }catch(Exception e) {
            Thread.currentThread().interrupt();
          }finally {
            try {
              dOut.close();
              soc.close();
            }catch(Exception e) {
              Thread.currentThread().interrupt();
            }
          }
        });

      }

    }catch (Exception e) {
      Thread.currentThread().interrupt();
    }
  }

  public void shutdown() {
    exec.shutdown();
    Thread.currentThread().interrupt();
  }
}