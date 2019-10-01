package GUI.entities;

import javafx.scene.shape.Line;

public class PeerLink {

  private final String peer1;
  private final String peer2;
  private final Line link;

  public PeerLink(String peer1, String peer2, Line link) {
    this.peer1 = peer1;
    this.peer2 = peer2;
    this.link = link;
  }

  public String getPeer1() {
    return this.peer1;
  }

  public String getPeer2() {
    return this.peer2;
  }

  public Line getLink() {
    return this.link;
  }
}