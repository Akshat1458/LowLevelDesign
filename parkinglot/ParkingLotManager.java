package parkinglot;

import java.time.LocalDateTime;
import java.util.Scanner;

import parkinglot.enums.PaymentMode;
import parkinglot.enums.TypeOfVehicle;

public class ParkingLotManager {

    private ParkingLot parkingLot;

    public ParkingLotManager(int[][] floorConfig) {
        parkingLot = new ParkingLot(floorConfig);
    }

    public Ticket handleEntry(Vehicle vehicle) {
        TypeOfVehicle type = vehicle.getTypeOfVehicle();

        if (!parkingLot.checkAvailableSlot(type)) {
            System.out.println("No slots available");
            return null;
        }

        Slot parkedSlot = parkingLot.parkVehicle(vehicle);

        Ticket ticket = generateTicket(parkedSlot, vehicle);
        return ticket;
    }

    private Ticket generateTicket(Slot parkedslot, Vehicle vehicle) {
        Ticket ticket = new Ticket(vehicle, parkedslot);
        return ticket;
    }

    public void handleExit(Ticket ticket) {
        ticket.setExitTime(LocalDateTime.now());

        Slot slot = ticket.getSlot();
        slot.unparkVehicle();
        double amount = PricingCalculator.calculatePrice(ticket);

        System.out.println("Your total amount is: " + amount);
        System.out.println("Pay by \n 1.Cash \n 2.UPI");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        PaymentMode paymentMode;
        switch (choice) {
            case 1:
                paymentMode = PaymentMode.CASH;
                break;
            case 2:
                paymentMode = PaymentMode.UPI;
                break;
            default:
                System.out.println("Invalid choice, defaulting to Cash");
                paymentMode = PaymentMode.CASH;
        }

        PaymentStrategy paymentStrategy = PaymentModeFactory.getPaymentStrategy(paymentMode);
        Payment payment = paymentStrategy.collectPayment(amount);
        ticket.setPayment(payment);
        System.out.println("Good to go");
    

    }

}
