package z9.cloud;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.CircularRedirectException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.RedirectLocations;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;
import org.apache.http.util.TextUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

/**
 * Created by david on 4/11/17.
 */
public class SSLRedirectStrategy extends DefaultRedirectStrategy {
    private final Log log = LogFactory.getLog(getClass());


    @Override
    public URI getLocationURI(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        Args.notNull(request, "HTTP request");
        Args.notNull(response, "HTTP response");
        Args.notNull(context, "HTTP context");
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        Header locationHeader = response.getFirstHeader("location");
        if(locationHeader == null) {
            throw new ProtocolException("Received redirect response " + response.getStatusLine() + " but no location header");
        } else {
            String location = locationHeader.getValue();
            if(this.log.isDebugEnabled()) {
                this.log.debug("Redirect requested to location '" + location + "'");
            }

            RequestConfig config = clientContext.getRequestConfig();
            URI uri = this.createLocationURI(request, location);

            try {
                if(!uri.isAbsolute()) {
                    if(!config.isRelativeRedirectsAllowed()) {
                        throw new ProtocolException("Relative redirect location '" + uri + "' not allowed");
                    }

                    HttpHost target = clientContext.getTargetHost();
                    Asserts.notNull(target, "Target host");
                    URI requestURI = new URI(request.getRequestLine().getUri());
                    URI absoluteRequestURI = URIUtils.rewriteURI(requestURI, target, false);
                    uri = URIUtils.resolve(absoluteRequestURI, uri);
                }
            } catch (URISyntaxException var12) {
                throw new ProtocolException(var12.getMessage(), var12);
            }

            RedirectLocations redirectLocations = (RedirectLocations)clientContext.getAttribute("http.protocol.redirect-locations");
            if(redirectLocations == null) {
                redirectLocations = new RedirectLocations();
                context.setAttribute("http.protocol.redirect-locations", redirectLocations);
            }

            if(!config.isCircularRedirectsAllowed() && redirectLocations.contains(uri)) {
                throw new CircularRedirectException("Circular redirect to '" + uri + "'");
            } else {
                redirectLocations.add(uri);
                return uri;
            }
        }
    }

    private URI createLocationURI(HttpRequest request, String location) throws ProtocolException {
        try {
            URIBuilder b = new URIBuilder((new URI(location)).normalize());

            String host = b.getHost();
            if(host != null) {
                host = host.toLowerCase(Locale.ROOT);
                Header hostHeader = request.getFirstHeader("Host");
                if (hostHeader != null) {
                    String[] s = hostHeader.getValue().split(":");
                    String currentHost = s[0];
                    if (host.equals(currentHost)) {
                        b.setHost(null);
                        b.setPort(-1);
                        b.setScheme(null);
                    }
                    else {
                        b.setHost(host);
                    }
                }
            }

            String path = b.getPath();
            if(TextUtils.isEmpty(path)) {
                b.setPath("/");
            }

            return b.build();
        } catch (URISyntaxException var5) {
            throw new ProtocolException("Invalid redirect URI: " + location, var5);
        }
    }
}
