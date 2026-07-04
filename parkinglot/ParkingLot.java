package parkinglot;

import java.util.ArrayList;

import parkinglot.enums.TypeOfVehicle;


public class ParkingLot {

    private ArrayList<Floor> floors;

    public ParkingLot(int[][] floorConfig){

        int numberOfFloors = floorConfig.length;
        floors = new ArrayList<>();

        for(int i = 0; i < numberOfFloors; i++){
            Floor floor = new Floor(floorConfig[i][0], floorConfig[i][1]);
            floors.add(floor);
        }
    }

    public boolean checkAvailableSlot(TypeOfVehicle type){
        return findVacantSlot(type) != null;
    }

    public Slot findVacantSlot(TypeOfVehicle type){
        for(Floor floor: floors){
            Slot slot = floor.findVacantSlotOnFloor(type);
            if(slot != null){
                return slot;
            }
        }
        return null;
    }

    public Slot parkVehicle(Vehicle vehicle){
        Slot slot = findVacantSlot(vehicle.getTypeOfVehicle());
        slot.parkVehicle(vehicle);
        return slot;
        
    }


    
}
