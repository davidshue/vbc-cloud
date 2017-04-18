package z9.cloud;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpVersion;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHttpResponse;
import org.springframework.cloud.netflix.zuul.filters.route.ZuulFallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import z9.cloud.core2.Z9HttpResponse;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by david on 4/17/17.
 */
@Component
public class GatewayCircuitBreaker implements ZuulFallbackProvider {
    private Z9HttpResponse fallback;
    private byte[] fallbackPayload;

    @PostConstruct
    public void after() throws JsonProcessingException {
        BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1,
                org.apache.http.HttpStatus.SC_OK, "OK") ;
        BasicHttpEntity entity = new BasicHttpEntity();
        byte[] message = "service not available".getBytes(Charset.forName("UTF-8"));
        entity.setContent(new ByteArrayInputStream(message));
        entity.setContentLength(message.length);
        response.setEntity(entity);

        // force Content-Length header so the client doesn't expect us to close the connection to end the response
        response.addHeader("Content-Length", String.valueOf(message.length));

        fallback = Z9HttpResponse.toZ9HttpResponse(response);

        ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        String fallbackJson = mapper.writeValueAsString(fallback);

        fallbackPayload = fallbackJson.getBytes();
    }
    @Override
    public String getRoute() {
        return "kafka-node";
    }

    @Override
    public ClientHttpResponse fallbackResponse() {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.OK;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return 200;
            }

            @Override
            public String getStatusText() throws IOException {
                return "OK";
            }

            @Override
            public void close() {

            }

            @Override
            public InputStream getBody() throws IOException {
                return new ByteArrayInputStream(fallbackPayload);
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return headers;
            }
        };
    }
}
