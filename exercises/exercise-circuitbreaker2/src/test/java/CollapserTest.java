import com.netflix.hystrix.HystrixEventType;
import com.netflix.hystrix.HystrixInvokableInfo;
import com.netflix.hystrix.HystrixRequestLog;
import com.netflix.hystrix.examples.basic.CommandCollapserGetValueForKey;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Future;

import static org.junit.Assert.*;

/**
 * Created by marco on 18/09/15.
 */
public class CollapserTest {



    @Test
    public void testCollapser() throws Exception {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            Future<String> f1 = new CommandCollapserGetValueForKey(1).queue();
            Future<String> f2 = new CommandCollapserGetValueForKey(2).queue();
            Future<String> f3 = new CommandCollapserGetValueForKey(3).queue();
            Future<String> f4 = new CommandCollapserGetValueForKey(4).queue();
            Future<String> f5 = new CommandCollapserGetValueForKey(5).queue();

            assertEquals("ValueForKey: 1", f1.get());
            assertEquals("ValueForKey: 2", f2.get());
            assertEquals("ValueForKey: 3", f3.get());
            assertEquals("ValueForKey: 4", f4.get());
            assertEquals("ValueForKey: 5", f5.get());


            int numExecuted = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size();

            System.err.println("num executed: " + numExecuted);

            // assert that the batch command 'GetValueForKey' was in fact executed and that it executed only
            // once or twice (due to non-determinism of scheduler since this example uses the real timer)
            if (numExecuted > 2) {
                fail("some of the commands should have been collapsed");
            }

            System.err.println("HystrixRequestLog.getCurrentRequest().getAllExecutedCommands(): " + HystrixRequestLog.getCurrentRequest().getAllExecutedCommands());

            int numLogs = 0;
            for (HystrixInvokableInfo<?> command : HystrixRequestLog.getCurrentRequest().getAllExecutedCommands()) {
                numLogs++;

                // assert the command is the one we're expecting
                assertEquals("GetValueForKey", command.getCommandKey().name());

                System.err.println(command.getCommandKey().name() + " => command.getExecutionEvents(): " + command.getExecutionEvents());

                // confirm that it was a COLLAPSED command execution
                assertTrue(command.getExecutionEvents().contains(HystrixEventType.COLLAPSED));
                assertTrue(command.getExecutionEvents().contains(HystrixEventType.SUCCESS));
            }

            assertEquals(numExecuted, numLogs);
        } finally {
            context.shutdown();
        }
    }


}
