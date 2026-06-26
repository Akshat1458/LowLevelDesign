import java.util.Scanner;
public class HumanStrategy implements PlayerStrategy{

    Scanner scanner;

    public HumanStrategy(Scanner scanner){
        this.scanner = scanner;
    }

    public Cell getMove(Board board){

        while(true){
            int row = scanner.nextInt();
            int col = scanner.nextInt();

            Cell cell = new Cell(row, col, Symbol.EMPTY);
            
            if(board.isValidMove(cell)){
                return cell;
            }
            System.out.println("Enter Valid input");
            scanner.nextLine();
        }

    }
    
}
