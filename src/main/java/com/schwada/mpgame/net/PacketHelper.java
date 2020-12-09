package com.schwada.mpgame.net;

import com.schwada.mpgame.logic.entity.Bullet;
import com.schwada.mpgame.logic.entity.Entity;
import com.schwada.mpgame.logic.entity.EntityType;
import com.schwada.mpgame.logic.entity.Player;

import java.util.Arrays;

public class PacketHelper {


    /**
     * Returns packet type based on first character which denotes type
     * @param packet packet containg type for resolution
     * @return type of packet
     */
    public static PacketType resolveType(String[] packet) {
        return PacketType.numValueOf(Integer.parseInt(packet[0].substring(0,1)));
    }

    public static String[] resolvePacketParameters(String[] data) {
        PacketType type = resolveType(data);

        if (type == PacketType.LOGIN_REQUEST) {
            return new String[] {
                data[0].substring(1,12).trim(),
                data[0].substring(12,23).trim(),
                data[0].substring(23,24).trim()
            };
        }

        if (type == PacketType.LOGOUT_REQUEST) {
            return new String[] {
                    data[0].substring(1,5).trim(),
            };
        }

        if (type == PacketType.LOGIN_RESPONSE) {
            return new String[] {
                data[0].substring(1,5).trim()
            };
        }

        if (type.equals(PacketType.POSITIONS_REQUEST) && data[0].length() > 1) {
            return new String[] {
                    data[0].substring(1,5),
                    data[0].substring(5,6),
                    data[0].substring(6,7),
                    data[0].substring(7,8),
                    data[0].substring(8,9),
                    data[0].substring(9,10),
                    data[0].substring(10,13).trim(),
                    data[0].substring(13,16).trim()
            };
        }

        if (type == PacketType.POSITIONS_RESPONSE) {
            return data;
        }

        if (type == PacketType.SCORES_RESPONSE) {
            return data;
        }

        return new String[0];
    }


    /**
     * Fixes up packet parameter to fit parameter descriptions
     * @param textToPad - packet parameter to modify
     * @param finalLength - length which packet parameter should have
     * @return
     */
    public static String padRight(Object textToPad, int finalLength) {
        String text = String.valueOf(textToPad);
        text = (text.length() > finalLength) ? text.substring(0, finalLength) : text;
        int length = (finalLength - text.length());
        if(length == 0) return text;
        return text + new String(new char[length]).replace('\0', ' ');
    }
}
