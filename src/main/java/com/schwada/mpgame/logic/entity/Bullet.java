package com.schwada.mpgame.logic.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Bullet extends Entity {

    public float dirX;
    public float dirY;
    public float angle;
    public int size = 8;
    public int damage = 5;
    public int speed = 5;
    public Player shotBy;

    public Bullet(Player shotBy, float angle) {
        this.x = shotBy.x;
        this.y = shotBy.y;
        this.color = shotBy.color;
        this.angle = angle;
        this.shotBy = shotBy;
        this.setType(shotBy.type);
        dirX = (float) Math.cos(Math.toRadians(angle));
        dirY = (float) Math.sin(Math.toRadians(angle));
    }

    public Bullet(){}

    public void setType(EntityType type) {
        this.type = type;
        Object[] typeAttrs = type.getAttrs();
        this.speed = (Integer) typeAttrs[1];
        this.size = (Integer) typeAttrs[4];
        this.damage = (Integer) typeAttrs[5];
    }

    @Override
    public void draw(GraphicsContext graphics) {
        graphics.setFill(Color.web("#" + this.color));
        graphics.fillRect(x, y, size, size);

    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, size,size);
    }
}
