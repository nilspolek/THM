package org.example;

import processing.core.PApplet;

public class ProcessingFramework {
    PApplet main;

    ProcessingFramework(PApplet main) {
        this.main = main;
    }

}

class Component {
    PApplet main;

    int colour;
    int width;
    int height;
    int positionX;
    int positionY;
    boolean isClickable;

    Component(PApplet main, int width, int height, int positionX, int positionY, int colour) {
        this.main = main;
        this.width = width;
        this.height = height;
        this.positionX = positionX;
        this.positionY = positionY;
        this.colour = colour;
        isClickable = true;
    }

    public boolean isHoverd() {
        if (positionX < main.mouseX && main.mouseX > positionX + width && positionY < main.mouseY && main.mouseY > positionY + height && isClickable)
            return true;
        return false;
    }

    public Component setIsVisible(boolean visible) {
        isClickable = !visible;
        return this;
    }

    public void render() {
        main.fill(colour);
    }

    public boolean isClicked() {
        if (positionX < main.mouseX && main.mouseX > positionX + width && positionY < main.mouseY && main.mouseY > positionY + height && isClickable && main.mousePressed)
            return true;
        return false;
    }
}
