package GUI.controllers;

import GUI.config.AppConfig;
import GUI.entities.PeerLink;
import GUI.entities.Settings;
import GUI.handlers.ControlledScreen;
import GUI.handlers.ScreensController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import protocol.overlay.OverlayNetwork;
import protocol.overlay.OverlayNetworkBase;
import protocol.overlay.OverlayNetworkStandard;
import protocol.utils.RandomizeUtils;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class OverlayNetworkController implements Initializable, ControlledScreen, GUIController {

  private Map<String,Node> nodeMap;
  private List<PeerLink> connections;

  private Settings overlayNetworkSettings;

  private ConsoleLogController consoleLogController;

  ScreensController screensController;

  private RadioButton selectedRadioButton = null;

  @FXML
  private StackPane mainStackPane;
  @FXML
  private Pane peersPane;
  @FXML
  private Pane measurementsChartPane;
  private BarChart<String,Number> measurementsBarChart;
  @FXML
  private ToggleGroup toggleGroup;
  @FXML
  private RadioButton firstRadioButton;
  @FXML
  private RadioButton secondRadioButton;
  @FXML
  private RadioButton thirdRadioButton;
  @FXML
  private GridPane manageSimulationGridPane;
  @FXML
  private Button startSimulationButton;
  @FXML
  private Button randomOverlayButton;
  @FXML
  private TextField firstSimulationPingTextField;
  @FXML
  private TextField firstSimulationPongTextField;
  @FXML
  private TextField secondSimulationPingTextField;
  @FXML
  private TextField secondSimulationPongTextField;
  @FXML
  private TextField thirdSimulationPingTextField;
  @FXML
  private TextField thirdSimulationPongTextField;

  //
  @FXML
  public void randomOverlayButtonPressed() {

    //use another random seed
    RandomizeUtils.getInstance().changeSeed();
    RandomizeUtils.getInstance().initRandom();

    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information");
    alert.setContentText("New Random Overlay created!");

    alert.showAndWait();
  }

  @FXML
  public void startSimulationButtonPressed() {

    //select which implementation use
    selectedRadioButton = (RadioButton) toggleGroup.getSelectedToggle();
    if(selectedRadioButton == null) {
      Alert alert = new Alert(Alert.AlertType.WARNING);
      alert.setTitle("Warning");
      alert.setContentText("Select a protocol implementation!");

      alert.showAndWait();
      return;
    }
    String selectedImpl = selectedRadioButton.getText();

    //clean old peers panel
    peersPane.getChildren().clear();

    //init peers panel
    initPeersPane();

    //init random
    RandomizeUtils.getInstance().initRandom();

    OverlayNetwork overlayNetwork = null;

    switch(selectedImpl) {

      case "Standard":

        overlayNetwork = new OverlayNetworkStandard(
                overlayNetworkSettings.getMaxConnections(),
                overlayNetworkSettings.getnPeers(),
                overlayNetworkSettings.getnBootstrapPeer(),
                overlayNetworkSettings.getInitialNetworkDimension(),
                this);
        break;
    }

    //init overlay network
    Thread overlayNetworkManager = new Thread(overlayNetwork);
    overlayNetworkManager.setName("Overlay Network Manager");
    overlayNetworkManager.start();
  }

  @Override
  public void setScreenParent(ScreensController screenParent) {

    this.screensController = screenParent;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    nodeMap = new HashMap<>();
    connections = new ArrayList<>();
  }

  public void shutdown() {

  }

  public void initializeOverlayNetworkSettings(Settings settings) {

    this.overlayNetworkSettings = settings;

    //initPeersPane();
    //initMeasurementsBarChart();
    initMeasurementsTextBoxes();
    initRadioButtons();
    initConsoleLogWindow();
  }

  //TODO controllore per stoppare/chiudere ?

  @Override
  public void updateMeasurements(int type, long counter) {

    Platform.runLater(() -> {

      String simulation = selectedRadioButton.getText();

      switch (simulation) {

        case AppConfig.PROTOCOL_IMPL_1_NAME:
          if (type == 0) {
            long oldValue = Long.parseLong(firstSimulationPingTextField.getText());
            oldValue = oldValue + counter;
            firstSimulationPingTextField.setText(String.valueOf(oldValue));
          } else {
            long oldValue = Long.parseLong(firstSimulationPongTextField.getText());
            oldValue = oldValue + counter;
            firstSimulationPongTextField.setText(String.valueOf(oldValue));
          }
          break;

        case AppConfig.PROTOCOL_IMPL_2_NAME:
          if (type == 0) {
            long oldValue = Long.parseLong(secondSimulationPingTextField.getText());
            oldValue = oldValue + counter;
            secondSimulationPingTextField.setText(String.valueOf(oldValue));
          } else {
            long oldValue = Long.parseLong(secondSimulationPongTextField.getText());
            oldValue = oldValue + counter;
            secondSimulationPongTextField.setText(String.valueOf(oldValue));
          }
          break;

        case AppConfig.PROTOCOL_IMPL_3_NAME:
          if (type == 0) {
            long oldValue = Long.parseLong(thirdSimulationPingTextField.getText());
            oldValue = oldValue + counter;
            thirdSimulationPingTextField.setText(String.valueOf(oldValue));
          } else {
            long oldValue = Long.parseLong(thirdSimulationPongTextField.getText());
            oldValue = oldValue + counter;
            thirdSimulationPongTextField.setText(String.valueOf(oldValue));
          }
          break;
      }
    });
  }

  @Override
  public void plotMeasurements() {

    Platform.runLater(()-> {
      initMeasurementsBarChart();
    });
  }

  @Override
  public void linkPeersConnection(String peerId1, String peerId2) {
    Platform.runLater(() -> {

      Node node1 = nodeMap.get(peerId1);
      Node node2 = nodeMap.get(peerId2);

      //draw connection line
      Line line = new Line();
      line.setStartX(node1.getCircle().getCenterX());
      line.setStartY(node1.getCircle().getCenterY());
      line.setEndX(node2.getCircle().getCenterX());
      line.setEndY(node2.getCircle().getCenterY());
      line.setVisible(true);
      line.getStrokeDashArray().addAll(1d, 5d);
      peersPane.getChildren().add(line);

      PeerLink peerLink = new PeerLink(peerId1,peerId2,line);
      connections.add(peerLink);
    });
  }

  public void unlinkPeersConnection(String peerId1, String peerId2) {
    Platform.runLater(() -> {

      int index = 0;
      while(index < connections.size()) {
        PeerLink link = connections.get(index);
        if((link.getPeer1() == peerId1 && link.getPeer2() == peerId2) ||
                link.getPeer1() == peerId2 && link.getPeer2() == peerId1) {
          peersPane.getChildren().remove(link.getLink());
          connections.remove(link);
          break;
        }
        else
          index++;
      }
    });
  }

  @Override
  public void setTextInConsole(String text) {
    Platform.runLater(() -> {
      consoleLogController.setTextInConsoleLogTextArea(text);
    });
  }

  @Override
  public void setPeerVisible(String peerId, boolean visible) {
    Platform.runLater(() -> {
              nodeMap.get(peerId).getCircle().setVisible(visible);
              nodeMap.get(peerId).getText().setVisible(visible);
            }
    );
  }

  @Override
  public void setPeerStatusColor(String peerId, String color) {

    Platform.runLater(() -> {

      Node node = nodeMap.get(peerId);

      if (node != null) {

        setColor(node, color);
      }
    });
  }

  @Override
  public void setPeerGlowEffect(String peerId, boolean glow, String color) {

    Platform.runLater(() -> {

      if (peerId != null && !color.isEmpty()) {

        Node node = nodeMap.get(peerId);
        setColor(node, color);
        setGlow(node, glow);
      }
    });
  }

  public void setColor(Node node, String color) {

    if(node != null) {

      switch (color) {

        case "GREEN":
          node.getCircle().setFill(Color.GREEN);
          break;

        case "RED":
          node.getCircle().setFill(Color.RED);
          break;

        case "BLUE":
          node.getCircle().setFill(Color.AQUA);
          break;

        case "BLACK":
          node.getCircle().setFill(Color.BLACK);
          break;

        case "AQUA":
          node.getCircle().setFill(Color.AQUA);
          break;

        case "PURPE":
          node.getCircle().setFill(Color.PURPLE);
          break;

        default:
          node.getCircle().setFill(Color.GRAY);

      }
    }
  }

  public void setGlow(Node node, boolean active) {

    if(node != null) {

      if(active)
        node.getGlow().setLevel(0.5);
      else
        node.getGlow().setLevel(0.0);
    }
  }

  private void initPeersPane() {

    //init nodes
    int peerId = 1;
    double y = 20;
    for(int i = 0; i < 10; i++) {
      double x = 20;
      for(int j = 0; j < 16; j++) {

        //peer node
        Circle circle = new Circle(x,y,10);
        circle.setVisible(false);
        Glow glow = new Glow();
        glow.setLevel(0.0);
        circle.setEffect(glow);

        Text text = new Text(x-5,y+20, String.valueOf(peerId));
        text.setBoundsType(TextBoundsType.VISUAL);
        Font font = Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR, 12);
        text.setFont(font);
        text.setVisible(false);

        peersPane.getChildren().addAll(circle,text);

        Node node = new Node(circle,glow,text,peerId);
        nodeMap.put(String.valueOf(peerId),node);

        peerId++;
        x = x + 50;
      }
      y = y + 50;
    }
  }

  private void initMeasurementsBarChart() {

//      CategoryAxis xAxis = new CategoryAxis();
//      NumberAxis yAxis = new NumberAxis();
//      measurementsBarChart = new BarChart<>(xAxis, yAxis);
//
//      measurementsBarChart.setMaxWidth(measurementsChartPane.getMaxWidth());
//      measurementsBarChart.setMaxHeight(measurementsChartPane.getMaxHeight());
//      measurementsBarChart.setPrefWidth(measurementsChartPane.getMaxWidth());
//      measurementsBarChart.setPrefHeight(measurementsChartPane.getMaxHeight());
//      measurementsBarChart.setTitle("Ping/Pong Measurements");
//      xAxis.setLabel("MessageBase Type");
//
//      //
//      firstSimSeries.setName("Ping");
//      firstSimSeries.getData().add(new XYChart.Data(AppConfig.PROTOCOL_IMPL_1_NAME, 0));
//      firstSimSeries.getData().add(new XYChart.Data(AppConfig.PROTOCOL_IMPL_2_NAME, 0));
//      firstSimSeries.getData().add(new XYChart.Data(AppConfig.PROTOCOL_IMPL_3_NAME, 0));
//
//      //
//      secondSimSeries.setName("Pong");
//      secondSimSeries.getData().add(new XYChart.Data(AppConfig.PROTOCOL_IMPL_1_NAME, 0));
//      secondSimSeries.getData().add(new XYChart.Data(AppConfig.PROTOCOL_IMPL_2_NAME, 0));
//      secondSimSeries.getData().add(new XYChart.Data(AppConfig.PROTOCOL_IMPL_3_NAME, 0));
//
//      measurementsBarChart.getData().addAll(firstSimSeries, secondSimSeries);
//      measurementsChartPane.getChildren().add(measurementsBarChart);

      CategoryAxis xAxis = new CategoryAxis();
      NumberAxis yAxis = new NumberAxis();
      measurementsBarChart = new BarChart<>(xAxis, yAxis);

      measurementsBarChart.setMaxWidth(measurementsChartPane.getMaxWidth());
      measurementsBarChart.setMaxHeight(measurementsChartPane.getMaxHeight());
      measurementsBarChart.setPrefWidth(measurementsChartPane.getMaxWidth());
      measurementsBarChart.setPrefHeight(measurementsChartPane.getMaxHeight());
      measurementsBarChart.setTitle("Ping/Pong Measurements");
      xAxis.setLabel("MessageBase Type");

      //
      XYChart.Series firstSimSeries = new XYChart.Series();
      firstSimSeries.setName("Ping");
      firstSimSeries.getData().add(new XYChart.Data(AppConfig.PROTOCOL_IMPL_1_NAME,
              Double.parseDouble(firstSimulationPingTextField.getText())));

      firstSimSeries.getData().add(new XYChart.Data(AppConfig.PROTOCOL_IMPL_2_NAME,
              Double.parseDouble(secondSimulationPingTextField.getText())));

      firstSimSeries.getData().add(new XYChart.Data(AppConfig.PROTOCOL_IMPL_3_NAME,
              Double.parseDouble(thirdSimulationPingTextField.getText())));

      //
      XYChart.Series secondSimSeries = new XYChart.Series();
      secondSimSeries.setName("Pong");
      secondSimSeries.getData().add(new XYChart.Data(AppConfig.PROTOCOL_IMPL_1_NAME,
              Double.parseDouble(firstSimulationPongTextField.getText())));

      secondSimSeries.getData().add(new XYChart.Data(AppConfig.PROTOCOL_IMPL_2_NAME,
              Double.parseDouble(secondSimulationPongTextField.getText())));

      secondSimSeries.getData().add(new XYChart.Data(AppConfig.PROTOCOL_IMPL_3_NAME,
              Double.parseDouble(thirdSimulationPongTextField.getText())));

      measurementsBarChart.getData().addAll(firstSimSeries, secondSimSeries);
      measurementsChartPane.getChildren().add(measurementsBarChart);

  }

  private void initRadioButtons() {

    firstRadioButton.setText(AppConfig.PROTOCOL_IMPL_1_NAME);
    secondRadioButton.setText(AppConfig.PROTOCOL_IMPL_2_NAME);
    thirdRadioButton.setText(AppConfig.PROTOCOL_IMPL_3_NAME);
  }

  private void initMeasurementsTextBoxes() {

    firstSimulationPingTextField.setText(String.valueOf(0));
    secondSimulationPingTextField.setText(String.valueOf(0));
    thirdSimulationPingTextField.setText(String.valueOf(0));

    firstSimulationPongTextField.setText(String.valueOf(0));
    secondSimulationPongTextField.setText(String.valueOf(0));
    thirdSimulationPongTextField.setText(String.valueOf(0));
  }

  private void initConsoleLogWindow() {

    //init console log window
    try {

      FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(getClass().getResource(AppConfig.PATH_TO_XML + AppConfig.CONSOLE_LOG_VIEW));
      Scene consoleLogScene = new Scene(fxmlLoader.load(), AppConfig.CONSOLE_LOG_WIDTH, AppConfig.CONSOLE_LOG_HEIGHT);

      Stage consoleLogStage = new Stage();
      consoleLogStage.setTitle("Console Log");
      //hide console log window when press exit button
      consoleLogStage.setOnCloseRequest((WindowEvent event) -> {
        try {
          consoleLogStage.hide();
        }catch(Exception e) {
          //
        }
      });
      consoleLogStage.setScene(consoleLogScene);
      consoleLogStage.show();

      //screen position
      Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
      consoleLogStage.setX((primScreenBounds.getWidth() - AppConfig.MAIN_WINDOW_WIDTH) / 2 + AppConfig.MAIN_WINDOW_WIDTH);
      consoleLogStage.setY((primScreenBounds.getHeight() - AppConfig.MAIN_WINDOW_HEIGHT) / 2);

      //controller
      consoleLogController = fxmlLoader.getController();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  private class Node {

    private final Circle circle;
    private final Glow glow;
    private final Text text;
    private final int peerId;

    protected Node(
            Circle circle,
            Glow glow,
            Text text,
            int peerId) {

      this.circle = circle;
      this.glow = glow;
      this.text = text;
      this.peerId = peerId;
    }

    public Circle getCircle() { return this.circle; }
    public Glow getGlow() { return this.glow; }
    public Text getText() { return this.text; }
    public int getPeerId() { return this.peerId; }
  }
}
