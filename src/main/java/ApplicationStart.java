import GUI.config.AppConfig;
import GUI.controllers.OverlayNetworkController;
import GUI.controllers.SettingsController;
import GUI.controllers.WelcomeController;
import GUI.handlers.ScreensController;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ApplicationStart extends Application {

  //gui controllers
  private OverlayNetworkController overlayNetworkViewController;
  private SettingsController settingsController;
  private WelcomeController welcomeController;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) {

    //gui
    try {

      ScreensController mainContainer = new ScreensController();

      //loading views
      welcomeController = (WelcomeController) mainContainer.loadScreen(AppConfig.WELCOME_VIEW_ID, AppConfig.PATH_TO_XML + AppConfig.WELCOME_VIEW);
      settingsController = (SettingsController) mainContainer.loadScreen(AppConfig.SETTINGS_VIEW_ID, AppConfig.PATH_TO_XML + AppConfig.SETTINGS_VIEW);
      overlayNetworkViewController = (OverlayNetworkController) mainContainer.loadScreen(AppConfig.OVERLAY_NETWORK_VIEW_ID, AppConfig.PATH_TO_XML + AppConfig.OVERLAY_NETWORK_VIEW);

      //dependency
      settingsController.setOverlayNetworkController(overlayNetworkViewController);

      //setting first view
      mainContainer.setScreen(AppConfig.WELCOME_VIEW_ID);

      Group root = new Group();
      root.getChildren().addAll(mainContainer);
      stage.setScene(new Scene(root, AppConfig.MAIN_WINDOW_WIDTH, AppConfig.MAIN_WINDOW_HEIGHT));
      stage.setTitle("P2P Ping/Pong Simulator");
      stage.setResizable(false);
      stage.show();

      //put in the middle of screen
      Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
      stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
      stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);

    } catch (Exception e) {
      //
    }
  }

  @Override
  public void stop() {
    //exit application
    welcomeController.shutdown();
    settingsController.shutdown();
    overlayNetworkViewController.shutdown();
  }
}