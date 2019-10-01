package protocol.overlay;

import protocol.peers.Peer;

public interface OverlayNetwork extends Runnable {

    Peer instatiatePeer(String peerId);
}