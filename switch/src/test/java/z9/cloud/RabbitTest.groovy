package z9.cloud

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=RabbitTestAppConfig.class)
@ActiveProfiles('rabbit')
class RabbitTest {
	@Autowired
	private AmqpTemplate template
	
	private Gson gson = new GsonBuilder()
		.setPrettyPrinting()
		.create()
	
	@Test
	void testAddSubscriber() {
		long time0 = System.currentTimeMillis()
		def payload = ['userId': 20006L, 'countryCode': 'es_US-exc', 'contentId': 905L, oid: 'switch']
		def json = gson.toJson(payload)
		println json

		template.convertAndSend("http_exchange", 'broadcast', json)

		long time1 = System.currentTimeMillis()
		println 'took ' + (time1-time0) + ' msecs'
	}
}
