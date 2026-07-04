package parkinglot;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        int[][] floorConfig = {{2, 3}, {1, 2}};
        ParkingLotManager parkingLotManager = new ParkingLotManager(floorConfig);

        Vehicle vehicle = new Bike("DL 2086");

        Ticket ticket = parkingLotManager.handleEntry(vehicle);
        Thread.sleep(5000);
        parkingLotManager.handleExit(ticket);

    }
    
}
