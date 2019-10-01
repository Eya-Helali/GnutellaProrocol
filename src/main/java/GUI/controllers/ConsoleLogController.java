package GUI.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;


public class ConsoleLogController {

  @FXML
  private TextArea consoleLogTextArea;

  public void setTextInConsoleLogTextArea(String text) {

    Platform.runLater(() -> {
      consoleLogTextArea.appendText(text+"\n");
    });
  }
}
