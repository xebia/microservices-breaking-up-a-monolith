import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.xebia.shop.circuitbreaker2.CacheRequestsCommand;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by marco on 18/09/15.
 */
public class TooManyRequestsTest {



    @Test
    public void testWithoutCacheHits() {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
           // generate many requests over a time period, to trip cb
           // then decrease load, cb should close again

        } finally {
            context.shutdown();
        }
    }


}

