package z9.cloud.journal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
class EventProcessor {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private UserEventRepository userEventRepository;

    @KafkaListener(id = "eventProcessor", topics = "http_input")
    public void processHttp(String message) {
        UserEvent event = new UserEvent(message);
        logger.debug("inside journal " + event.getPk());

        userEventRepository.save(event);
    }
}
