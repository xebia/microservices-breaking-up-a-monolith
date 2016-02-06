package com.xebia.payment.v2.domain;

import java.util.ArrayList;
import java.util.List;

// TODO: manager is currently just a placeholder

public class ShopManager {
    private final List<Clerk> clerks = new ArrayList<>();

    public Clerk dispatchClerk(WebUser webUser) {
        Clerk clerk = new Clerk(webUser);
        clerks.add(clerk);
        return clerk;
    }
}
