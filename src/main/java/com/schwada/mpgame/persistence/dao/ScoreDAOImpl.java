package com.schwada.mpgame.persistence.dao;

import com.schwada.mpgame.persistence.ConnectionFactory;
import com.schwada.mpgame.persistence.model.ScoreModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScoreDAOImpl implements ScoreDAO {


    @Override
    public List<ScoreModel> getAll() {
        Connection connection = ConnectionFactory.getConnection();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM scores limit 100");
            return extractUserFromResultSet(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateScore(ScoreModel s) throws Exception {
        Connection connection = ConnectionFactory.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE scores SET score=" + s.getScore() +" WHERE username=" + "\"" + s.getUsername() + "\"" );
            int i = ps.executeUpdate();
            if (i == 1) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }


    @Override
    public boolean createScore(ScoreModel s) throws Exception {
        Connection connection = ConnectionFactory.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO scores (username,score) VALUES (?, ?)");
            ps.setString(1, s.getUsername());
            ps.setInt(2, s.getScore());
            int i = ps.executeUpdate();
            if (i == 1) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }


    @Override
    public ScoreModel getScoreByUsername(String nickname) throws Exception {
        Connection connection = ConnectionFactory.getConnection();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM scores WHERE username=" + "\"" + nickname + "\"");
            List<ScoreModel> results = extractUserFromResultSet(rs);
            return (results.isEmpty()) ? null : results.get(0);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    private List<ScoreModel> extractUserFromResultSet(ResultSet rs) throws SQLException {
        List<ScoreModel> scores = new ArrayList<>();
        while (rs.next()) {
            ScoreModel score = new ScoreModel(rs.getInt("primary_key"), rs.getString("username"), rs.getInt("score"));
            scores.add(score);
        }
        return scores;
    }
}
