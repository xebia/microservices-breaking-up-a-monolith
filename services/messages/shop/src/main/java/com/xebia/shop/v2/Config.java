package com.xebia.shop.v2;

public class Config {

    private Config() {
    }

    // General name for the exchange to use
    public static final String SHOP_EXCHANGE = "shop";

    // Sent by Clerk when handling control to shop process
    public static final String START_SHOPPING = "START_SHOPPING";

    // Results received by Clerk from other services
    public static final String ORDER_COMPLETED = "ORDER_COMPLETED";

}
