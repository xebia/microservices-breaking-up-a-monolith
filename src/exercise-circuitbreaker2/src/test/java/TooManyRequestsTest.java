import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixRequestLog;
import com.netflix.hystrix.HystrixThreadPoolMetrics;
import com.netflix.hystrix.examples.demo.*;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.xebia.shop.circuitbreaker2.GetUserAccCommand;
import org.junit.Before;
import org.junit.Test;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Created by marco on 18/09/15.
 */
public class TooManyRequestsTest {

    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(5, 5, 5, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(), new ThreadPoolExecutor.CallerRunsPolicy());

    @Before
    public void configureHystrix() {

        // exercise: play with core size settings
        ConfigurationManager.getConfigInstance().setProperty("hystrix.threadpool.default.coreSize", 6);
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.GetUserAccCommand.execution.isolation.thread.timeoutInMilliseconds", 50);
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.metrics.rollingPercentile.numBuckets", 10);
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.metrics.rollingStats.timeInMilliseconds", 10000);

    }

    @Test
    public void testLoadWithOneThread() {
            startMetricsMonitor();
            while (true) {
                runSimulatedRequestOnThread();
                // wait seconds on each loop
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    // ignore
                }

            }

        }


        public void runSimulatedRequestOnThread() {
            pool.execute(new Runnable() {

                @Override
                public void run() {
                    HystrixRequestContext context = HystrixRequestContext.initializeContext();
                    try {

                        UserAccount user = new GetUserAccCommand(new HttpCookie("mockKey", "mockValueFromHttpRequest")).execute();

                        System.out.println("Request => " + HystrixRequestLog.getCurrentRequest().getExecutedCommandsAsString());
                        Collection<HystrixThreadPoolMetrics> poolMetricsCollection = HystrixThreadPoolMetrics.getInstances();
                        for (HystrixThreadPoolMetrics metrics: poolMetricsCollection){
                           // System.out.println("Threads executed => " + metrics.getCumulativeCountThreadsExecuted());
                           // System.out.println("Active count => " + metrics.getCurrentActiveCount());

                        }
                        HystrixCommandMetrics userAccountMetrics = HystrixCommandMetrics.getInstance(HystrixCommandKey.Factory.asKey(GetUserAccCommand.class.getSimpleName()));
                        //System.out.println("Circuitbreaker status => " + userAccountMetrics.getProperties().circuitBreakerEnabled().get().toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        context.shutdown();
                    }
                }

            });
        }

        public void startMetricsMonitor() {
            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    while (true) {
                        /**
                         * Since this is a simple example and we know the exact HystrixCommandKeys we are interested in
                         * we will retrieve the HystrixCommandMetrics objects directly.
                         *
                         * Typically you would instead retrieve metrics from where they are published which is by default
                         * done using Servo: https://github.com/Netflix/Hystrix/wiki/Metrics-and-Monitoring
                         */

                        // wait 5 seconds on each loop
                        try {
                            Thread.sleep(5000);
                        } catch (Exception e) {
                            // ignore
                        }

                        // we are using default names so can use class.getSimpleName() to derive the keys
                        HystrixCommandMetrics userAccountMetrics = HystrixCommandMetrics.getInstance(HystrixCommandKey.Factory.asKey(GetUserAccCommand.class.getSimpleName()));

                        // print out metrics
                        StringBuilder out = new StringBuilder();
                        out.append("\n");
                        out.append("#####################################################################################").append("\n");
                        out.append("# GetUserAccCommand: " + getStatsStringFromMetrics(userAccountMetrics)).append("\n");
                        out.append("#####################################################################################").append("\n");
                        System.out.println(out.toString());
                    }
                }

                private String getStatsStringFromMetrics(HystrixCommandMetrics metrics) {
                    StringBuilder m = new StringBuilder();
                    if (metrics != null) {
                        HystrixCommandMetrics.HealthCounts health = metrics.getHealthCounts();
                        m.append("Requests: ").append(health.getTotalRequests()).append(" ");
                        m.append("Errors: ").append(health.getErrorCount()).append(" (").append(health.getErrorPercentage()).append("%)   ");
                        m.append("Mean: ").append(metrics.getExecutionTimePercentile(50)).append(" ");
                        m.append("75th: ").append(metrics.getExecutionTimePercentile(75)).append(" ");
                        m.append("90th: ").append(metrics.getExecutionTimePercentile(90)).append(" ");
                        m.append("99th: ").append(metrics.getExecutionTimePercentile(99)).append(" ");
                        m.append("Concurrent: ").append(metrics.getCurrentConcurrentExecutionCount()).append(" ");
                    }
                    return m.toString();
                }

            });
            t.setDaemon(true);
            t.start();
        }


}

