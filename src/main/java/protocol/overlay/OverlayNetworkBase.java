package protocol.overlay;

import GUI.config.AppConfig;
import GUI.controllers.GUIController;
import protocol.BootstrapServer;
import protocol.peers.Peer;
import protocol.utils.RandomizeUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class OverlayNetworkBase implements OverlayNetwork {

  private final int maxConnections;
  private final int nPeers;
  private final int nBootstrapPeer;
  private final int initialNetworkDimension;

  private Map<String,Peer> peers = new HashMap<>();
  private Map<String,List<String>> peerConnectionsMap = new HashMap<>();
  private List<String> peersOffline = new ArrayList<>();
  private List<String> peersOnline = new ArrayList<>();
  private List<String> bootstrapPeerList = new ArrayList<>();

  private GUIController guiController;

  private BootstrapServer bootstrapServerRunnable;

  public OverlayNetworkBase(
          int maxConnections,
          int nPeers,
          int nBootstrapPeer,
          int initialNetworkDimension,
          GUIController guiController) {

    this.maxConnections = maxConnections;
    this.nPeers = nPeers;
    this.nBootstrapPeer = nBootstrapPeer;
    this.initialNetworkDimension = initialNetworkDimension;
    this.guiController = guiController;
    this.bootstrapServerRunnable = new BootstrapServer(AppConfig.BOOTSTRAP_SERVER_PORT);
  }

  @Override
  public void run() {

    initPeers();
    initOverlayNetworkPeers();
    connectOverlayNetworkPeers();
    initBootstrap();
    startSimulation();
    shutdown();
  }

  private void initPeers() {

    try {

      TimeUnit.MILLISECONDS.sleep(1000);
      getGuiController().setTextInConsole("Initialize peers..");

      for (int i = 0; i < getnPeers(); i++) {
        String id = String.valueOf(i+1);
        getPeersOffline().add(id);
      }

    }catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void initOverlayNetworkPeers()  {

    try {

      /* randomly choose fixed number of peers and put online */
      TimeUnit.MILLISECONDS.sleep(AppConfig.CONSOLE_LOG_DELAY_MSG);
      getGuiController().setTextInConsole("Build initial overlay network..");
      TimeUnit.MILLISECONDS.sleep(AppConfig.CONSOLE_LOG_DELAY_MSG);
      getGuiController().setTextInConsole("Select randomly " + getInitialNetworkDimension() + " peers..");
      TimeUnit.MILLISECONDS.sleep(AppConfig.CONSOLE_LOG_DELAY_MSG);
      getGuiController().setTextInConsole("Putting peers online..");
      TimeUnit.MILLISECONDS.sleep(AppConfig.CONSOLE_LOG_DELAY_MSG);

      for (int i = 0; i < getInitialNetworkDimension(); i++) {

        String id = RandomizeUtils.getInstance().getRandomElementFromList(getPeersOffline());
        getPeersOffline().remove(id);
        getPeersOnline().add(id);

        //init peer
        Peer peer = instatiatePeer(id);
        getPeers().put(id, peer);
        Thread t = new Thread(peer);
        t.setName("Peer "+id+" Thread");
        t.start();

        //init connections map
        peerConnectionsMap.put(id, new ArrayList<>());

        //gui
        getGuiController().setPeerStatusColor(id, "GREEN");
        getGuiController().setPeerVisible(id, true);
        getGuiController().setTextInConsole("Peer " + id + " is online");

        TimeUnit.MILLISECONDS.sleep(AppConfig.CONSOLE_LOG_DELAY_MSG);
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void connectOverlayNetworkPeers() {

    try {

      /* randomly connect peers */
      getGuiController().setTextInConsole("Connect peers together..");

      int index = 0;
      while (index < getInitialNetworkDimension()) {

        //select an initial overlay peer from peers list
        String selectedPeerId = getPeersOnline().get(index);
        Peer peer = peers.get(selectedPeerId);

        //select randomly number of connections that selected peer will be use
        //(not too high, not go over half of max)
        int peerNumberConnections = RandomizeUtils.getInstance().getRandomInteger(
                getMaxConnections() / 10) + 1;

        //make randomly connections with other online peers
        int nConn = 0; //make almost 1 connection

        while (nConn < peerNumberConnections) {

          //active connections (passive connections are considered)
          int nPeerActiveConn = peer.getActiveConnections();

          //peer reach connections limit (exit)
          if (nPeerActiveConn == getMaxConnections()) {
            break;
          }

          //select randomly a target peer
          String targetPeerId = RandomizeUtils.getInstance().getRandomElementFromList(getPeersOnline());
          Peer targetPeer = peers.get(targetPeerId);

          //avoid self connect and check if target peer have a connection with peer
          if (targetPeerId != selectedPeerId && !isPeerAlreadyConnected(selectedPeerId,targetPeerId)) {

            //check if target peer reach max connections
            int targetPeerNumberConnections = targetPeer.getActiveConnections();

            if (targetPeerNumberConnections < getMaxConnections()) {

              //try connection
              boolean connected = peer.connect(
                      targetPeerId,
                      targetPeer.getLocalIp(),
                      targetPeer.getLocalPort());

              if(connected) {

                //update connections map
                peerConnectionsMap.get(selectedPeerId).add(targetPeerId);
                peerConnectionsMap.get(targetPeerId).add(selectedPeerId);
              }

              ++nConn;
            }
          }
        }
        ++index;
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private boolean isPeerAlreadyConnected(String peerId, String targetPeerId) {

    List<String> peerConnectedList = peerConnectionsMap.get(peerId);
    if(peerConnectedList.contains(targetPeerId)) {
      return true;
    }
    else return false;
  }

  private void initBootstrap() {

    try {

      //gui
      guiController.setTextInConsole("Init Overlay Network completed");
      TimeUnit.MILLISECONDS.sleep(AppConfig.CONSOLE_LOG_DELAY_MSG);
      guiController.setTextInConsole("Initialize Bootstrap Server..");
      TimeUnit.MILLISECONDS.sleep(AppConfig.CONSOLE_LOG_DELAY_MSG);
      guiController.setTextInConsole("Select randomly " + nBootstrapPeer + " peers..");

      //init bootstrap
      int index_bootstrap = 0;
      while (index_bootstrap < nBootstrapPeer) {

        String selectedPeerId = RandomizeUtils.getInstance().getRandomElementFromList(peersOnline);

        if (!bootstrapPeerList.contains(selectedPeerId)) {

          bootstrapPeerList.add(selectedPeerId);
          ++index_bootstrap;

          //register on bootstrap server
          Peer peer = peers.get(selectedPeerId);
          String peerInfo = selectedPeerId+"@"+peer.getLocalIp()+"@"+peer.getLocalPort();
          bootstrapServerRunnable.registerPeer(peerInfo);

          //gui
          TimeUnit.MILLISECONDS.sleep(AppConfig.CONSOLE_LOG_DELAY_MSG);
          guiController.setTextInConsole(selectedPeerId + " registered to Bootstrap");
        }
      }

      //gui
      guiController.setTextInConsole("Bootstrap ready");

      Thread bootstrapThread = new Thread(bootstrapServerRunnable);
      bootstrapThread.setName("Bootstrap Server");
      bootstrapThread.start();

      //gui
      guiController.setTextInConsole("Bootstrap @port: " +
              AppConfig.BOOTSTRAP_SERVER_PORT);

    }catch(Exception e) {
      //
    }
  }

  private void startSimulation() {

    getGuiController().setTextInConsole("Start Simulation..");

    try {

      int index = 0;
      while (index < 1) { //sostituisci 1 con nPeers //TODO

        //select randomly a peer not in overlay
        String selectedPeerId = RandomizeUtils.getInstance().getRandomElementFromList(peersOffline);
        peersOffline.remove(selectedPeerId);
        peersOnline.add(selectedPeerId);

        //init peer
        Peer peer = instatiatePeer(selectedPeerId);
        getPeers().put(selectedPeerId, peer);

        //gui
        getGuiController().setPeerStatusColor(selectedPeerId, "GREEN");
        getGuiController().setPeerVisible(selectedPeerId, true);
        getGuiController().setTextInConsole("Peer " + selectedPeerId + " is online");

        TimeUnit.MILLISECONDS.sleep(200);

        //connect to Bootstrap Server
        peer.connectToBootstrap(
                bootstrapServerRunnable.getBootstrapIp(),
                bootstrapServerRunnable.getBootstrapPort());





        //provvisorio.. lo shutdown chiude le socket quando finisce la simulation
        Thread.sleep(200000);


        //da spostare
        getGuiController().plotMeasurements();


        index++;
      }

    }catch(Exception e){
      //
    }
  }

  public int getMaxConnections() { return maxConnections; }
  public int getnPeers() { return nPeers; }
  public int getnBootstrapPeer() { return nBootstrapPeer; }
  public int getInitialNetworkDimension() { return initialNetworkDimension; }

  public Map<String,Peer> getPeers() { return peers; }
  public List<String> getPeersOffline() { return peersOffline; }
  public List<String> getPeersOnline() { return peersOnline; }

  public GUIController getGuiController() { return guiController; }

  public void shutdown() {

    //shutdown peers (with their connection handler)
    peers.forEach( (k,v) ->  {
      v.shutdown();
    });

    //shutdown bootstrap server
    bootstrapServerRunnable.shutdown();

    Thread.currentThread().interrupt();
  }
}