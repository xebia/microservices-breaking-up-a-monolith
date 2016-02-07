package com.xebia.shopmanager.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvalidStatusException extends Exception {
    private static Logger LOG = LoggerFactory.getLogger(InvalidStatusException.class);

    public InvalidStatusException(String message) {
        LOG.error(message);
    }
}
