package parkinglot;

import java.util.ArrayList;

import parkinglot.enums.TypeOfVehicle;

public class Floor {

    private ArrayList<Slot> slots;

    public Floor(int numberOfCarSlot, int numberOfBikeSlot){
        slots = new ArrayList<>();

        for(int i = 0; i < numberOfCarSlot; i++){
            Slot slot = new CarSlot();
            slots.add(slot);
        }

        for(int i = 0; i < numberOfBikeSlot; i++){
            Slot slot = new BikeSlot();
            slots.add(slot);
        }

    }

    public Slot findVacantSlotOnFloor(TypeOfVehicle type){
        for(Slot slot: slots){
            boolean occupied = slot.isOccupied();
            if(!occupied && slot.getType() == type){
                return slot;
            }
        }
        return null;
    }

    
}
