package com.schwada.mpgame.logic.entity;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;

public abstract class Entity {

    public EntityType type;
    public String color;
    public float x;
    public float y;

    public abstract Rectangle getBounds();
    public abstract void setType(EntityType entityType);
    public abstract void draw(GraphicsContext graphics);
}
