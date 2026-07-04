package parkinglot;

import parkinglot.enums.*;


public class Slot {
    
    private boolean occupied;
    private TypeOfVehicle type;
    private Vehicle vehicle;

    public Slot(TypeOfVehicle typeOfVehicle){
        this.type = typeOfVehicle;
        this.occupied = false;
    }

    public boolean isOccupied(){
        return occupied;
    }

    public TypeOfVehicle getType(){
        return type;
    }

    public void parkVehicle(Vehicle vehicle){
        occupied = true;
        this.vehicle = vehicle;
    }
    public void unparkVehicle(){
        occupied = false;
    }

    
}
