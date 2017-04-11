package z9.cloud;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.Socket;

public class RequestRunner extends Thread  {
    private static final Log logger = LogFactory.getLog(RequestRunner.class);

    private RequestHandler handler;
    private Socket socket;
    private boolean secure = false;

    public RequestRunner(RequestHandler handler, Socket socket, boolean secure) {
        this.handler = handler;
        this.socket = socket;
        this.secure = secure;
    }

    public void run() {
    	logger.debug("request runner started...");
        handler.handleRequest(socket, secure);
    }
}
