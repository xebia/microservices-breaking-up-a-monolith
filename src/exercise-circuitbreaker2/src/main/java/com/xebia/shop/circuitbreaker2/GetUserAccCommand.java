package com.xebia.shop.circuitbreaker2;

/**
 * Created by marco on 19/09/15.
 */


    import java.net.HttpCookie;

    import com.netflix.hystrix.HystrixCommand;
    import com.netflix.hystrix.HystrixCommandGroupKey;
    import com.netflix.hystrix.HystrixCommandProperties;
    import com.netflix.hystrix.examples.demo.UserAccount;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

/**
     * Sample HystrixCommand simulating one that would fetch UserAccount objects from a remote service or database.
     * <p>
     * This uses request caching and fallback behavior.
     */
    public class GetUserAccCommand extends HystrixCommand<UserAccount> {

        private final HttpCookie httpCookie;
        private final UserCookie userCookie;
       // private static Logger LOG = LoggerFactory.getLogger(GetUserAccCommand.class);

    /**
         *
         * @param cookie
         * @throws IllegalArgumentException
         *             if cookie is invalid meaning the user is not authenticated
         */
        public GetUserAccCommand(HttpCookie cookie) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("User"))
                            //        .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(5000))
                      //      .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withCircuitBreakerRequestVolumeThreshold(5))
                    //.andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withCircuitBreakerForceOpen(true))
                    // .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withCircuitBreakerForceClosed(true))
                    .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withCircuitBreakerErrorThresholdPercentage(5))
                    //.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withMetricsRollingStatisticalWindowInMilliseconds(30000))
                    //.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(1))

            );
            this.httpCookie = cookie;
        /* parse or throw an IllegalArgumentException */
            this.userCookie = UserCookie.parseCookie(httpCookie);
        }

        @Override
        protected UserAccount run() {
        /* simulate performing network call to retrieve user information */
            try {
                Thread.sleep((int) (Math.random() * 10) + 2);
            } catch (InterruptedException e) {
                // do nothing
            }

        /* fail 5% of the time to show how fallback works */
            if (Math.random() > 0.50) {
                throw new RuntimeException("random failure processing UserAccount network response");
            }

        /* latency spike 5% of the time so timeouts can be triggered occasionally */
        //    if (Math.random() > 0.95) {
                // random latency spike
        //        try {
        //            Thread.sleep((int) (Math.random() * 300) + 25);
        //        } catch (InterruptedException e) {
                    // do nothing
        //        }
        //    }

        /* success ... create UserAccount with data "from" the remote service response */
            return new UserAccount(86975, "John James", 2, true, false, true);
        }

        /**
         * Use the HttpCookie value as the cacheKey so multiple executions
         * in the same HystrixRequestContext will respond from cache.
         */
        @Override
        protected String getCacheKey() {
            return httpCookie.getValue();
        }

        /**
         * Fallback that will use data from the UserCookie and stubbed defaults
         * to create a UserAccount if the network call failed.
         */
        @Override
        protected UserAccount getFallback() {
        /*
         * first 3 come from the HttpCookie
         * next 3 are stubbed defaults
         */
            return new UserAccount(userCookie.userId, userCookie.name, userCookie.accountType, true, true, true);
        }

        /**
         * Represents values containing in the cookie.
         * <p>
         * A real version of this could handle decrypting a secure HTTPS cookie.
         */
        private static class UserCookie {
            /**
             * Parse an HttpCookie into a UserCookie or IllegalArgumentException if invalid cookie
             *
             * @param cookie
             * @return UserCookie
             * @throws IllegalArgumentException
             *             if cookie is invalid
             */
            private static UserCookie parseCookie(HttpCookie cookie) {
            /* real code would parse the cookie here */
             //   if (Math.random() < 0.998) {
                /* valid cookie */
                    return new UserCookie(12345, "Henry Peter", 1);
             //   } else {
                /* invalid cookie */
             //       throw new IllegalArgumentException();
             //   }
            }

            public UserCookie(int userId, String name, int accountType) {
                this.userId = userId;
                this.name = name;
                this.accountType = accountType;
            }

            private final int userId;
            private final String name;
            private final int accountType;
        }
    }


