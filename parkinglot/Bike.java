package parkinglot;

import parkinglot.enums.TypeOfVehicle;

public class Bike extends Vehicle {

    public Bike(String numberPlate){
        super(numberPlate, TypeOfVehicle.BIKE);
    }
    
}
