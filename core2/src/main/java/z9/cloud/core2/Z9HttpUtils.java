package z9.cloud.core2;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;

/**
 * Created by david on 1/15/17.
 */
public abstract class Z9HttpUtils {
    public static String getZ9SessionId(HttpRequest request) {
        Header[] cookies = request.getHeaders("Cookie");
        for (Header cookie : cookies) {
            for (HeaderElement headerElement : cookie.getElements()) {
                if (headerElement.getName().equals("z9sessionid")) {
                    return headerElement.getValue();
                }
                NameValuePair nvp = headerElement.getParameterByName("z9sessionid");
                if (nvp != null) {
                    return nvp.getValue();
                }
            }
        }
        return null;
    }

    public static void addZ9SessionIdToRequest(HttpRequest request, String id) {
        request.addHeader("Cookie", "z9sessionid=" + id);
    }

    public static void addZ9SessionIdToResponse(HttpResponse response, String id) {
        response.addHeader("Set-Cookie", "z9sessionid=" + id + "; path=/; HttpOnly");
    }
}
