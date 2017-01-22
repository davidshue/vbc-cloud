package z9.cloud.core2;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.MalformedCookieException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by david on 1/15/17.
 */
public abstract class Z9HttpUtils {
    public final static String Z9_SESSION_ID = "z9sessionid";
    public static String getZ9SessionId(HttpRequest request) {
        Header[] cookies = request.getHeaders("Cookie");
        for (Header cookie : cookies) {
            for (HeaderElement headerElement : cookie.getElements()) {
                if (headerElement.getName().equals(Z9_SESSION_ID)) {
                    return headerElement.getValue();
                }
                NameValuePair nvp = headerElement.getParameterByName(Z9_SESSION_ID);
                if (nvp != null) {
                    return nvp.getValue();
                }
            }
        }
        return null;
    }

    public static boolean isZ9SessionIdSet(HttpResponse response, HttpClientContext context)  {
        CookieOrigin origin = context.getCookieOrigin();
        CookieSpec spec = context.getCookieSpec();

        for (Header header : response.getHeaders("Set-Cookie")) {
            try {
                for (Cookie cookie : spec.parse(header, origin)) {
                    if (cookie.getName().equals(Z9_SESSION_ID)) {
                        return true;
                    }
                }
            } catch (MalformedCookieException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
    
    
    public static Map<String, String> getAllCookies(HttpRequest request) {
        Map<String, String> result = new LinkedHashMap<>();
        Header[] cookies = request.getHeaders("Cookie");
        for (Header cookie : cookies) {
            result.put(cookie.getName(), cookie.getValue());
            for (HeaderElement he : cookie.getElements()) {
                for (NameValuePair nv : he.getParameters()) {
                    result.put(nv.getName(), nv.getValue());
                }
            }
        }
        
        return result;
    }

    public static String randomZ9SessionId() {
        return UUID.randomUUID().toString().replaceAll("-", "X");
    }

    public static void addZ9SessionIdToRequest(HttpRequest request, String id) {
        request.addHeader("Cookie", Z9_SESSION_ID + "=" + id);
    }

    public static void addZ9SessionIdToResponse(HttpResponse response, String id) {
        response.addHeader("Set-Cookie", Z9_SESSION_ID + "=" + id + "; path=/; HttpOnly");
    }
}
