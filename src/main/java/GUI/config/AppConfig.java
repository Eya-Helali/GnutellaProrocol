package GUI.config;

public final class AppConfig {

    //views ids
    public static final String WELCOME_VIEW_ID= "WelcomeView";
    public static final String SETTINGS_VIEW_ID = "SettingsView";
    public static final String OVERLAY_NETWORK_VIEW_ID = "OverlayNetworkView";
    public static final String CONSOLE_LOG_VIEW_ID = "ConsoleLogView";

    //views files
    public static final String PATH_TO_XML = "/views/";
    public static final String WELCOME_VIEW = "welcomeView.fxml";
    public static final String SETTINGS_VIEW = "settingsView.fxml";
    public static final String OVERLAY_NETWORK_VIEW = "overlayNetworkView.fxml";
    public static final String CONSOLE_LOG_VIEW = "consoleLogView.fxml";

    //screen dimensions
    public static final double MAIN_WINDOW_HEIGHT = 800;
    public static final double MAIN_WINDOW_WIDTH = 800;
    public static final double CONSOLE_LOG_HEIGHT = 800;
    public static final double CONSOLE_LOG_WIDTH = 250;

    //message types
    public static final String MESSAGE_PING = "Ping";
    public static final String MESSAGE_PONG = "Pong";

    //protocols names
    public static final String PROTOCOL_IMPL_1_NAME = "Standard";
    public static final String PROTOCOL_IMPL_2_NAME = "PongCache";
    public static final String PROTOCOL_IMPL_3_NAME = "PingReduce";

    //utils
    public static final int INITIAL_RANDOM_SEED = 123;
    public static final String INITIAL_PEER_ID = "1";

    //network
    public static final int DEFAULT_PEER_TCP_PORT = 10000;
    public static final int BOOTSTRAP_SERVER_PORT = 20000;
    public static final String DEFAULT_PEER_ADDRESS = "localhost";
    public static final int DEFAULT_TIMEOUT_SOCKET = 200000;

    //
    public static final int CONSOLE_LOG_DELAY_MSG = 50;
}