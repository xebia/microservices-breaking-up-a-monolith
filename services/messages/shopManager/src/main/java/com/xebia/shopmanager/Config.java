package com.xebia.shopmanager;

public class Config {
    // General name for the exchange to use
    public final static String shopExchange = "shop";

    // Sent by Clerk when handling control to shop process
    public final static String startShopping = "startShopping";

    // Sent by Clerk when Order is received to initiate payment
    public final static String handlePayment = "handlePayment";

    // Sent by Clerk when Payment is received to initiate fulfillment
    public final static String handleFulfillment = "handleFulfillment";

    // Results received by Clerk from other services
    public static final String orderCompleted = "orderCompleted";
    public static final String orderPaid = "orderPaid";
    public static final String orderShipped = "orderShipped";

    public static final String sessionExpired = "sessionExpired";
}
