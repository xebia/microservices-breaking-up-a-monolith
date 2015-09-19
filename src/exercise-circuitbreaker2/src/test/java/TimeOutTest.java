import com.netflix.hystrix.HystrixEventType;
import com.netflix.hystrix.HystrixInvokableInfo;
import com.netflix.hystrix.HystrixRequestLog;
import com.netflix.hystrix.examples.basic.CommandCollapserGetValueForKey;
import com.netflix.hystrix.examples.basic.CommandHelloWorld;
import com.netflix.hystrix.examples.demo.HystrixCommandDemo;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.*;

/**
 * Created by marco on 18/09/15.
 */
public class TimeOutTest {



    @Test
    public void testHystrixCommand() {

        HystrixCommandDemo hcd = new HystrixCommandDemo();
        hcd.startDemo();

    }



}
