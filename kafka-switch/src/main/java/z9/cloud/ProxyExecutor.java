package z9.cloud;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;

public class ProxyExecutor extends Thread implements Proxy {
    private static final Log logger = LogFactory.getLog(ProxyExecutor.class);

	private Executor taskExecutor;
	private RequestHandler handler;
	private ServerSocket serverSocket;
	private int port = 7009;
	private int backlog = 50; 
	private String identifier = "proxy";
	
	private boolean running = false;

	public ProxyExecutor(RequestHandler handler, Executor taskExecutor) {
	    this.handler = handler;
		this.taskExecutor = taskExecutor;
	}

	/**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @param backlog the backlog to set
     */
    public void setBacklog(int backlog) {
        this.backlog = backlog;
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
        ServerSocketFactory ssf = ServerSocketFactory.getDefault();
        try {
            serverSocket = ssf.createServerSocket(port, backlog);
            serverSocket.setReuseAddress(true);
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
        logger.info(identifier + " started, listening on port: " + this.port);
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
                logger.info(e);
            }
        }
        logger.info(identifier + " thread existing");
	}
}