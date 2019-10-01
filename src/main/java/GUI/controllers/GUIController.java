package GUI.controllers;

public interface GUIController {

    void setTextInConsole(String text);
    void setPeerStatusColor(String peerId, String color);
    void setPeerVisible(String peerId, boolean visible);
    void setPeerGlowEffect(String peerId, boolean glow, String color);
    void linkPeersConnection(String peerId1, String peerId2);
    void updateMeasurements(int type, long counter);
    void plotMeasurements();
}