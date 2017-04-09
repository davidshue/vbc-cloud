package z9.cloud.core2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Created by david on 1/15/17.
 */
public class HttpRetry {
    private static Log logger = LogFactory.getLog(HttpRetry.class);
    private int retries = 3;

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public HttpRequest receiveRequestHeader(DefaultBHttpServerConnection conn) throws IOException, HttpException {
        int count = 0;
        while (true) {
            try {
                count++;
                if (count > 1) {
                    logger.debug("receiveRequestHeader " + count + " times");
                }
                return conn.receiveRequestHeader();
            } catch (SocketTimeoutException|ConnectionClosedException e) {
                if (count >= retries) {
                    throw e;
                }
            }
        }
    }

    public HttpResponse receiveResponseHeader(HttpClientConnection activeConn) throws IOException, HttpException {
        int count = 0;
        while (true) {
            try {
                count++;
                if (count > 1) {
                    logger.debug("receiveResponseHeader " + count + " times");
                }
                return activeConn.receiveResponseHeader();
            } catch (SocketTimeoutException|ConnectionClosedException e) {
                if (count >= retries) {
                    throw e;
                }
            }

        }
    }

    public byte[] toByteArray(HttpEntity entity) throws IOException {
        int count = 0;
        while (true) {
            try {
                count++;
                if (count > 1) {
                    logger.debug("toByteArray " + count + " times");
                }
                return EntityUtils.toByteArray(entity);
            } catch (SocketTimeoutException|ConnectionClosedException e) {
                if (count >= retries) {
                    throw e;
                }
            }
        }
    }
}
