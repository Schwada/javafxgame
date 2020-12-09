package com.schwada.mpgame.logic;

import java.util.ArrayList;
import java.util.List;

public class Input {
    private List<String> keys = new ArrayList<>();
    private int mouseX;
    private int mouseY;
    private boolean mouseClicked;


    public boolean containsKey(String codeString) {
        return keys.stream().anyMatch((key) -> (key == null ? codeString == null : key.equals(codeString)));
    }


    public void setMousePress(int mouseX,int mouseY){
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        mouseClicked = true;
    }

    public void setMouseRelease(){
        mouseClicked = false;
    }

    public void putKey(String codeString, boolean b) {
        keys.add(codeString);
    }

    public void removeKey(String toString) {
        if(containsKey(toString)){
            for (int i = 0; i < keys.size(); i++) {
                if (keys.get(i) == null ? toString == null : keys.get(i).equals(toString)) { keys.remove(i); }
            }
        }
    }

    public boolean isKey(String keycode){
        if(containsKey(keycode)){return containsKey(keycode);}
        return containsKey(keycode);
    }

    public boolean isMouseClicked() {return mouseClicked;}
    public int getMouseX() {return mouseX;}
    public int getMouseY() {return mouseY;}
}



