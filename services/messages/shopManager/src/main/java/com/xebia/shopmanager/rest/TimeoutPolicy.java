package com.xebia.shopmanager.rest;

import org.springframework.stereotype.Component;

@Component
public class TimeoutPolicy {
    private long timeout = 10000;

    public TimeoutPolicy() {}

    public TimeoutPolicy(long timeout) {
        this.timeout = timeout;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

}
