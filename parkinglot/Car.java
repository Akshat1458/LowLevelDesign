package parkinglot;

import parkinglot.enums.TypeOfVehicle;

public class Car extends Vehicle{

    public Car(String numberPlate){
        super(numberPlate, TypeOfVehicle.CAR);
    }
    
}
