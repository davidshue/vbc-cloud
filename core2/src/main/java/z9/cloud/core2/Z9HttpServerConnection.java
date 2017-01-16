package z9.cloud.core2;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.impl.DefaultBHttpServerConnection;

/**
 * Created by david on 1/15/17.
 */
public class Z9HttpServerConnection extends DefaultBHttpServerConnection {
    public Z9HttpServerConnection(int buffersize) {
        super(buffersize);
    }

    @Override
    protected void onRequestReceived(HttpRequest request) {
    }

    @Override
    protected void onResponseSubmitted(HttpResponse response) {
    }
}
