package tictactoe;

public class Player {

    private Symbol symbol;
    private PlayerStrategy playerStrategy;

    public Player(Symbol symbol, PlayerStrategy playerStrategy){

        this.playerStrategy = playerStrategy;
        this.symbol = symbol;
    }

    public Cell getMove(Board board){
        
        System.out.println("MOVE FOR " + symbol);
        Cell cell = playerStrategy.getMove(board);
        cell.setSymbol(symbol);
        return cell;
    }
    
}
