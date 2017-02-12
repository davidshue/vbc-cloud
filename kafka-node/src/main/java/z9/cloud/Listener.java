package z9.cloud;

import org.springframework.kafka.annotation.KafkaListener;

import java.util.concurrent.CountDownLatch;

/**
 * Created by david on 2/8/17.
 */
public class Listener {
    private final CountDownLatch latch1 = new CountDownLatch(1);

    public CountDownLatch getLatch1() {
        return latch1;
    }

    @KafkaListener(id = "foo", topics = "annotated1")
    public void listen1(String foo) {
        System.out.println(foo);
        this.latch1.countDown();
    }
}
