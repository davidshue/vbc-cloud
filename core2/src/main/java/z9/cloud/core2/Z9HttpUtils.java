package z9.cloud.core2;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;

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
