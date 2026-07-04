package tictactoe;

public class Board {

    private int rows;
    private int columns;
    private Cell[][] grid;

    public Board(int rows, int columns){
        this.rows = rows;
        this.columns = columns;
        grid = new Cell[rows][columns];

        for(int row = 0; row < rows; row++){
            for(int col = 0; col < columns; col++){
                grid[row][col] = new Cell(row, col, Symbol.EMPTY);
            }
        }

    }

    public boolean isValidMove(Cell cell){
        return cell.getRow() >= 0 && cell.getRow() < rows && cell.getColumn() >= 0 && cell.getColumn() < columns && grid[cell.getRow()][cell.getColumn()].getSymbol() == Symbol.EMPTY;
    }

    public boolean isGameFinished(){
        if(getWinner() != Symbol.EMPTY){
            return true;
        }
        // Check if all cells are filled (draw)
        for(int row = 0; row < rows; row++){
            for(int col = 0; col < columns; col++){
                if(grid[row][col].getSymbol() == Symbol.EMPTY){
                    return false;
                }
            }
        }
        return true;
    }

    public Symbol getWinner(){
        // Check rows
        for(int row = 0; row < rows; row++){
            if(grid[row][0].getSymbol() != Symbol.EMPTY &&
               grid[row][0].getSymbol() == grid[row][1].getSymbol() &&
               grid[row][1].getSymbol() == grid[row][2].getSymbol()){
                return grid[row][0].getSymbol();
            }
        }
        // Check columns
        for(int col = 0; col < columns; col++){
            if(grid[0][col].getSymbol() != Symbol.EMPTY &&
               grid[0][col].getSymbol() == grid[1][col].getSymbol() &&
               grid[1][col].getSymbol() == grid[2][col].getSymbol()){
                return grid[0][col].getSymbol();
            }
        }
        // Check diagonal (top-left to bottom-right)
        if(grid[0][0].getSymbol() != Symbol.EMPTY &&
           grid[0][0].getSymbol() == grid[1][1].getSymbol() &&
           grid[1][1].getSymbol() == grid[2][2].getSymbol()){
            return grid[0][0].getSymbol();
        }
        // Check diagonal (top-right to bottom-left)
        if(grid[0][2].getSymbol() != Symbol.EMPTY &&
           grid[0][2].getSymbol() == grid[1][1].getSymbol() &&
           grid[1][1].getSymbol() == grid[2][0].getSymbol()){
            return grid[0][2].getSymbol();
        }
        return Symbol.EMPTY;
    }

    public void makeMove(Cell cell){
        grid[cell.getRow()][cell.getColumn()] = cell;
    }

    public void display(){
        for(int row = 0; row < rows; row++){
            for(int col = 0; col < columns; col++){
                if(grid[row][col].getSymbol() == Symbol.EMPTY){
                    System.out.print(" - ");
                } else {
                    System.out.print(" " + grid[row][col].getSymbol() + " ");
                }
                if(col < columns - 1){
                    System.out.print("|");
                }
            }
            System.out.println();
            if(row < rows - 1){
                System.out.println("-----------");
            }
        }
        System.out.println();
    }
    
}
