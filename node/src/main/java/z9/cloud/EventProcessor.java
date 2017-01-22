package z9.cloud;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import z9.cloud.core2.HttpRetry;
import z9.cloud.core2.Z9HttpRequest;
import z9.cloud.core2.Z9HttpResponse;
import z9.cloud.core2.Z9HttpUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;

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
    private CookieSwapper cookieSwapper;

    @Autowired
    private HttpRetry httpRetry;


    private HttpHost httpHost;

    private CloseableHttpClient httpClient;

    @PostConstruct
    public void afterInit() {
        httpHost = new HttpHost(serverAddress, serverPort, protocol);
        httpClient = HttpClients.custom()
                .addInterceptorFirst((HttpRequestInterceptor) (request, context) -> {
                    request.removeHeaders(HTTP.CONTENT_LEN);
                    String zsessionId = Z9HttpUtils.getZ9SessionId(request);
                    if (StringUtils.isBlank(zsessionId)) {
                        String zid = Z9HttpUtils.getZid(request);
                        if (StringUtils.isBlank(zid)) {
                            return;
                        }

                        context.setAttribute("zid", zid);
                        context.setAttribute("setZid", Boolean.TRUE);
                    }
                    else {
                        context.setAttribute("zid", zsessionId);
                        context.setAttribute("setZid", Boolean.FALSE);
                        cookieSwapper.swap(request);
                    }
                })
                .addInterceptorLast((HttpResponse response, HttpContext context) -> {
                    // nothing to mediate if the status code is not 200
                    //if (response.getStatusLine().getStatusCode() != 200) {
                    //    return;
                    //}
                    if (response.getHeaders("Set-Cookie").length == 0) {
                        return;
                    }

                    String z9sessionId = (String) context.getAttribute("zid");
                    if (StringUtils.isBlank(z9sessionId)) {
                        return;
                    }

                    HttpClientContext clientContext = HttpClientContext.adapt(context);
                    cookieSwapper.mediate(z9sessionId, response, clientContext);

                    boolean setZid = (boolean) context.getAttribute("setZid");
                    if (setZid && !Z9HttpUtils.isZ9SessionIdSet(response, clientContext)) {
                        Z9HttpUtils.addZ9SessionIdToResponse(response, z9sessionId);
                    }


            /*
                    HttpClientContext clientContext = HttpClientContext.adapt(context);
                    CookieOrigin origin = clientContext.getCookieOrigin();
                    System.out.println(origin);

                    System.out.println("needZ9sessionid :" + context.getAttribute("needZ9sessionid"));
                    CookieSpec spec = clientContext.getCookieSpec();

                    for (Header header : response.getHeaders("Set-Cookie")) {
                        spec.parse(header, origin).forEach(cookie -> {
                            System.out.println("name: " + cookie.getName());
                            System.out.println("value: " + cookie.getValue());
                            System.out.println("domain: " + cookie.getDomain());
                            System.out.println("expiry: " + cookie.getExpiryDate());
                            System.out.println("path: " + cookie.getPath());
                            System.out.println("comment: " + cookie.getComment());
                            System.out.println("port: " + cookie.getPorts());
                            System.out.println("version: " + cookie.getVersion());
                        });
                    }
                    System.out.println(spec);
                    System.out.println("Right after HTTP returns");
                    */
                }).build();
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
        CloseableHttpResponse response = null;

        try {
            response = httpClient.execute(httpHost, request);

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                byte[] bytes = httpRetry.toByteArray(response.getEntity());

                HttpEntity byteArrayEntity = new ByteArrayEntity(bytes);
                response.setEntity(byteArrayEntity);
            }
            logger.info("Received response: {0} " + response);
            return response;
        } finally {
            IOUtils.closeQuietly(response);
        }

    }



}
