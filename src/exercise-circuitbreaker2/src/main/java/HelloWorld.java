import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.exception.HystrixRuntimeException;

public class HelloWorld extends HystrixCommand<String> {
                         // TODO: refactor, remove name and counter?
    private final String name;
    private final int counter;

    public HelloWorld(String name, int counter) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionTimeoutInMilliseconds(300)));
        this.name = name;
        this.counter = counter;
    }

    @Override
    protected String run() {
        Service service = new Service(counter);
        return service.doSomething();
    }

    public static void main(String args[]) {
        for (int i = 0; i < 10;i++) {
            try {
                String result = new HelloWorld("Test",i).execute();
                System.out.println("Result: " + result);
            } catch (HystrixRuntimeException e) {
                System.out.println("Iteration " + i + " timed out");
            }
        }
    }
}

class Service {
    public Service(int counter) { this.counter = counter;}
    private int counter=0;
    public String doSomething() {
        long waitTime = Math.round(Math.random()*500);
        System.out.println("Waiting " + waitTime + " ms for iteration " + counter);
        try {
            // Exercise: We're simulation a long running operation here by waiting some random amount of time.
            // If the wait-time is too long the call to this method is interrupted by Hystrix.
            // What would happen if, as in real life, Thread.sleep() would be a call to a remote service?
            // Would the request on the remote end be interrupted? Or would it complete? What are we going to tell our customer?
            Thread.sleep(waitTime);
            System.out.println("Wake-up " + counter);
        } catch (InterruptedException e) {
            System.out.println("Interrupted " + counter);
        }
        return counter + " waited " + waitTime + " ms";
    }
}
