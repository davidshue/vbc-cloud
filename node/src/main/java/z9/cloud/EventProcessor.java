package z9.cloud;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import z9.cloud.core2.Z9HttpRequest;
import z9.cloud.core2.Z9HttpResponse;

import java.io.IOException;
import java.util.List;

@Component
class EventProcessor {
    private final Log logger = LogFactory.getLog(getClass());

    public static final Long THIRTY_MIN = 30*60_000L;

    public static final Long ONE_HOUR = 60 * 60_000L;

    @Value("${http.waittime}")
    private long waitTime;


    //@Value("${eureka.instance.hostname}:${server.port}")
    @Autowired
    @Qualifier("env")
    private String nodeId = "node1";


    @Autowired
    private SessionHelper sessionHelper;

    @Autowired
    private EventProcessorCircuitBreaker eventProcessorCircuitBreaker;

    private ObjectMapper objectMapper = new ObjectMapper();



    @KafkaListener(id = "eventProcessor", topics = "http_input")
    public void processHttp(String message) throws IOException, HttpException {
        Z9HttpRequest input = objectMapper.readValue(message, Z9HttpRequest.class);

        if (StringUtils.equals(input.getOrigin(), nodeId)) {
            return;
        }

        if ((System.currentTimeMillis() - input.getTimestamp()) >= THIRTY_MIN) {
            String z9SessionId = input.getZ9SessionId();
            if (z9SessionId != null) {
                List<Revival> revivals = sessionHelper.revive(nodeId, z9SessionId);
                revivals.forEach((Revival revival) -> {
                            try {
                                logger.debug("REVIVING " + revival.getUrl());
                                Z9HttpResponse  res = executeHttp(revival.getRequest());
                                logger.debug("REVIVING " + revival.getUrl()
                                    + ", STATUS " + res.getStatusLine().getStatusCode()
                                    + " " + res.getStatusLine().getReasonPhrase());
                            } catch (IOException | HttpException e) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                );
            }
        }
        logger.debug("REPLAYING " + input.getRequestLine().getUri() + " FROM " + input.getOrigin());
        Z9HttpResponse  res = executeHttp(input);
        logger.debug("REPLAYING " + input.getRequestLine().getUri() + " FROM " + input.getOrigin()
            + ", STATUS " + res.getStatusLine().getStatusCode()
            + " " + res.getStatusLine().getReasonPhrase());
    }

    public Z9HttpResponse executeHttp(Z9HttpRequest input) throws IOException, HttpException {
        HttpRequest request = input.toBasicHttpRequest();
        HttpResponse response = eventProcessorCircuitBreaker.exchange(request);

        return Z9HttpResponse.toZ9HttpResponse(response);
    }
}
