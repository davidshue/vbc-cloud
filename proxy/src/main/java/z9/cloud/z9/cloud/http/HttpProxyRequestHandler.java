package z9.cloud.z9.cloud.http;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import z9.cloud.RequestHandler;
import z9.cloud.core.HttpInput;
import z9.cloud.core.HttpOutput;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;


public class HttpProxyRequestHandler implements RequestHandler {
	private static Log logger = LogFactory.getLog(HttpProxyRequestHandler.class);
	
	private HttpDelegate httpDelegate;

	public HttpProxyRequestHandler(HttpDelegate httpDelegate) {
		this.httpDelegate = httpDelegate;
	}

	public void handleRequest(Socket socket) {
		try {
			while (true) {
				logger.debug("Starting connection ...");
				HttpInput clientInput = HttpClientUtil.readFromClient(socket.getInputStream());
				if (clientInput == null) {
					break;
				}
				HttpOutput output = httpDelegate.handle(clientInput);
				if (output == null || output.getPayload() == null) {
				    return;
				}
				output.write(socket.getOutputStream());
				socket.getOutputStream().flush();		
			}
		} catch (SocketException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		} catch (Exception e) {
		    logger.error(e.getMessage());
		}
		finally {
			try {
				socket.close();
			} catch (IOException e) {
				// Do nothing
			}
			logger.debug("Connection Closed ...");
		}
	}
}
