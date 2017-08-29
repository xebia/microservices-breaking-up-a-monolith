package com.xebia.shopmanager;

public class Config {

    private Config() {
    }

    // General name for the exchange to use
    public final static String SHOP_EXCHANGE = "shop";

    // Sent by Clerk when handling control to shop process
    public final static String START_SHOPPING = "START_SHOPPING";

    // Sent by Clerk when Order is received to initiate payment
    public final static String HANDLE_PAYMENT = "HANDLE_PAYMENT";

    // Sent by Clerk when Payment is received to initiate fulfillment
    public final static String HANDLE_FULFILLMENT = "HANDLE_FULFILLMENT";

    // Results received by Clerk from other services
    public static final String ORDER_COMPLETED = "ORDER_COMPLETED";
    public static final String ORDER_PAID = "ORDER_PAID";
    public static final String ORDER_SHIPPED = "ORDER_SHIPPED";

    public static final String SESSION_EXPIRED = "SESSION_EXPIRED";
}
