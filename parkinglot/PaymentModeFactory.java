package parkinglot;

import parkinglot.enums.PaymentMode;

public class PaymentModeFactory {

    public static PaymentStrategy getPaymentStrategy(PaymentMode paymentMode){
        
        switch(paymentMode){
            case PaymentMode.UPI:
                return new UPIPaymentStrategy();
            case PaymentMode.CASH:
                return new CashPaymentStrategy();
        }
        return null;
    }
    
}
