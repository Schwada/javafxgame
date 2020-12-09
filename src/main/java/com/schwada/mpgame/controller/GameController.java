package com.schwada.mpgame.controller;

import com.schwada.mpgame.logic.GameState;
import com.schwada.mpgame.logic.Input;
import com.schwada.mpgame.logic.entity.Bullet;
import com.schwada.mpgame.logic.entity.Entity;
import com.schwada.mpgame.logic.entity.Player;
import com.schwada.mpgame.net.Client;
import com.schwada.mpgame.net.PacketHelper;
import com.schwada.mpgame.scene.SceneManager;
import com.schwada.mpgame.scene.SceneType;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

public class GameController implements Initializable {

    @FXML
    public Canvas drawArea;

    private GraphicsContext graphics;
    private AnimationTimer gameLoop;

    private final SceneManager manager;
    private final GameState state;
    private final Client client;
    private final Input input;

    public GameController (SceneManager manager, Client client, GameState state) {
        this.manager = manager;
        this.client = client;
        this.state = state;
        this.input = new Input();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        graphics = drawArea.getGraphicsContext2D();

        drawArea.setFocusTraversable(true);
        drawArea.setOnKeyPressed(event -> { if (!input.containsKey(event.getCode().toString())) {input.putKey(event.getCode().toString(), true);} });
        drawArea.setOnKeyReleased(event -> input.removeKey(event.getCode().toString()));
        drawArea.setOnMousePressed(event ->input.setMousePress((int)event.getSceneX(),(int) event.getSceneY()));
        drawArea.setOnMouseReleased(event ->input.setMouseRelease());

        client.sendPacket(new String[]{ "3" });
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Thread t = new Thread(() -> {
                    update();
                    render();
                });
                Platform.runLater(t);
            }
        };
        gameLoop.start();
    }

    private void render() {
        graphics.setFill(Color.BLACK);
        graphics.fillRect(0, 0, 1000, 500);

        if(state.getAuthenticated() && state.getClientPlayer() != null) {
            graphics.setFill(Color.WHITE);
            graphics.fillText("score: " + state.getClientPlayer().score,10,15);
        }

        Vector<Entity> entities = state.getEntities();
        synchronized (entities) {
            Iterator<Entity> i = entities.iterator();
            while (i.hasNext()) {
                Entity entity = i.next();
                entity.draw(graphics);
            }
        }
    }

    private void update() {
        if(state.getClientPlayer() != null && state.getClientPlayer().health <= 0) {
            gameLoop.stop();
            state.setAuthenticated(false);
            this.state.setUid(null);
            this.state.setClientPlayer(null);
            this.state.setEntities(null);
            manager.setScene(SceneType.LOST);
        }

        StringBuilder sendData = new StringBuilder("3");
        sendData.append(state.getUid());
        sendData.append((input.isKey("W")) ? 1 : 0);
        sendData.append((input.isKey("S")) ? 1 : 0);
        sendData.append((input.isKey("D")) ? 1 : 0);
        sendData.append((input.isKey("A")) ? 1 : 0);
        sendData.append((input.isMouseClicked()) ? 1 : 0);
        sendData.append(PacketHelper.padRight(input.getMouseX(),3));
        sendData.append(PacketHelper.padRight(input.getMouseY(),3));

        client.sendPacket(new String[]{ sendData.toString() });
    }
}
