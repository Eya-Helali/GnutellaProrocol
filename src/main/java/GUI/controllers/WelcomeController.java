package GUI.controllers;

import GUI.config.AppConfig;
import GUI.handlers.ControlledScreen;
import GUI.handlers.ScreensController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class WelcomeController implements Initializable, ControlledScreen {

    ScreensController screensController;

    @FXML
    protected void handleSubmitStartButton(ActionEvent event) {

        //go to settings page
        screensController.setScreen(AppConfig.SETTINGS_VIEW_ID);
    }

    @Override
    public void setScreenParent(ScreensController screenParent) {

        this.screensController = screenParent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void shutdown() {

    }
}