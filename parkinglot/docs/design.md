# Parking Lot Design

## User Flow

1. User enters the parking
2. Availability check, if full deny entry else proceed further
3. Parking checks the requirement of user if it is bike or car
4. Based on requirement parking generates ticket(which captures entry time, vehicle number assigned slot) and assign the floor and slot to the user
5. Vehicle is parked
6. After sometime user want to exit so shares the ticket
7. Parking tell the price to user based on ticket
8. User pays the amount
9. Takes exit and that slot is vacant now

## Core Entities

- Vehicle
- Parking Lot
- Floors
- Parking Slot
- Ticket
- Parking Lot Manager
- Payment

## Class Diagram

```mermaid
classDiagram
    %% Core Management
    class ParkingLotManager {
        -ParkingLot parkingLot
        +handleEntry(vehicle)
        +createTicket()
        +handleExit(ticket)
    }

    %% Physical Hierarchy
    class ParkingLot {
        -List~Floor~ floors
        +CheckSlotAvailability()
        +parkVehicle()
        +unparkVehicle()
        +FindVacantSlot(type)
    }

    class Floor {
        -List~Slot~ slots
        +FindVacantSlot(Type)
    }

    class Slot {
        -boolean occupied
        -String type
        -Vehicle vehicle
        +isOccupied()
        +getTypeOfSlot()
        +park(vehicle)
        +unpark(vehicle)
    }

    class CarSlot {
        -String Type = CAR
    }

    class BikeSlot {
        -String Type = Bike
    }

    %% Payment System
    class PaymentStrategy {
        <<interface>>
        +MakePayment()
    }

    class CashPayment {
        +MakePayment()
    }

    class UPIPayment {
        +MakePayment()
    }

    class Payment {
        -Double amount
        -String modeOfPayment
    }

    %% Vehicles
    class Vehicle {
        -String numberPlate
        -String Type
    }

    class Car {
        -String type = CAR
    }

    class Bike {
        -String Type = bike
    }

    %% Transactional
    class Ticket {
        -Vehicle vehicle
        -Slot slot
        -Time entryTime
        -Time exitTime
        -Payment payment
    }

    %% Relationships
    ParkingLotManager *-- ParkingLot : contains
    ParkingLot *-- Floor : contains
    Floor *-- Slot : contains
    
    Slot <|-- CarSlot : extends
    Slot <|-- BikeSlot : extends
    
    PaymentStrategy <|.. CashPayment : implements
    PaymentStrategy <|.. UPIPayment : implements
    
    Vehicle <|-- Car : extends
    Vehicle <|-- Bike : extends
    
    Ticket --> Vehicle
    Ticket --> Slot
    Ticket *-- Payment : contains
```