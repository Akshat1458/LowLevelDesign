package parkinglot;

import java.time.Duration;
import parkinglot.enums.TypeOfVehicle;

public class PricingCalculator {

    private static final double CAR_RATE_PER_SECOND = 0.05;
    private static final double BIKE_RATE_PER_SECOND = 0.02;

    public static double calculatePrice(Ticket ticket) {
        long seconds = Duration.between(ticket.getEntryTime(), ticket.getExitTime()).toSeconds();

        double ratePerSecond = getRate(ticket.getSlot().getType());

        return seconds * ratePerSecond;
    }

    private static double getRate(TypeOfVehicle type) {
        switch (type) {
            case CAR:
                return CAR_RATE_PER_SECOND;
            case BIKE:
                return BIKE_RATE_PER_SECOND;
            default:
                throw new IllegalArgumentException("Unknown vehicle type: " + type);
        }
    }
}
