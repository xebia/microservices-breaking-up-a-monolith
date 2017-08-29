package com.xebia.payment.v2;

public class Config {

    private Config() {
    }

    // General name for the exchange to use
    public static final String SHOP_EXCHANGE = "shop";

    // Sent by Clerk when Order is received to initiate payment
    public static final String HANDLE_PAYMENT = "HANDLE_PAYMENT";


    // Results received by Clerk from other services
    public static final String ORDER_PAID = "ORDER_PAID";

}
