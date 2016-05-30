package z9.cloud

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EventProcessor {
	private final Log logger = LogFactory.getLog(getClass())
	
	@Autowired
	private AmqpTemplate template

	
	void processHttp(msg) {
		logger.info msg
	}

}
