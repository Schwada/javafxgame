package com.schwada.mpgame.logic.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Player extends Entity {

    // Identifiers
    public String username;
    public String uid;
    public String origin;
    // Points
    public int score;
    public int kills;
    // Input
    public boolean up = false;
    public boolean down = false;
    public boolean right = false;
    public boolean left = false;
    public float mouseX;
    public float mouseY;
    public float mouseDistance;
    public boolean attack = false;
    public boolean bulletShot = false;
    // Type of player attributes
    public int movementSpeed = 5;
    public int bulletCooldown = 60;
    public int playerSize = 32;
    public int maxHealth = 100;
    public int health;
    public Shape shape;

    public Player() {
    }

    public Player(String origin, String uid, String username, EntityType type, String color, float x, float y, int health, int score, int kills) {
        this.x = x;
        this.y = y;
        this.uid = uid;
        this.color = color;
        this.score = score;
        this.health = health;
        this.origin = origin;
        this.username = username;

        this.setType(type);
        this.health = maxHealth;
    }

    public void setType(EntityType type) {
        this.type = type;
        Object[] typeAttrs = type.getAttrs();
        this.movementSpeed = (Integer) typeAttrs[0];
        this.bulletCooldown = (Integer) typeAttrs[2];
        this.playerSize = (Integer) typeAttrs[3];
        this.maxHealth = (Integer) typeAttrs[6];
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, playerSize,playerSize);
    }

    @Override
    public void draw(GraphicsContext graphics) {
        graphics.setFill(Color.web("#" + this.color));

        graphics.fillOval(x, y, playerSize, playerSize);
        graphics.setFill(Color.SNOW);
        graphics.fillText(username, x, y);
        graphics.fillText(String.valueOf(health), x, y - 20);
    }

    @Override
    public String toString() {
        return "Player{" +
                "username='" + username + '\'' +
                ", uid='" + uid + '\'' +
                ", origin='" + origin + '\'' +
                ", score=" + score +
                ", kills=" + kills +
                ", up=" + up +
                ", down=" + down +
                ", right=" + right +
                ", left=" + left +
                ", mouseDistance=" + mouseDistance +
                ", attack=" + attack +
                ", bulletShot=" + bulletShot +
                ", movementSpeed=" + movementSpeed +
                ", bulletCooldown=" + bulletCooldown +
                ", playerSize=" + playerSize +
                ", health=" + health +
                ", shape=" + shape +
                ", type=" + type +
                ", color='" + color + '\'' +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
