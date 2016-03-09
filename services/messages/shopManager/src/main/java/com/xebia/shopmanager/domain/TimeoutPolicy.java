package com.xebia.shopmanager.domain;

import org.springframework.stereotype.Component;

@Component
public class TimeoutPolicy {
    private long timeout = 1000000;

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
