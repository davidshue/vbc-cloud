package z9.cloud;

import org.springframework.kafka.annotation.KafkaListener;

import java.util.concurrent.CountDownLatch;

/**
 * Created by david on 2/8/17.
 */
public class Listener {
    private final CountDownLatch latch = new CountDownLatch(1);

    public CountDownLatch getLatch() {
        return latch;
    }

    @KafkaListener(id = "foo", topics = "node-test")
    public void listen1(String foo) {
        System.out.println(foo);
        this.latch.countDown();
    }
}
