package com.xebia.shop.circuitbreaker2;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

/**
 * Created by marco on 18/09/15.
 */
public class CacheRequestsCommand extends HystrixCommand<Boolean> {


        private final int value;

        public CacheRequestsCommand(int value) {
            super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
            this.value = value;
        }

        @Override
        protected Boolean run() {
            return value == 0 || value % 2 == 0;
        }

        @Override
        protected String getCacheKey() {
            return String.valueOf(value);
        }
}
