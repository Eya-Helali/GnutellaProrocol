package protocol.overlay;

import GUI.config.AppConfig;
import GUI.controllers.GUIController;
import protocol.peers.Peer;
import protocol.peers.PeerStandard;

import java.net.InetAddress;

public class OverlayNetworkStandard extends OverlayNetworkBase {

    private int port = AppConfig.DEFAULT_PEER_TCP_PORT;

    public OverlayNetworkStandard(
            int maxConnections,
            int nPeers,
            int nBootstrapPeer,
            int initialNetworkDimension,
            GUIController guiController) {

        super(maxConnections, nPeers, nBootstrapPeer, initialNetworkDimension, guiController);
    }

    @Override
    public Peer instatiatePeer(String peerId) {

        String ip = "";

        try {

            ip = InetAddress.getLocalHost().getHostAddress();

        } catch (Exception e) {
            //
        }

        Peer peer = new PeerStandard(peerId, ip, port, getMaxConnections(), getGuiController());
        peer.setStatus(true);
        peer.init();
        port++;

        return peer;
    }
}