package com.xebia.shop.circuitbreaker2;

import com.netflix.config.DynamicLongProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesChainedArchaiusProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by marco on 19/09/15.
 */
public class IBANValidator {

    private static Logger LOG = LoggerFactory.getLogger(IBANValidator.class);

    private static DynamicLongProperty timeToWait = DynamicPropertyFactory.getInstance().getLongProperty("hystrixdemo.sleep", 100);


    public static synchronized boolean isValid()
            throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
        long t = timeToWait.get();
        LOG.info("waiting {} ms", t);
        if (t > 0) {
            Thread.sleep(t);
        }
        return true;
    }
}
