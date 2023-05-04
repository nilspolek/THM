//package org.example;
// Den Code bespreche ich ausführlich im Podcast "Herzbergs Hörsaal" in der Episode
// https://anchor.fm/dominikusherzberg/episodes/PiS-Das-Nim-Spiel-in-Java-programmiert-edks2t
//

import java.util.*;
import java.util.stream.Stream;

import processing.core.PApplet;



class Move {
    final int row, number;

    static Move of(int row, int number) {
        return new Move(row, number);
    }

    private Move(int row, int number) {
        if (row < 0 || number < 1) throw new IllegalArgumentException();
        this.row = row;
        this.number = number;
    }

    public String toString() {
        return "(" + row + ", " + number + ")";
    }
}

interface NimGame {
    static boolean isWinning(int... numbers) {
        return Arrays.stream(numbers).reduce(0, (i, j) -> i ^ j) != 0;
        // klassische Variante:
        // int res = 0;
        // for(int number : numbers) res ^= number;
        // return res != 0;
    }

    NimGame play(Move... moves);

    Move randomMove();

    Move bestMove();

    boolean isGameOver();

    String toString();
}

class Nim extends PApplet implements NimGame {
    int backgroundColour = color(0, 0, 0);
    static Nim nim;

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (this == other) return true;
        if (getClass() != other.getClass()) return false;
        return ((Nim) other).hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        int[] temp = Arrays.stream(rows).filter(n -> n > 0).toArray();
        Arrays.sort(temp);
        return Arrays.hashCode(temp);
    }

    private Random r = new Random();
    int[] rows;
    Rectangle[] listOfRectangles;

    public static void main(String[] args) {
        String[] appArgs = {"Grab Ball"};
        nim = Nim.of(1, 2, 3);
        Nim mySketch = new Nim();
        PApplet.runSketch(appArgs, mySketch);
    }


    public void draw() {
        Arrays.stream(listOfRectangles).filter(Objects::nonNull).forEach(Rectangle::render);
    }

    public void setup() {
        background(backgroundColour);
        noStroke();
        int currentX = 30;
        int currentY = 30;
        listOfRectangles = new Rectangle[Arrays.stream(nim.rows).sum() + 1];
        int counter = 0;
        for (int i = 0; i < nim.rows.length; i++) {
            for (int j = 0; j < nim.rows[i]; j++) {
                listOfRectangles[counter] = new Rectangle((currentX + (Rectangle.sizeX * j)), (currentY + (Rectangle.sizeY * i)), this);
                counter++;
                currentX += 30;
            }
            currentX = 30;
            currentY += 30;
        }
        listOfRectangles[listOfRectangles.length - 1] = new Butten(width - 80, height - 80, this, listOfRectangles);
    }

    public void settings() {
        fullScreen();
    }

    public static Nim of(int... rows) {
        return new Nim(rows);
    }

    private Nim(int... rows) {
        assert rows.length >= 1;
        assert Arrays.stream(rows).allMatch(n -> n >= 0);
        this.rows = Arrays.copyOf(rows, rows.length);
    }

    private Nim play(Move m) {
        assert !isGameOver();
        assert m.row < rows.length && m.number <= rows[m.row];
        Nim nim = Nim.of(rows);
        nim.rows[m.row] -= m.number;
        return nim;
    }

    public Nim play(Move... moves) {
        Nim nim = this;
        for (Move m : moves) nim = nim.play(m);
        return nim;
    }

    public Move randomMove() {
        assert !isGameOver();
        int row;
        do {
            row = r.nextInt(rows.length);
        } while (rows[row] == 0);
        int number = r.nextInt(rows[row]) + 1;
        return Move.of(row, number);
    }

    public Move bestMove() {
        assert !isGameOver();
        if (!NimGame.isWinning(rows)) return randomMove();
        Move m;
        do {
            m = randomMove();
        } while (NimGame.isWinning(play(m).rows));
        return m;
    }

    public boolean isGameOver() {
        return Arrays.stream(rows).allMatch(n -> n == 0);
    }

    public String toString() {
        String s = "";
        for (int n : rows) s += "\n" + "I ".repeat(n);
        return s;
    }
}

class Butten extends Rectangle {
    int sizeX = 50;
    int sizeY = 50;

    Rectangle[] rectangles;

    Butten(int x, int y, Nim nim, Rectangle[] rectangles) {
        super(x, y, nim);
        this.rectangles = rectangles;
    }
    public Rectangle render() {
        this.isClicked();
        nim.fill(pinColour);
        nim.rect(x, y, sizeX, sizeY);
        return this;
    }
    void isClicked() {
        if (nim.mouseX > x && nim.mouseX < (x + sizeX) && nim.mouseY > y && nim.mouseY < (y + sizeY) && isClickable && nim.mousePressed) {
            Stream.of(rectangles).filter(e -> e.pinColour == color(200, 200, 200)).forEach(e -> e.pinColour = nim.backgroundColour);
        }
    }
}

class Rectangle extends PApplet {
    int x;
    int y;
    static int sizeX = 10;
    static int sizeY = 50;

    boolean isClickable = true;
    int pinColour = color(255, 255, 255);

    Nim nim;

    Rectangle(int x, int y, Nim nim) {
        this.x = x;
        this.y = y;
        this.nim = nim;
    }

    public Rectangle render() {
        this.isClicked();
        nim.fill(pinColour);
        nim.rect(x, y, sizeX, sizeY);
        return this;
    }

    void setIsClickable(){
            pinColour = nim.backgroundColour;
            isClickable = false;
    }

    void isClicked() {
        if (nim.mouseX > x && nim.mouseX < (x + sizeX) && nim.mouseY > y && nim.mouseY < (y + sizeY) && isClickable && nim.mousePressed) {
            pinColour = color(200, 200, 200);
        }
    }
}


