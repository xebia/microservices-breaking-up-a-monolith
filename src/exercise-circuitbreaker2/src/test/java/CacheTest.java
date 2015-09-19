import com.netflix.hystrix.examples.basic.CommandUsingRequestCache;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.xebia.shop.circuitbreaker2.CacheRequestsCommand;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by marco on 18/09/15.
 */
public class CacheTest {



    @Test
    public void testWithoutCacheHits() {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            assertTrue(new CacheRequestsCommand(2).execute());
            assertFalse(new CacheRequestsCommand(1).execute());
            assertTrue(new CacheRequestsCommand(0).execute());
            assertTrue(new CacheRequestsCommand(58672).execute());
        } finally {
            context.shutdown();
        }
    }

    @Test
    public void testWithCacheHits() {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            CacheRequestsCommand command2a = new CacheRequestsCommand(2);
            CacheRequestsCommand command2b = new CacheRequestsCommand(2);

            assertTrue(command2a.execute());
            // this is the first time we've executed this command with the value of "2" so it should not be from cache
            assertFalse(command2a.isResponseFromCache());

            assertTrue(command2b.execute());
            // this is the second time we've executed this command with the same value so it should return from cache
            assertTrue(command2b.isResponseFromCache());
        } finally {
            context.shutdown();
        }

        // start a new request context
        context = HystrixRequestContext.initializeContext();
        try {
            CacheRequestsCommand command3b = new CacheRequestsCommand(2);
            assertTrue(command3b.execute());
            // this is a new request context so this should not come from cache
            assertFalse(command3b.isResponseFromCache());
        } finally {
            context.shutdown();
        }
    }
}

