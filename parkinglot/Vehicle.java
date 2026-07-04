package parkinglot;

import parkinglot.enums.TypeOfVehicle;

public class Vehicle {
    
    private String numberPlate;
    private TypeOfVehicle type;

    public Vehicle(String numberPlate, TypeOfVehicle type){
        this.numberPlate = numberPlate;
        this.type = type;
    }

    public TypeOfVehicle getTypeOfVehicle(){
        return type;
    }

}
