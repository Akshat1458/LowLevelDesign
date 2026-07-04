package parkinglot;

import parkinglot.enums.PaymentMode;

public class CashPaymentStrategy implements PaymentStrategy {

    public Payment collectPayment(double amount){
        System.out.println("payment done by cash");
        
        Payment payment = new Payment(amount, PaymentMode.CASH);
        return payment;
    }
    
}
