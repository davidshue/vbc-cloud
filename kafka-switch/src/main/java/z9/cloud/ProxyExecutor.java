package z9.cloud;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;

public abstract class ProxyExecutor extends Thread implements Proxy {
    private static final Log logger = LogFactory.getLog(ProxyExecutor.class);

	private Executor taskExecutor;
	private RequestHandler handler;
    private int port = -1;
    private int backlog = -1;
	private ServerSocket serverSocket;
	private String identifier = "proxy";
	
	private boolean running = false;

    public void setTaskExecutor(Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void setHandler(RequestHandler handler) {
        this.handler = handler;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public int getBacklog() {
        return backlog;
    }

    /* (non-Javadoc)
                 * @see com.zeronines.proxy.server.Proxy#getIdentifier()
                 */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @param identifier the identifier to set
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * @return the running
     */
    public boolean isRunning() {
        return running;
    }

    /* (non-Javadoc)
     * @see com.zeronines.proxy.server.Proxy#startExecutor()
     */
    public void startExecutor() {
        logger.info("Starting " + identifier + "...");
        if (running) {
            return;
        }
        try {
            serverSocket = createServerSocket();
            this.running = true;
            Thread t = new Thread(this);
            t.start();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }       
	}   
    
    public void stopExecutor() {
        logger.info("Stopping " + identifier + "...");
        if (!running) {
            return;
        }
        try {
            serverSocket.close();
            serverSocket = null;
            this.running = false;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
	
	public void run() {
        logger.info(identifier + " started, listening on port: " + port);
        while (running) {
            try {
                logger.debug("waiting...");
                Socket s = serverSocket.accept();
                s.setTcpNoDelay(true);
                s.setSoTimeout( 200 * 1000 );

                // Log some debugging information
                InetAddress addr = s.getInetAddress();
                logger.debug("Received a new connection from (" + addr.getHostAddress() + "): " + addr.getHostName());

                RequestRunner runner = new RequestRunner(handler, s);
                taskExecutor.execute(runner);

            }  catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        logger.info(identifier + " thread exiting...");
	}

    protected  ServerSocketFactory createFactory() {
        return ServerSocketFactory.getDefault();
    }

    protected abstract ServerSocket createServerSocket() throws IOException;
}