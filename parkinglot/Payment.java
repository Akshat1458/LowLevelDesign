package parkinglot;


import parkinglot.enums.PaymentMode;

public class Payment {
    
    private double amount;
    private PaymentMode paymentMode;

    public Payment(double amount, PaymentMode paymentMode){
        this.amount = amount;
        this.paymentMode = paymentMode;
    }
}
