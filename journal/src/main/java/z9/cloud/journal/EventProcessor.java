package z9.cloud.journal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import z9.cloud.core2.Z9HttpRequest;

import java.io.IOException;

@Component
class EventProcessor {
    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private UserEventRepository userEventRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(id = "eventProcessor", topics = "http_topic")
    public void processHttp(String message) throws IOException, HttpException {
        Z9HttpRequest input = objectMapper.readValue(message, Z9HttpRequest.class);
        logger.debug("inside journal " + input.getOrigin());

        userEventRepository.save(new UserEvent(input));
    }
}
