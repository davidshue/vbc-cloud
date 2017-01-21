package z9.cloud.core2;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by david on 1/18/17.
 */
public class HttpCoreTest {
    @Test
    public void testEntity() throws Exception {
        CloseableHttpClient httpclient = HttpClients.custom()
                .addInterceptorFirst((HttpRequestInterceptor) (request, context) -> {
                    HttpClientContext clientContext = HttpClientContext.adapt(context);
                    CookieOrigin origin = clientContext.getCookieOrigin();
                    System.out.println(origin);
                    CookieSpec spec = clientContext.getCookieSpec();
                    System.out.println(spec);
                    System.out.println("Right before HTTP call");
                    context.setAttribute("needZ9sessionid", "1");
                    //AtomicInteger count = (AtomicInteger) context.getAttribute("count");
                    //request.addHeader("Count", Integer.toString(count.getAndIncrement()));
                })
                .addInterceptorLast((HttpResponse response, HttpContext context) -> {
                    //HttpClientContext clientContext = (HttpClientContext) context;
                    HttpClientContext clientContext = HttpClientContext.adapt(context);
                    CookieOrigin origin = clientContext.getCookieOrigin();
                    System.out.println(origin);
                    System.out.println(origin.getHost());

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
                })
                .build();
        HttpGet httpget = new HttpGet("https://www.google.com");

        System.out.println(httpget.getProtocolVersion());
        assertEquals("https://www.google.com", httpget.getURI().toString());

        CloseableHttpResponse response = httpclient.execute(httpget);
        for (Header header : response.getAllHeaders()) {
            System.out.println(header.toString());
            System.out.println(header.getName());
            System.out.println(header.getValue());

            for (HeaderElement headerElement : header.getElements()) {
                System.out.println("\t" + headerElement.getName());
                System.out.println("\t" + headerElement.getValue());
                for (NameValuePair nameValuePair : headerElement.getParameters()) {
                    System.out.println("\t\t" + nameValuePair.getName() + "|||" + nameValuePair.getValue());
                }
            }
        }
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String out = EntityUtils.toString(entity);
                System.out.println(out);
            }
        } finally {
            response.close();
        }
    }
}
