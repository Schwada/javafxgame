package com.schwada.mpgame.net;

import java.util.Arrays;

/**
 * Denotes what type of packet is being
 * sent/received. It is always contained in
 * the first character of the data in the packet.
 * @author Schwada
 */
public enum PacketType {
    /**
     * 0        = Packet type
     * 1-8      = Character color
     * 9-20     = Username
     * 21-33    = Password
     */
    LOGIN_REQUEST(0),
    /**
     * 0        = Packet type
     * 1-5      = If successful this will contain uid
     */
    LOGIN_RESPONSE(1),
    /**
     * 0        = Packet type
     * 1-5      = Uid of user on server
     */
    LOGOUT_REQUEST(2),
    /**
     * 0        = Packet type
     * 1-5      = Uid of user on server
     * 6        = UP Active ( 0 - false, 1 - true )
     * 7        = DOWN Active ( 0 - false, 1 - true )
     * 8        = RIGHT Active ( 0 - false, 1 - true )
     * 9        = LEFT Active ( 0 - false, 1 - true )
     * 10       = ATTACK Active ( 0 - false, 1 - true )
     */
    POSITIONS_REQUEST(3),
    /**
     * 0        = Packet type
     * 1-7      = y coordinates
     * 8-14     = x coordinates
     * 15-18    = angle
     * 19       = character type
     * 20-27    = color
     * 28-32    = uid
     */
    POSITIONS_RESPONSE(4),
    SCORES_REQUEST(5),
    SCORES_RESPONSE(6);

    private int numVal;

    PacketType(int numval) {
        this.numVal = numval;
    }

    public int getNumVal() {
        return numVal;
    }

    public static PacketType numValueOf(int value) {
        return Arrays.stream(values())
                .filter(packetType -> packetType.getNumVal() == value)
                .findFirst().orElse(null);
    }

}

