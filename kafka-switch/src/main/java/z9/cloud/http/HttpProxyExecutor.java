package z9.cloud.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import z9.cloud.ProxyExecutor;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by david on 4/10/17.
 */
public class HttpProxyExecutor extends ProxyExecutor {
    private static final Log logger = LogFactory.getLog(HttpProxyExecutor.class);

    @Override
    protected ServerSocket createServerSocket() throws IOException {
        ServerSocketFactory ssf = createFactory();

        ServerSocket serverSocket = ssf.createServerSocket(getPort(), getBacklog());
        serverSocket.setReuseAddress(true);
        return serverSocket;
    }
}
