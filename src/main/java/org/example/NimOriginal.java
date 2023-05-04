package org.example;
// Den Code bespreche ich ausführlich im Podcast "Herzbergs Hörsaal" in der Episode
// https://anchor.fm/dominikusherzberg/episodes/PiS-Das-Nim-Spiel-in-Java-programmiert-edks2t
//

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;


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

class Nim implements NimGame {
    private Random r = new Random();
    int[] rows;

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
    private static int evaluate(int[] rows) {
        return NimGame.isWinning(rows) ? 1 : -1;
    }

    private static int minimax(Nim game, int depth, boolean isMaximizingPlayer) {
        if (game.isGameOver() || depth == 0) {
            return evaluate(game.rows);
        }

        if (isMaximizingPlayer) {
            int bestScore = Integer.MIN_VALUE;
            for (Move move : game.getPossibleMoves()) {
                Nim newGame = game.play(move);
                int score = minimax(newGame, depth - 1, false);
                bestScore = Math.max(bestScore, score);
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (Move move : game.getPossibleMoves()) {
                Nim newGame = game.play(move);
                int score = minimax(newGame, depth - 1, true);
                bestScore = Math.min(bestScore, score);
            }
            return bestScore;
        }
    }

    public Move bestMove() {
        assert !isGameOver();
        List<Move> possibleMoves = getPossibleMoves();
        System.out.println(possibleMoves);
        if (possibleMoves.isEmpty()) return randomMove();
        Move bestMove = possibleMoves.get(0);
        int bestScore = Integer.MIN_VALUE;
        for (Move move : possibleMoves) {
            Nim newGame = play(move);
            int score = minimax(newGame, 5, false);
            if (score > bestScore) {
                bestMove = move;
                bestScore = score;
            }
        }
        return bestMove;
    }

    private List<Move> getPossibleMoves() {
        List<Move> possibleMoves = new ArrayList<>();
        for (int i = 0; i < rows.length; i++) {
            for (int j = 1; j <= rows[i]; j++) {
                int[] newRows = Arrays.copyOf(rows, rows.length);
                newRows[i] -= j;
                if (NimGame.isWinning(newRows)) continue;
                possibleMoves.add(Move.of(i, j));
            }
        }
        return possibleMoves;
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