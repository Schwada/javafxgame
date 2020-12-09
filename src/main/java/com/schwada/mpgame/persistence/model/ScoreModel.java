package com.schwada.mpgame.persistence.model;

import java.io.Serializable;

public class ScoreModel implements Serializable {
    public int ID;
    private String username;
    private int score;

    public ScoreModel(int ID, String username, int score) {
        this.ID = ID;
        this.username = username;
        this.score = score;
    }

    public ScoreModel(String username, int score) {
        this.username = username;
        this.score = score;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "ScoreModel{" +
                "ID=" + ID +
                ", username='" + username + '\'' +
                ", score=" + score +
                '}';
    }
}
