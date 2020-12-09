package com.schwada.mpgame.persistence.dao;

import com.schwada.mpgame.persistence.model.ScoreModel;

import java.util.List;

public interface ScoreDAO {

    List<ScoreModel> getAll() throws Exception;
    boolean updateScore(ScoreModel s) throws Exception;
    boolean createScore(ScoreModel s) throws Exception;
    ScoreModel getScoreByUsername(String username) throws Exception;
}
