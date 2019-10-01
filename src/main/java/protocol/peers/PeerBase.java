package protocol.peers;

import GUI.config.AppConfig;
import GUI.controllers.GUIController;
import protocol.config.ProtocolConfig;
import protocol.connections.Connection;
import protocol.connections.TCPConnection;
import protocol.connections.TCPConnectionHandler;
import protocol.messages.*;
import protocol.utils.RandomizeUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static GUI.config.AppConfig.CONSOLE_LOG_DELAY_MSG;

public abstract class PeerBase implements Peer {

    private final String peerId;
    private final String localIp;
    private final int localPort;
    private final int maxConnections;
    private boolean online;

    private BlockingQueue<MessageBase> rcvMessageQueue;
    private TCPConnectionHandler tcpConnectionHandler;

    /**
     * Hash Map that bind MessageBase GUID to connection where come from
     */
    private Map<String,String> messagesConnectionsMap;

    private GUIController guiController;

    protected PeerBase(
            String peerId,
            String localIp,
            int localPort,
            int maxConnections,
            GUIController guiController) {
        this.peerId = peerId;
        this.localIp = localIp;
        this.localPort = localPort;
        this.maxConnections = maxConnections;
        this.guiController = guiController;

        online = false;
        rcvMessageQueue = new LinkedBlockingQueue<>();

        tcpConnectionHandler = new TCPConnectionHandler(
                rcvMessageQueue,
                peerId,
                localIp,
                localPort,
                maxConnections);

        messagesConnectionsMap = new HashMap<>();
    }

    @Override
    public void run() {

        while(!Thread.currentThread().isInterrupted()) {

            try {

                MessageBase m = getRcvMessageQueue().take();

                if(m != null) {

                    if(m.getType() == ProtocolConfig.PAYLOAD_DESCRIPTOR_PING) {

                        MessagePing messagePing = (MessagePing) m;

                        //gui
                        getGuiController().updateMeasurements(0,1);
                        getGuiController().setTextInConsole(getPeerId() + " received ping from " + messagePing.getSenderId());
                        TimeUnit.MILLISECONDS.sleep(AppConfig.CONSOLE_LOG_DELAY_MSG);
                        getGuiController().setPeerGlowEffect(getPeerId(), true, "RED");
                        TimeUnit.MILLISECONDS.sleep(AppConfig.CONSOLE_LOG_DELAY_MSG);
                        getGuiController().setPeerGlowEffect(getPeerId(), false, "GREEN");

                        receivePing(messagePing);
                    }
                    else if(m.getType() == ProtocolConfig.PAYLOAD_DESCRIPTOR_PONG) {

                        MessagePong messagePong = (MessagePong) m;

                        //gui
                        getGuiController().updateMeasurements(0,1);
                        getGuiController().setTextInConsole(getPeerId() + " received pong from " + messagePong.getSenderId());
                        TimeUnit.MILLISECONDS.sleep(AppConfig.CONSOLE_LOG_DELAY_MSG);
                        getGuiController().setPeerGlowEffect(getPeerId(), true, "BLACK");
                        TimeUnit.MILLISECONDS.sleep(AppConfig.CONSOLE_LOG_DELAY_MSG);
                        getGuiController().setPeerGlowEffect(getPeerId(), false, "GREEN");

                        receivePong(messagePong);
                    }

                    else if(m.getType() == ProtocolConfig.PAYLOAD_DESCRIPTOR_WELCOME) {

                        MessageHandshake messageHandshake = (MessageHandshake) m;

                        //gui
                        getGuiController().setTextInConsole(getPeerId() + " received welcome from " + messageHandshake.getSenderId());
                        TimeUnit.MILLISECONDS.sleep(AppConfig.CONSOLE_LOG_DELAY_MSG);
                        getGuiController().setPeerGlowEffect(getPeerId(), true, "PURPLE");
                        TimeUnit.MILLISECONDS.sleep(AppConfig.CONSOLE_LOG_DELAY_MSG);
                        getGuiController().setPeerGlowEffect(getPeerId(), false, "GREEN");
                    }
                }

            }catch(Exception e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public boolean connect(String peerId, String ip, int port) {

        boolean connected = false;

        if(ip != null && port > -1) {

            //connect to peer
            connected = getTcpConnectionHandler().connect(ip, port);

            if(connected) {

                try {

                    //gui (glow effect)
                    getGuiController().setPeerGlowEffect(getPeerId(), true, "GREEN");
                    TimeUnit.MILLISECONDS.sleep(CONSOLE_LOG_DELAY_MSG);
                    getGuiController().setPeerGlowEffect(getPeerId(), false, "GREEN");
                    TimeUnit.MILLISECONDS.sleep(CONSOLE_LOG_DELAY_MSG);
                    getGuiController().setPeerGlowEffect(peerId, true, "GREEN");
                    TimeUnit.MILLISECONDS.sleep(CONSOLE_LOG_DELAY_MSG);
                    getGuiController().setPeerGlowEffect(peerId, false, "GREEN");

                    getGuiController().linkPeersConnection(
                            getPeerId(),
                            peerId);
                    getGuiController().setTextInConsole("" +
                            getPeerId() +
                            " connected to " +
                            peerId);

                }catch(Exception e) {
                    //
                }
            }
        }
        return connected;
    }

    @Override
    public void connectToBootstrap(String ip, int port) {

        try {

            String msg = null;
            Socket socket = null;
            DataInputStream dIn = null;

            try {

                InetAddress ia = InetAddress.getByName(ip);

                socket = new Socket(ia, port);
                dIn = new DataInputStream(socket.getInputStream());

                //gui
                getGuiController().setTextInConsole(getPeerId() + " connected to Bootstrap");
                TimeUnit.MILLISECONDS.sleep(CONSOLE_LOG_DELAY_MSG);

                msg = dIn.readUTF();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    dIn.close();
                    socket.close();
                } catch (Exception e) {
                    //
                }
            }

            if (msg != null) {

                //id@ip@port peer obtained from bootstrap
                String[] parts = msg.split("@");
                String targetPeerId = parts[0];
                String targetPeerIp = parts[1];
                int targetPeerPort = Integer.parseInt(parts[2]);

                //gui
                getGuiController().setTextInConsole(getPeerId() + " get info about "+ targetPeerId);
                TimeUnit.MILLISECONDS.sleep(CONSOLE_LOG_DELAY_MSG);

                if (parts.length == 3) {

                    //connect (with welcome msg)
                    boolean connected = connect(targetPeerId, targetPeerIp, targetPeerPort);

                    //check
                    if(!connected) {
                        //TODO log
                        return;
                    }

                    //put remote ip/port obtained from bootstrap in the only connection
                    Connection connection = getTcpConnectionHandler().getConnections().get(0);
                    connection.setRemoteIp(targetPeerIp);
                    connection.setRemoteServerPort(targetPeerPort);

                    //generate ping
                    MessagePing messagePing = new MessagePing(
                            RandomizeUtils.getInstance().getRandomUID(),
                            ProtocolConfig.PAYLOAD_DESCRIPTOR_PING,
                            ProtocolConfig.TTL_DEFAULT,
                            ProtocolConfig.HOPS_DEFAULT,
                            getPeerId(),
                            getLocalIp(),
                            getLocalPort()
                    );

                    //populate hash map
                    //original ping sender bind guid to null
                    messagesConnectionsMap.put(
                            messagePing.getGuid(),
                            null
                    );

                    //send ping to peer obtained from bootstrap
                    send(targetPeerIp, targetPeerPort, messagePing);

                }
            }
        } catch (Exception e) {
            //
        }
    }

    @Override
    public int send(String ip, int port, MessageBase message) {

        if(message != null) {

            return getTcpConnectionHandler().send(ip,port,message);
        }
        else
            return -1;
    }

    @Override
    public int getActiveConnections(){

        return getTcpConnectionHandler().getConnections().size();
    }

    public String getPeerId() { return peerId; }
    public String getLocalIp() { return localIp; }
    public int getLocalPort() { return localPort; }
    public int getMaxConnections() { return maxConnections; }
    public GUIController getGuiController() { return this.guiController; }

    public void setStatus(boolean status) { online = status; }
    public boolean isOnline() { return online; }

    public BlockingQueue<MessageBase> getRcvMessageQueue() { return rcvMessageQueue; }
    public TCPConnectionHandler getTcpConnectionHandler() { return tcpConnectionHandler; }

    public Map<String,String> getMessagesConnectionsMap() { return messagesConnectionsMap; }
}