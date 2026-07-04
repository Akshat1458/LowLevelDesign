package parkinglot;

import parkinglot.enums.PaymentMode;

public class UPIPaymentStrategy implements PaymentStrategy {

    public Payment collectPayment(double amount){
        System.out.println("payment done by UPI");
        
        Payment payment = new Payment(amount, PaymentMode.UPI);
        return payment;
    }
    
}
