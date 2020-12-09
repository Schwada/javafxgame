package com.schwada.mpgame.persistence.dao;

public class ScoreDAOFactory {

    public static ScoreDAOImpl create() {
        return new ScoreDAOImpl();
    }
}
