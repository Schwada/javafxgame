package com.schwada.mpgame;

import com.schwada.mpgame.logic.GameState;
import com.schwada.mpgame.net.Client;
import com.schwada.mpgame.scene.SceneManager;

import com.schwada.mpgame.scene.SceneType;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.logging.Logger;

/**
 *
 */
public class Main extends Application {
    private final static Logger logger = Logger.getLogger(Main.class.getName());

    private Client client;
    private GameState state;
    private static boolean servermode;

    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tT] [%4$s] %5$s%6$s%n");
        logger.info("Multiplayer game, David Schwam 2020");
        logger.info("Launching game...");
        servermode = Boolean.parseBoolean(args[0]);
        launch();
    }

    @Override
    public void init() throws Exception {
        logger.info("Setting up state...");
        this.state = new GameState();
        logger.info("Starting client...");
        this.client = new Client(this.state, servermode);
        Thread clientThread = new Thread(this.client);
        clientThread.setDaemon(true);
        clientThread.start();
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Setting stage...");
        SceneManager manager = new SceneManager(primaryStage, state, client);
        if(servermode) {
            manager.setScene(SceneType.SERVER);
        }else{
            manager.setScene(SceneType.LOGIN);
        }
    }
}
