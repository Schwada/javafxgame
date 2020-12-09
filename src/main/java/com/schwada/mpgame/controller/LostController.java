package com.schwada.mpgame.controller;

import com.schwada.mpgame.logic.DataHandler;
import com.schwada.mpgame.logic.GameState;
import com.schwada.mpgame.net.Client;
import com.schwada.mpgame.net.PacketHelper;
import com.schwada.mpgame.persistence.model.ScoreModel;
import com.schwada.mpgame.scene.SceneManager;
import com.schwada.mpgame.scene.SceneType;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class LostController implements Initializable {
    private final static Logger logger = Logger.getLogger(DataHandler.class.getName());

    private final SceneManager manager;
    private final GameState state;
    private final Client client;

    @FXML
    public Button respawnButton;
    @FXML
    public Button quitButton;

    @FXML
    public Label FirstUserNick;
    @FXML
    public Label FirstUserScore;
    @FXML
    public Label SecondUserNick;
    @FXML
    public Label SecondUserScore;
    @FXML
    public Label ThirdUserNick;
    @FXML
    public Label ThirdUserScore;

    public LostController (SceneManager manager, Client client, GameState state) {
        this.manager = manager;
        this.client = client;
        this.state = state;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.client.sendPacket(new String[]{"5"});
        int timeout = 0;
        while(this.state.getScores().isEmpty()){
            try {
                TimeUnit.SECONDS.sleep(1);
                if(timeout == 10) {
                    logger.info("Request score - failed - timeout");
                    return;
                }
                timeout++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        List<ScoreModel> scores = this.state.getScores();
        if(!scores.isEmpty()) {

            FirstUserNick.setText(scores.get(0).getUsername());
            FirstUserScore.setText(String.valueOf(scores.get(0).getScore()));

            SecondUserNick.setText(scores.get(1).getUsername());
            SecondUserScore.setText(String.valueOf(scores.get(1).getScore()));

            ThirdUserNick.setText(scores.get(2).getUsername());
            ThirdUserScore.setText(String.valueOf(scores.get(2).getScore()));

        }
    }

    public void handleRespawn(ActionEvent actionEvent){
        this.state.setAuthenticated(null);
        this.state.setUid(null);
        this.controlsEnabled(false);
        String[] packet = state.getRespawnPacket();
        Task<Void> loginAttempt = new Task<>() {
            @Override
            public Void call() throws Exception {
                client.sendPacket(packet);
                int timeout = 0;
                while (state.getAuthenticated() == null) {
                    TimeUnit.SECONDS.sleep(1);
                    timeout++;
                    if(timeout == 3) {
                        logger.info("Respawn request - failed - Could not connect to server, timeout");
                        state.setAuthenticated(false);
                    }
                }
                if (state.getAuthenticated().equals(true)) {
                    state.setRespawnPacket(packet);
                    Platform.runLater(() -> manager.setScene(SceneType.GAME));
                    manager.setScene(SceneType.GAME);
                    return null;
                }
                if (state.getAuthenticated().equals(false)) Platform.runLater(() -> controlsEnabled(true));
                return null;
            }
        };
        new Thread(loginAttempt).start();
    }

    private void controlsEnabled(boolean state) {
        logger.info("Respawn request - info - " + (state ? "Enabling" : "Disabling") + " controls");
        respawnButton.setDisable(!state);
        quitButton.setDisable(!state);
    }

    public void handleQuit(ActionEvent actionEvent) {
        this.state.setRespawnPacket(null);
        this.state.setUid(null);
        this.manager.setScene(SceneType.LOGIN);
    }
}
