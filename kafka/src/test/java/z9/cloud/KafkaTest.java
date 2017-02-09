package z9.cloud;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * Created by david on 2/8/17.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes=AppConfig.class)
public class KafkaTest {
    @Autowired
    private Listener listener;

    @Autowired
    private KafkaTemplate<Integer, String> template;

    @Test
    public void testSimple() throws Exception {
        template.send("annotated1", 0, "foo-bar");
        template.flush();
        assertTrue(this.listener.getLatch1().await(10, TimeUnit.SECONDS));
    }
}
