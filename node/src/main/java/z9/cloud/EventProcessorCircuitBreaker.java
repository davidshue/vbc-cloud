package z9.cloud;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import z9.cloud.core2.HttpRetry;
import z9.cloud.core2.Z9HttpUtils;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by david on 4/21/17.
 */
@Component
public class EventProcessorCircuitBreaker {
    private final Log logger = LogFactory.getLog(getClass());

    @Value("${http.port}")
    private int serverPort;

    @Value("${http.server}")
    private String serverAddress;

    @Value("${http.protocol}")
    private String protocol;

    @Value("${http.revive.logout}")
    private String logoutUrl;

    //@Value("${eureka.instance.hostname}:${server.port}")
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
    public void afterInit() throws NoSuchAlgorithmException, KeyManagementException {
        httpHost = new HttpHost(serverAddress, serverPort, protocol);

        SSLContext sslContext = SSLContext.getInstance("SSL");

        // set up a TrustManager that trusts everything
        sslContext.init(null, new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers()  {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs,
                                           String authType) throws CertificateException {}

            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
        } }, new SecureRandom());

        httpClient = HttpClients.custom().disableRedirectHandling()
                .addInterceptorFirst((HttpRequestInterceptor) (request, context) -> {
                    request.removeHeaders(HTTP.CONTENT_LEN);
                    /*
                    Header hostHeader = request.getFirstHeader("Host");
                    if (hostHeader != null) {
                        request.removeHeader(hostHeader);
                        hostHeader = new BasicHeader("Host", serverAddress + ":" + serverPort);
                        request.addHeader(hostHeader);
                    }
                    */
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
                    mediateLocationHeader(response, context);
                    HttpClientContext clientContext = HttpClientContext.adapt(context);
                    logger.debug("firsthand handling response on " + clientContext.getRequest().getRequestLine());

                    String z9sessionId = (String) context.getAttribute("zid");
                    if (StringUtils.isBlank(z9sessionId)) {
                        logger.debug("no zid for " + clientContext.getRequest().getRequestLine());
                        return;
                    }

                    if (response.getHeaders("Set-Cookie").length == 0) {
                        logger.debug("no set-cookie for " + clientContext.getRequest().getRequestLine());
                        return;
                    }

                    cookieSwapper.mediate(z9sessionId, response, clientContext);
                })
                .addInterceptorLast((HttpResponse response, HttpContext context) -> {
                    HttpClientContext clientContext = HttpClientContext.adapt(context);
                    logger.debug("secondhand handling response on " + clientContext.getRequest().getRequestLine());

                    response.removeHeaders("node");
                    response.addHeader("node", nodeId);
                    String z9sessionId = (String) context.getAttribute("zid");

                    if (StringUtils.isBlank(z9sessionId)) {
                        logger.debug("no zid for " + clientContext.getRequest().getRequestLine());
                        return;
                    }
                    addZ9SessionCookie(response, clientContext);
                })
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
                .setRedirectStrategy(new SSLRedirectStrategy())
                .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                .build();
    }

    private void addZ9SessionCookie(HttpResponse response, HttpClientContext context) {
        if (context.getRequest().getRequestLine().getUri().startsWith(logoutUrl)) {
            response.removeHeaders("zsession-reset");
            response.addHeader(new BasicHeader("zsession-reset", ""));
        }

        String z9sessionId = (String) context.getAttribute("zid");
        boolean setZid = (boolean) context.getAttribute("setZid");
        if (setZid && !Z9HttpUtils.isZ9SessionIdSet(response, context)) {
            response.removeHeaders("zsession-reset");
            response.addHeader(new BasicHeader("zsession-reset", z9sessionId));
        }
    }

    private void mediateLocationHeader(HttpResponse response, HttpContext context) {
        Header locationHeader = response.getFirstHeader("Location");
        if (locationHeader == null || StringUtils.isBlank(locationHeader.getValue())) {
            return;
        }
        try {
            HttpClientContext clientContext = HttpClientContext.adapt(context);

            Header sslHeader = clientContext.getRequest().getFirstHeader("onSsl");
            boolean onSsl = false;
            if (sslHeader != null) {
                onSsl = Integer.valueOf(sslHeader.getValue()) == 1;
            }
            if (!onSsl) {
                rewriteLocation(response, locationHeader, false);
                return;
            }
            String domain = "";
            Header hostHeader = clientContext.getRequest().getFirstHeader("Host");
            if (hostHeader != null && StringUtils.isNotBlank(hostHeader.getValue())) {
                String[] args = hostHeader.getValue().split(":");
                if (args != null && args.length >= 1) {
                    domain = args[0];
                }
            }
            if (StringUtils.isBlank(domain)) {
                return;
            }

            URI uri = new URI(locationHeader.getValue());
            if (uri.getScheme() != null && StringUtils.equalsIgnoreCase(domain, uri.getHost())) {
                rewriteLocation(response, locationHeader, true);
            }

        } catch (URISyntaxException e) {
            logger.warn(e.getMessage(), e);
        }
    }

    private void rewriteLocation(HttpResponse res, Header location, boolean onSsl) {
        try {
            URI uri = new URI(location.getValue());
            if (onSsl) {
                if ("https".equals(uri.getScheme())) {
                    return;
                }
            }
            else {
                if (!"https".equals(uri.getScheme())) {
                    return;
                }
            }
            res.removeHeader(location);
            URIBuilder uriBuilder = new URIBuilder(uri);
            String scheme = onSsl? "https" : "http";
            uriBuilder.setScheme(scheme);
            location = new BasicHeader("Location", uriBuilder.build().toString());
            res.addHeader(location);
        } catch (URISyntaxException e) {
            logger.warn(e.getMessage(), e);
        }
    }

    @HystrixCommand(fallbackMethod = "fallback")
    public HttpResponse exchange(HttpRequest request) throws IOException {
        CloseableHttpResponse response = null;

        try {
            response = httpClient.execute(httpHost, request);

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                byte[] bytes = httpRetry.toByteArray(response.getEntity());
                System.out.println("bytes length: " + bytes.length);

                HttpEntity byteArrayEntity = new ByteArrayEntity(bytes);
                response.setEntity(byteArrayEntity);
            }
            logger.info("Received response: {0} " + response);
            return response;
        } catch (Exception e) {
            logger.warn("node circuitbreaker tripped", e);
            throw e;
        } finally {
            IOUtils.closeQuietly(response);
        }
    }

    public HttpResponse fallback(HttpRequest request) throws IOException {
        logger.warn("Circuit Breaker tripped for " + nodeId);
        BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1,
                HttpStatus.SC_OK, "OK");


        BasicHttpEntity entity = new BasicHttpEntity();
        String error = "App Server for " + nodeId + " is not available";
        byte[] message = error.getBytes(Charset.forName("UTF-8"));
        entity.setContent(new ByteArrayInputStream(message));
        entity.setContentLength(message.length);
        response.setEntity(entity);

        // force Content-Length header so the client doesn't expect us to close the connection to end the response
        response.addHeader("Content-Length", String.valueOf(message.length));

        return response;
    }
}
