package com.schwada.mpgame.logic;

import com.schwada.mpgame.logic.entity.Bullet;
import com.schwada.mpgame.logic.entity.Entity;
import com.schwada.mpgame.logic.entity.EntityType;
import com.schwada.mpgame.logic.entity.Player;
import com.schwada.mpgame.net.PacketHelper;
import com.schwada.mpgame.net.PacketType;
import com.schwada.mpgame.persistence.dao.ScoreDAOImpl;
import com.schwada.mpgame.persistence.model.ScoreModel;

import java.util.*;
import java.util.logging.Logger;

public class DataHandler {
    private final static Logger logger = Logger.getLogger(DataHandler.class.getName());

    private GameState state;

    public DataHandler(GameState state) {
        this.state = state;
    }

    public String[] process(String[] packet, String origin) {
        PacketType type = PacketHelper.resolveType(packet);
        String[] data = PacketHelper.resolvePacketParameters(packet);

        // logger.info("DataHandler - about to process packet of type " + type);
        if (type == PacketType.LOGIN_REQUEST) return processLoginRequest(data, origin);
        if (type == PacketType.LOGIN_RESPONSE) return processLoginResponse(data);
        if (type == PacketType.POSITIONS_RESPONSE) return processPositionsResponse(data);
        if (type == PacketType.POSITIONS_REQUEST) return processPositionsRequest(data);
        if (type == PacketType.LOGOUT_REQUEST) return processLogoutRequest(data);
        if (type == PacketType.SCORES_REQUEST) return processScoresRequest(data);
        if (type == PacketType.SCORES_RESPONSE) return processScoresResponse(data);

        return new String[0];
    }

    private String[] processPositionsResponse(String[] data) {
        ArrayList<Entity> entities = new ArrayList<Entity>();
        for (String unprocessedEntity : data) {
            Entity constructed;
            if(unprocessedEntity.length() >= 45) {
                if (Integer.parseInt(unprocessedEntity.substring(29,30)) == 0) {
                    Player player = new Player();
                    player.username = unprocessedEntity.substring(5,17);
                    player.uid = unprocessedEntity.substring(1,5);
                    player.health = Integer.parseInt(unprocessedEntity.substring(38,41).trim());
                    player.score = Integer.parseInt(unprocessedEntity.substring(41,45).trim());
                    if(state.getUid() != null && !state.getUid().isEmpty() && state.getUid().equals(player.uid)) {
                        this.state.setClientPlayer(player);
                    }
                    constructed = player;
                } else {
                    constructed = new Bullet();
                }

                constructed.x = Float.parseFloat(unprocessedEntity.substring(17,23));
                constructed.y = Float.parseFloat(unprocessedEntity.substring(23,29));
                constructed.color = unprocessedEntity.substring(31,38).trim();
                constructed.setType(EntityType.numValueOf(Integer.parseInt(unprocessedEntity.substring(30,31))));
                entities.add(constructed);
            }
        }

        state.setEntities(entities);
        return new String[0];
    }

    private String[] processPositionsRequest(String[] data) {
        Vector<Entity> entities = state.getEntities();
        synchronized (entities) {
            String[] positions = new String[entities.size()];
            Iterator<Entity> i = entities.iterator();
            int index = 0;
            while (i.hasNext()) {
                Entity ent = i.next();
                if(data.length > 1 &&  (ent instanceof Player) && ((Player) ent).uid.equals(data[0])) {
                    Player controller = (Player) ent;
                    controller.up = data[1].equals("1");
                    controller.down = data[2].equals("1");
                    controller.right = data[3].equals("1");
                    controller.left = data[4].equals("1");
                    controller.attack = data[5].equals("1");
                    controller.mouseX = Float.parseFloat(data[6]);
                    controller.mouseY = Float.parseFloat(data[7]);
                }

                StringBuilder sendData = new StringBuilder("4");
                sendData.append(PacketHelper.padRight((ent instanceof Player) ? ((Player) ent).uid : "",4));
                sendData.append(PacketHelper.padRight((ent instanceof Player) ? ((Player) ent).username : "",12));
                sendData.append(PacketHelper.padRight(String.valueOf(ent.x),6));
                sendData.append(PacketHelper.padRight(String.valueOf(ent.y),6));
                sendData.append((ent instanceof Bullet) ? 1 : 0);
                sendData.append(ent.type.numVal);
                sendData.append(PacketHelper.padRight(ent.color,7));
                sendData.append(PacketHelper.padRight(String.valueOf((ent instanceof Player) ? ((Player) ent).health : ""),3));
                sendData.append(PacketHelper.padRight(String.valueOf((ent instanceof Player) ? ((Player) ent).score : ""), 4));
                positions[index] = sendData.toString();
                index++;
            }
            return positions;
        }
    }


    public String[] processLoginResponse(String[] data) {
        String uid = data[0];
        if (uid.length() <= 0){
            logger.info("DataHandler - Your login request was unsuccessful");
            state.setAuthenticated(false);
        } else {
            logger.info("DataHandler - Your login request was successful");
            state.setUid(uid);
            state.setAuthenticated(true);
        }
        return new String[0];
    }

    public String[] processLogoutRequest(String[] data) {
        Vector<Entity> entities = state.getEntities();
        synchronized (entities) {
            Iterator i = entities.iterator();
            while (i.hasNext()) {
                Entity ent = (Entity) i.next();
                if (ent instanceof Player && ((Player) ent).uid.equals(data[0])) {
                    logger.info("DataHandler - Your logout request was successful");
                    i.remove();
                }
            }
        }
        return new String[0];
    }

    public String[] processLoginRequest(String[] data, String origin) {
        String uid = state.generateUid();
        Vector<Entity> entities = state.getEntities();
        synchronized (entities) {
            Iterator<Entity> i = entities.iterator();
            while (i.hasNext()) {
                if (((Player) i.next()).username.equals(data[0])) {
                    return new String[0];
                }
            }
            Player newPlayer = new Player(origin, uid, data[0], EntityType.numValueOf(Integer.parseInt(data[2])), data[1].substring(2,8),100,100,100,0, 0);
            entities.add(newPlayer);
            logger.info("DataHandler - Your login request was successful");
        }

        return new String[]{ "1" + uid };
    }

    public String[] processScoresRequest(String[] data) {
        ScoreDAOImpl dao = new ScoreDAOImpl();
        List<ScoreModel> scores = dao.getAll();
        String[] scoresResp = new String[scores.size()];
        int index = 0;
        for (ScoreModel score : scores) {
            StringBuilder sendData = new StringBuilder("6");
            sendData.append(PacketHelper.padRight(score.getUsername(),12));
            sendData.append(PacketHelper.padRight(score.getScore(),4));
            scoresResp[index] = sendData.toString();
            index++;
        }
        return scoresResp;
    }

    public String[] processScoresResponse(String[] data) {
        List<ScoreModel> scores = new ArrayList<>();
        for (String rawScore : data) {
            if(rawScore.length() > 1) {
                ScoreModel score = new ScoreModel(
                        rawScore.substring(1,13),
                        Integer.parseInt(rawScore.substring(13,15).trim())
                );
                scores.add(score);
            }
        }
        this.state.setScores(scores);
        return new String[0];
    }
}
