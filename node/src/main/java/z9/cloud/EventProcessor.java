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
import org.springframework.stereotype.Component;
import z9.cloud.core2.HttpRetry;
import z9.cloud.core2.Z9HttpRequest;
import z9.cloud.core2.Z9HttpResponse;
import z9.cloud.core2.Z9HttpUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Component
class EventProcessor {
    private final Log logger = LogFactory.getLog(getClass());

    public static final Long THIRTY_MIN = 30*60_000L;

    public static final Long ONE_HOUR = 60 * 60_000L;

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

    @Autowired
    private SessionHelper sessionHelper;


    private HttpHost httpHost;

    private CloseableHttpClient httpClient;

    @PostConstruct
    public void afterInit() {
        httpHost = new HttpHost(serverAddress, serverPort, protocol);
        httpClient = HttpClients.custom()
                .addInterceptorFirst((HttpRequestInterceptor) (request, context) -> {
                    request.removeHeaders(HTTP.CONTENT_LEN);
                    String zsessionId = Z9HttpUtils.getZ9SessionId(request);

                    context.setAttribute("setZid", Boolean.FALSE);
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
                        cookieSwapper.swap(request, zsessionId);
                    }
                })
                .addInterceptorFirst((HttpResponse response, HttpContext context) -> {
                    // nothing to mediate if the status code is not 200
                    //if (response.getStatusLine().getStatusCode() != 200) {
                    //    return;
                    //}
                    HttpClientContext clientContext = HttpClientContext.adapt(context);
                    logger.info("firsthand handling response on " + clientContext.getRequest().getRequestLine());

                    String z9sessionId = (String) context.getAttribute("zid");
                    if (StringUtils.isBlank(z9sessionId)) {
                        logger.info("no zid for " + clientContext.getRequest().getRequestLine());
                        return;
                    }

                    if (response.getHeaders("Set-Cookie").length == 0) {
                        logger.info("no set-cookie for " + clientContext.getRequest().getRequestLine());
                        return;
                    }

                    cookieSwapper.mediate(z9sessionId, response, clientContext);
                })
                .addInterceptorLast((HttpResponse response, HttpContext context) -> {
                    HttpClientContext clientContext = HttpClientContext.adapt(context);
                    logger.info("secondhand handling response on " + clientContext.getRequest().getRequestLine());

                    response.addHeader("node", nodeId);
                    String z9sessionId = (String) context.getAttribute("zid");

                    if (StringUtils.isBlank(z9sessionId)) {
                        logger.info("no zid for " + clientContext.getRequest().getRequestLine());
                        return;
                    }
                    addZ9SessionCookie(response, clientContext);
                })
                .build();
    }

    private void addZ9SessionCookie(HttpResponse response, HttpClientContext context) {
        boolean unsetZid = context.getAttribute("unsetZid") != null? true: false;
        if (unsetZid) {
            Z9HttpUtils.removeZ9SessionIdFromResponse(response);
            return;
        }
        String z9sessionId = (String) context.getAttribute("zid");
        boolean setZid = (boolean) context.getAttribute("setZid");
        if (setZid && !Z9HttpUtils.isZ9SessionIdSet(response, context)) {
            Z9HttpUtils.addZ9SessionIdToResponse(response, z9sessionId);
        }
    }



    public void processHttp(Z9HttpRequest input) throws IOException, HttpException {
        logger.info(input.getOrigin());
        if (StringUtils.equals(input.getOrigin(), nodeId)) {
            return;
        }

        if ((System.currentTimeMillis() - input.getTimestamp()) >= THIRTY_MIN) {
            String z9SessionId = input.getZ9SessionId();
            if (z9SessionId != null) {
                List<Revival> revivals = sessionHelper.revive(nodeId, z9SessionId);
                revivals.forEach((Revival revival) -> {
                            try {
                                executeHttp(revival.getRequest());
                            } catch (IOException | HttpException e) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                );
            }
        }

        executeHttp(input);
    }

    public Z9HttpResponse executeHttp(Z9HttpRequest input) throws IOException, HttpException {
        HttpRequest request = input.toBasicHttpRequest();
        HttpResponse response = exchange(request);

        return Z9HttpResponse.toZ9HttpResponse(response);
    }

    private HttpResponse exchange(HttpRequest request) throws IOException {
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
