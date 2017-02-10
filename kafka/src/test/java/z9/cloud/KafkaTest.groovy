package z9.cloud

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import z9.cloud.core2.Z9Header
import z9.cloud.core2.Z9HttpRequest
import z9.cloud.core2.Z9ProtocolVersion
import z9.cloud.core2.Z9RequestLine

import java.util.concurrent.TimeUnit

import static org.junit.Assert.assertTrue

/**
 * Created by david on 2/8/17.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AppConfig.class)
class KafkaTest {
    @Autowired
    private Listener listener

    @Autowired
    private KafkaTemplate<Integer, String> template

    @Test
    void testSimple() throws Exception {
        Z9HttpRequest request = new Z9HttpRequest(
                headers: [new Z9Header(name: 'header1', value: 'value1'), new Z9Header(name: 'header2', value: 'value2')],
                requestLine: new Z9RequestLine(method: 'post', uri: 'http://www.cnn.com', protocolVersion: new Z9ProtocolVersion(protocol: 'https', major: 1, minor: 1)),
                content: 'this is a test'.bytes
        )
        template.send("annotated1", 0, request)
        template.flush()
        assertTrue(this.listener.getLatch1().await(10, TimeUnit.SECONDS))
    }
}
