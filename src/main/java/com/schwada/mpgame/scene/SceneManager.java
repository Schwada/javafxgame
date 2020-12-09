package com.schwada.mpgame.scene;

import com.schwada.mpgame.logic.GameState;
import com.schwada.mpgame.net.Client;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Manages switching scenes and injecting
 * dependencies into the controllers
 * of the application
 *
 * @author David Schwam
 */
public class SceneManager {
    private final static Logger logger = Logger.getLogger(SceneManager.class.getName());

    private final Stage primaryStage;
    private final GameState state;
    private final Client client;

    /**
     * Constructor which prepares scene loader
     * @param primaryStage Stage on which scenes will be switched
     * @param state Game state that will be injected into controllers
     * @param client Client that will be injected into controllers
     */
    public SceneManager(Stage primaryStage, GameState state, Client client) {
        logger.info("Instantiating scene manager...");
        this.client = client;
        this.state = state;
        this.primaryStage = primaryStage;
        this.primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
        });
    }

    /**
     * Switches current scene to specified type
     * @param type Type of scene to switch to.
     */
    public void setScene(SceneType type) {
        logger.info("Scene manager - switching scene to type: " + type +" and loading controller");
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(this.getControllerFactory());
        Parent root = null;
        try {
            loader.setLocation(getClass().getResource(type.name().toLowerCase() + ".fxml"));
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("Scene manager - failed switching to scene type: " + type + " - unable to find scene definition");
            return;
        }

        this.primaryStage.setScene(new Scene(root,1000,500));
        this.primaryStage.sizeToScene();
        this.primaryStage.setResizable(false);
        this.primaryStage.setTitle(type.name().toLowerCase());
        this.primaryStage.show();
        this.primaryStage.setMinWidth(this.primaryStage.getWidth());
        this.primaryStage.setMinHeight(this.primaryStage.getHeight());
        this.primaryStage.setOnCloseRequest(event -> {
            if(state.getUid() != null && !state.getUid().isEmpty()) {
                this.client.sendPacket(new String[]{"2" + state.getUid()});
            }
            //Platform.exit();
        });
        logger.info("Scene manager - switched scene to type: " + type);
    }

    /**
     * Gets the factory for controllers
     * that contains dependency injection
     * @return controller factory
     */
    private Callback<Class<?>, Object> getControllerFactory() {
        return type -> {
            try {
                List<Object> parameters = new ArrayList<>();
                for (Constructor<?> c : type.getConstructors()) {
                    for (int i = 0; i <= c.getParameterCount() - 1; i++) {
                        if (c.getParameterTypes()[i] == SceneManager.class) parameters.add(this);
                        if (c.getParameterTypes()[i] == Client.class) parameters.add(this.client);
                        if (c.getParameterTypes()[i] == GameState.class) parameters.add(this.state);
                    }
                    logger.info("Scene manager - Injecting dependencies into controller");
                    return c.newInstance(parameters.get(0), parameters.get(1), parameters.get(2));
                }
                return type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("Scene manager - failed injecting dependencies into scene");
            }
            return null;
        };
    }

}
