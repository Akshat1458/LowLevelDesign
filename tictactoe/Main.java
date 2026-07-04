package tictactoe;

import java.util.Scanner;
public class Main{

    public static void main(String[] args){

        Scanner scanner = new Scanner(System.in);

        PlayerStrategy playerStrategy = new HumanStrategy(scanner);
        Game game = new Game(playerStrategy, playerStrategy);

        System.out.println("Game starts");
        game.play();
        System.out.println("Game ends");
    }
}