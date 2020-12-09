package com.schwada.mpgame.logic.entity;

import com.schwada.mpgame.net.PacketType;

import java.util.Arrays;

/**
 *
 *
 *  public int movementSpeed;
 *  public int bulletspeed;
 *  public int bulletCooldown;
 *  public int playerSize = 32;
 *  public int bulletSize = 8;
 *  public int bulletDamage = 5
 *  public int health;
 *  public Shape shape;
 *
 */
public enum EntityType {
    PLAIN(0, new Object[]{ 5,24, 60, 32, 8, 5, 100 }, "Default"),
    SPRINT(1, new Object[]{ 10, 16, 140, 24, 10, 4, 80 }, "Light"),
    TANK(2, new Object[]{ 2, 8, 160, 38, 7, 20, 80 }, "Heavy");

    public int numVal;
    private Object[] attrs;
    private String name;

    EntityType(int numval, Object[] attrs, String name) {
        this.numVal = numval;
        this.attrs = attrs;
        this.name = name;
    }

    public int getNumVal() {
        return numVal;
    }

    public Object[] getAttrs() {
        return attrs;
    }

    public String getStringValue() {
        return name;
    }

    public static EntityType nameValueOf(String name) {
        return Arrays.stream(values())
                .filter(packetType -> packetType.getStringValue().equals(name))
                .findFirst().orElse(null);
    }

    public static EntityType numValueOf(int value) {
        return Arrays.stream(values())
                .filter(packetType -> packetType.getNumVal() == value)
                .findFirst().orElse(null);
    }
}
