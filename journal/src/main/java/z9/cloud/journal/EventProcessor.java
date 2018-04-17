package z9.cloud.journal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
class EventProcessor {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private UserEventRepository userEventRepository;

    @Value("${journal.blob.enabled:false}")
    private boolean enableBlob;

    @KafkaListener(id = "inputEventProcessor", topics = "http_input")
    public void processHttpInput(String message) {
        UserEvent event = UserEvent.constructInputUserEvent(message, enableBlob);
        logger.debug("inside journal for input" + event.getPk());

        userEventRepository.save(event);
    }

    @KafkaListener(id = "outputEventProcessor", topics = "http_output")
    public void processHttpOutput(String message) {
        UserEvent event = UserEvent.constructOutputUserEvent(message, enableBlob);
        logger.debug("inside journal for output " + event.getPk());

        userEventRepository.save(event);
    }
}
