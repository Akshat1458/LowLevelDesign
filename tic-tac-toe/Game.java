public class Game {
    
    private Board board;
    private Player playerX;
    private Player playerO;
    private Player currentPlayer;

    public Game(PlayerStrategy playerStrategy1, PlayerStrategy playerStrategy2){
        board = new Board(3, 3);
        playerX = new Player(Symbol.X, playerStrategy1);
        playerO = new Player(Symbol.O, playerStrategy2);
        currentPlayer = playerX;
    }

    public void play(){

        while(!board.isGameFinished()){

            board.display();

            Cell cell = currentPlayer.getMove(board);
            board.makeMove(cell);

            switchPlayer();
        }

        Symbol winner = board.getWinner();
        if(winner == Symbol.EMPTY){
            System.out.println("Game DRAW");
        }
        else{
            System.out.println("The winner is " + winner);
        }
    }
    private void switchPlayer(){
        
        currentPlayer = (currentPlayer == playerX) ? playerO : playerX;
    }

}
