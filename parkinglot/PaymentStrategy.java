package parkinglot;

public interface PaymentStrategy {

    public Payment collectPayment(double amount);
    
}
