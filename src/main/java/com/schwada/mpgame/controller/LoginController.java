package com.schwada.mpgame.controller;


import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import com.schwada.mpgame.logic.GameState;
import com.schwada.mpgame.logic.entity.EntityType;
import com.schwada.mpgame.net.Client;
import com.schwada.mpgame.net.PacketHelper;
import com.schwada.mpgame.scene.SceneManager;
import com.schwada.mpgame.scene.SceneType;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {
    private final static Logger logger = Logger.getLogger(LoginController.class.getName());

    private final String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";

    @FXML
    public Button registerButton;
    @FXML
    public TextField lanField;
    @FXML
    public ColorPicker colorField;
    @FXML
    public ChoiceBox typeField;
    @FXML
    private Button loginButton;
    @FXML
    public TextField nicknameField;
    @FXML
    private Label errorText;


    private final SceneManager manager;
    private final GameState state;
    private final Client client;

    public LoginController (SceneManager manager, Client client, GameState state) {
        this.manager = manager;
        this.client = client;
        this.state = state;
    }

    public void handleLogin(ActionEvent actionEvent) {
        logger.info("LoginController - Handling login request");
        if(nicknameField.getText().isEmpty() || nicknameField.getText().length() > 10) {
            logger.info("LoginController - Login request failed, username missing or too big");
            errorText.setText("Please fill out a nickname or limit your nickname to 10 characters");
            return;
        }

        if(!lanField.getText().isEmpty()) {
            if(lanField.getText().matches(PATTERN)) {
                this.client.setServerIp(lanField.getText());
            } else {
                logger.info("LoginController - invalid ip address passed");
                errorText.setText("Invalid ip address passed");
                return;
            }
        }

        this.attemptLogin();
    }

    public void attemptLogin() {
        state.setAuthenticated(null);
        this.controlsEnabled(false);
        String[] packet = new String[]{
            "0" +
             PacketHelper.padRight(nicknameField.getText(),12) +
             PacketHelper.padRight(colorField.getValue().toString(),10) +
             EntityType.nameValueOf(typeField.getValue().toString()).numVal
        };
        Task<Void> loginAttempt = new Task<>() {
            @Override
            public Void call() throws Exception {
                client.sendPacket(packet);
                int timeout = 0;
                while (state.getAuthenticated() == null) {
                    TimeUnit.SECONDS.sleep(1);
                    timeout++;
                    if(timeout == 3) {
                        logger.info("Login request - failed - Could not connect to server, timeout");
                        state.setAuthenticated(false);
                    }
                }
                if (state.getAuthenticated().equals(true)) {
                    state.setRespawnPacket(packet);
                    Platform.runLater(() -> manager.setScene(SceneType.GAME));
                }
                if (state.getAuthenticated().equals(false)) Platform.runLater(() -> controlsEnabled(true));
                Platform.runLater(() -> errorText.setText("Timeout..nickname in use or the game server is not active"));
                return null;
            }
        };
        new Thread(loginAttempt).start();
    }

    private void controlsEnabled(boolean state) {
        logger.info("Login request - info - " + (state ? "Enabling" : "Disabling") + " controls");
        nicknameField.setEditable(state);
        colorField.setEditable(state);
        typeField.setDisable(!state);
        loginButton.setDisable(!state);
        errorText.setText((!state) ? "Loading..." : "invalid details"); //TODO server return reason
    }

}
