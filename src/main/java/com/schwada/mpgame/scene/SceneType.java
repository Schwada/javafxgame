package com.schwada.mpgame.scene;

public enum SceneType {
    LOGIN(0),
    GAME(1),
    LOST(2),
    SERVER(3);

    private int numVal;

    SceneType(int numval) {
        this.numVal = numval;
    }

    public int getNumVal() {
        return numVal;
    }
}
