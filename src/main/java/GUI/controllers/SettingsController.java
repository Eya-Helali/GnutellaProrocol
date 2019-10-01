package GUI.controllers;

import GUI.config.AppConfig;
import GUI.entities.Settings;
import GUI.handlers.ControlledScreen;
import GUI.handlers.ScreensController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable, ControlledScreen {

    ScreensController screensController;

    OverlayNetworkController overlayNetworkController;

    public void setOverlayNetworkController(OverlayNetworkController overlayNetworkController) {
        this.overlayNetworkController = overlayNetworkController;
    }

    @FXML
    protected void handleSubmitLaunchButton(javafx.event.ActionEvent event) {

        //init overlay network interface
        screensController.setScreen(AppConfig.OVERLAY_NETWORK_VIEW_ID);

        //collect params from gui
        Settings settings = new Settings(
            50,
                10,
                50,
                10,
                20
        );

        overlayNetworkController.initializeOverlayNetworkSettings(settings);
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