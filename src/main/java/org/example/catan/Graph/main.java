package org.example.catan.Graph;
import org.example.catan.CatanBoard;

public class main {
    public static void main(String[] args) {
        CatanBoard board = new CatanBoard(3);
        board.test( 0, -2);
        board.test( 1, -2);
        board.test( 2, -2);
        board.test(-1, -1);
        board.test( 0, -1);
        board.test( 1, -1);
        board.test( 2, -1);
        board.test(-2,  0);
        board.test(-1,  0);
        board.test( 0,  0);
        board.test( 1,  0);
        board.test( 2,  0);
        board.test(-2,  1);
        board.test(-1,  1);
        board.test( 0,  1);
        board.test( 1,  1);
        board.test(-2,  2);
        board.test(-1,  2);
        board.test( 0,  2);

    }
}

