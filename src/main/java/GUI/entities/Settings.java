package GUI.entities;

public final class Settings {

  private final int maxConnections;
  private final int cacheDimension;
  private final int nPeers;
  private final int nBootstrapPeer;
  private final int initialNetworkDimension;

  public Settings(
          int maxConnections,
          int cacheDimension,
          int nPeers,
          int nBootstrapPeer,
          int initialNetworkDimension) {

    this.maxConnections = maxConnections;
    this.cacheDimension = cacheDimension;
    this.nPeers = nPeers;
    this.nBootstrapPeer = nBootstrapPeer;
    this.initialNetworkDimension = initialNetworkDimension;
  }

  public int getMaxConnections() { return this.maxConnections; }
  public int getCacheDimension() { return this.cacheDimension; }
  public int getnPeers() { return this.nPeers; }
  public int getnBootstrapPeer() { return this.nBootstrapPeer; }
  public int getInitialNetworkDimension() { return this.initialNetworkDimension; }
}