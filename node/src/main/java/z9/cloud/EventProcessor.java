package z9.cloud;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import z9.cloud.core2.HttpRetry;
import z9.cloud.core2.Z9HttpRequest;
import z9.cloud.core2.Z9HttpResponse;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

@Component
class EventProcessor {
    private final Log logger = LogFactory.getLog(getClass());

    private static final String LINE_RETURN = "\r\n";
    private static final String HTTP_ELEMENT_CHARSET = "US-ASCII";

    @Autowired
    private Environment environment;

    @Value("${http.waittime}")
    private long waitTime;

    @Value("${http.port}")
    private int serverPort;

    @Value("${http.server}")
    private String serverAddress;

    @Value("${http.protocol}")
    private String protocol;

    @Autowired
    @Qualifier("env")
    private String nodeId = "node1";

    @Autowired
    private HttpRetry httpRetry;

    private SocketAddress endpoint;

    private HttpHost httpHost;

    @PostConstruct
    public void afterInit() {
        endpoint = new InetSocketAddress(serverAddress, serverPort);
        httpHost = new HttpHost(serverAddress, serverPort, protocol);
    }


    public void processHttp(Z9HttpRequest input) throws IOException, HttpException {
        logger.info(input.getOrigin());
        if (StringUtils.equals(input.getOrigin(), nodeId)) {
            return;
        }
        executeHttp(input);
    }

    public Z9HttpResponse executeHttp(Z9HttpRequest input) throws IOException, HttpException {
        HttpRequest request = input.toBasicHttpRequest();
        HttpResponse response = exchange(request);



        return Z9HttpResponse.toZ9HttpResponse(response);

    }

    HttpResponse exchange(HttpRequest request) throws IOException {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;

        try {
            httpclient = HttpClients.custom().build();



            response = httpclient.execute(httpHost, request);

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                byte[] bytes = httpRetry.toByteArray(response.getEntity());

                HttpEntity byteArrayEntity = new ByteArrayEntity(bytes);
                response.setEntity(byteArrayEntity);
            }
            logger.info("Received response: {0} " + response);
            return response;
        } finally {
            IOUtils.closeQuietly(httpclient);
            IOUtils.closeQuietly(response);
        }

    }

    HttpResponse handle(HttpRequest request) throws IOException, HttpException {
        Socket socket = null;
        DefaultBHttpClientConnection activeConn = null;
        try {
            socket = new Socket();
            socket.connect(endpoint, 120000);
            activeConn = new DefaultBHttpClientConnection(8192);
            activeConn.bind(socket);
            activeConn.setSocketTimeout(1000);


            activeConn.sendRequestHeader(request);
            if (request instanceof HttpEntityEnclosingRequest) {
                activeConn.sendRequestEntity((HttpEntityEnclosingRequest)request);
            }
            activeConn.flush();

            HttpResponse response = httpRetry.receiveResponseHeader(activeConn);

            activeConn.receiveResponseEntity(response);

            byte[] bytes = httpRetry.toByteArray(response.getEntity());

            HttpEntity entity = new ByteArrayEntity(bytes);
            response.setEntity(entity);


            logger.info("Received response: {0} " + response);
            return response;

        } finally {
            IOUtils.closeQuietly(activeConn);
            IOUtils.closeQuietly(socket);
        }
    }


}
