package z9.cloud.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import z9.cloud.ProxyExecutor;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by david on 4/10/17.
 */
public class HttpsProxyExecutor extends ProxyExecutor {
    private static final Log logger = LogFactory.getLog(HttpsProxyExecutor.class);

    private String keystoreName = "server.jks";

    private String keystorePasscode = "letmein";

    public void setKeystoreName(String keystoreName) {
        this.keystoreName = keystoreName;
    }

    public void setKeystorePasscode(String keystorePasscode) {
        this.keystorePasscode = keystorePasscode;
    }

    @Override
    protected  ServerSocketFactory createFactory() {
        System.setProperty("javax.net.ssl.keyStore", keystoreName);
        System.setProperty("javax.net.ssl.keyStorePassword", keystorePasscode);
        return SSLServerSocketFactory.getDefault();
    }

    @Override
    protected ServerSocket createServerSocket() throws IOException {
        ServerSocketFactory ssf = createFactory();

        SSLServerSocket serverSocket = (SSLServerSocket) ssf.createServerSocket(getPort(), getBacklog());
        serverSocket.setReuseAddress(true);
        serverSocket.setNeedClientAuth(false);
        return serverSocket;
    }
}
