package tictactoe;

public class Cell {
    private int row;
    private int column;
    private Symbol symbol;

    public Cell(int row, int column, Symbol symbol){
        this.row = row;
        this.column = column;
        this.symbol = symbol;
    }

    public int getRow(){
        return row;
    }
    public int getColumn(){
        return column;
    }

    public void setSymbol(Symbol symbol){
        this.symbol = symbol;
    }

    public Symbol getSymbol(){
        return symbol;
    }
}
