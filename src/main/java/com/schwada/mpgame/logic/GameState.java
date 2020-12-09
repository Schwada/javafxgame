package com.schwada.mpgame.logic;

import com.schwada.mpgame.logic.entity.Entity;
import com.schwada.mpgame.logic.entity.Player;
import com.schwada.mpgame.persistence.model.ScoreModel;

import java.util.*;
import java.util.logging.Logger;

public class GameState {
    private final static Logger logger = Logger.getLogger(GameState.class.getName());

    private String clientUid;
    private Player clientPlayer;
    private Boolean authenticated = false;
    private String[] respawnPacket = null;

    private final Vector<Entity> entities = new Vector<>();
    private final Vector<ScoreModel> scores = new Vector<>();


    // TODO REFACTOR RETURN STRING
    public String generateUid() {
        Random r = new Random();
        int uid = r.nextInt((9999 - 1000) + 1) + 1000;
        synchronized (entities) {
            for (Entity entity : entities) {
                if (Integer.parseInt(((Player) entity).uid) == uid) return generateUid();
            }
        }
        return String.valueOf(uid);
    }


    public Boolean getAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(Boolean authenticated) {
        logger.info("State - Your authentication state has been changed to " + (authenticated == null ? "loading" : authenticated));
        this.authenticated = authenticated;
    }

    public String[] getRespawnPacket() {
        return respawnPacket;
    }

    public void setRespawnPacket(String[] respawnPacket) {
        logger.info("State - a respawn request has been set");
        this.respawnPacket = respawnPacket;
    }

    public void setUid(String uid) {
        logger.info("State - your client uid was set to " + uid);
        this.clientUid = uid;
    }

    public String getUid() {
        return clientUid;
    }

    public Vector<Entity> getEntities() {
        return entities;
    }

    public Vector<ScoreModel> getScores() {
        return scores;
    }

    public void setScores(List<ScoreModel> scores) {
        this.scores.clear();
        if(scores != null) {
            scores.sort(Comparator.comparingInt(ScoreModel::getScore));
            Collections.reverse(scores);
            this.scores.addAll(scores);
        }
    }

    public void setEntities(List<Entity> entities) {
        this.entities.clear();
        if(entities != null) {
            this.entities.addAll(entities);
        }
    }

    public Player getClientPlayer() {
        return clientPlayer;
    }

    public void setClientPlayer(Player clientPlayer) {
        this.clientPlayer = clientPlayer;
    }
}
