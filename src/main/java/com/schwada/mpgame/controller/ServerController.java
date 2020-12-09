package com.schwada.mpgame.controller;

import com.schwada.mpgame.logic.GameState;
import com.schwada.mpgame.logic.entity.Bullet;
import com.schwada.mpgame.logic.entity.Entity;
import com.schwada.mpgame.logic.entity.Player;
import com.schwada.mpgame.net.Client;
import com.schwada.mpgame.persistence.dao.ScoreDAOImpl;
import com.schwada.mpgame.persistence.model.ScoreModel;
import com.schwada.mpgame.scene.SceneManager;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

public class ServerController implements Initializable {

    private final static Logger logger = Logger.getLogger(ServerController.class.getName());

    private final SceneManager manager;
    private final GameState state;
    private final Client client;

    private Queue<Entity> bulletAddQueue = new LinkedList<>();
    private Vector<String> bulletAddScoreQueue = new Vector<>();
    private Vector<String> bulletActiveQueue = new Vector<>();

    @FXML
    public Label serverLocationText;
    @FXML
    public Button quitButton;

    public ServerController (SceneManager manager, Client client, GameState state) {
        this.manager = manager;
        this.client = client;
        this.state = state;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AnimationTimer loop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Thread t = new Thread(() -> {
                    update();
                });
                Platform.runLater(t);
            }
        };

        loop.start();
    }

    public void update () {
        Vector<Entity> entities = state.getEntities();
        synchronized (entities) {
            Iterator<Entity> i = entities.iterator();
            while (i.hasNext()) {
                Entity ent = i.next();
                if(ent instanceof Player) {
                    Player player = (Player) ent;
                    if (player.up && player.y > 0) player.y = player.y - player.movementSpeed;
                    if (player.down && player.y < 480) player.y = player.y + player.movementSpeed;
                    if (player.right && player.y < 980) player.x = player.x + player.movementSpeed;
                    if (player.left && player.x > 0) player.x = player.x - player.movementSpeed;

                    if (player.attack && !bulletActiveQueue.contains(player.uid)) {
                        float angle = (float) Math.toDegrees(Math.atan2(player.mouseY - player.y, player.mouseX - player.x));
                        bulletAddQueue.add(new Bullet(player, angle));
                        bulletActiveQueue.add(player.uid);
                    }

                    if (player.health <= 0) {
                        try {
                            ScoreDAOImpl dao = new ScoreDAOImpl();
                            ScoreModel score = new ScoreModel(((Player) ent).username, ((Player) ent).score);
                            if(dao.getScoreByUsername(((Player) ent).username) != null){
                                dao.updateScore(score);
                            } else {
                                dao.createScore(score);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        i.remove();
                    }
                    if (!bulletAddScoreQueue.isEmpty() && bulletAddScoreQueue.contains(((Player) ent).username)) {
                        ((Player) ent).score += 5;
                        bulletAddScoreQueue.remove(((Player) ent).username);
                    }
                }

                if (ent instanceof Bullet) {
                    Bullet bullet = (Bullet) ent;
                    if (bullet.x > 1000 || bullet.y >500 || bullet.x <= 0 || bullet.y <= 0) {
                        i.remove();
                        bulletActiveQueue.remove(bullet.shotBy.uid);
                    } else {
                        bullet.x = bullet.x + bullet.dirX * bullet.speed;
                        bullet.y = bullet.y + bullet.dirY * bullet.speed;
                        try {
                            for (Entity entity : entities) {
                                if (entity instanceof Player && !((Player) entity).uid.equals(bullet.shotBy.uid)) {
                                    if (bullet.getBounds().intersects(entity.getBounds().getBoundsInLocal())) {
                                        ((Player) entity).health -= bullet.damage;
                                        i.remove();
                                        bulletAddScoreQueue.add(bullet.shotBy.username);
                                        bulletActiveQueue.remove(bullet.shotBy.uid);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("concurrent mistake");
                        }
                    }
                }
            }

            while (!bulletAddQueue.isEmpty()) {
                Entity ent = bulletAddQueue.remove();
                entities.add(ent);
            }
        }
    }

}
