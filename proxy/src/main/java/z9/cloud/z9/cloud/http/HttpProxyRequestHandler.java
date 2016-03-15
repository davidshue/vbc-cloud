package z9.cloud.z9.cloud.http;

import com.zeronines.service.HttpInput;
import com.zeronines.service.HttpOutput;
import com.zeronines.service.HttpService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import z9.cloud.RequestHandler;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class HttpProxyRequestHandler implements RequestHandler {
	private static Log logger = LogFactory.getLog(HttpProxyRequestHandler.class);
	
	private HttpService httpService;

	public HttpProxyRequestHandler(HttpService httpService) {
		this.httpService = httpService;
	}

	public void handleRequest(Socket socket) {
		try {
			while (true) {
				logger.debug("Starting connection ...");
				HttpInput clientInput = HttpClientUtil.readFromClient(socket.getInputStream());
				if (clientInput == null) {
					break;
				}
				HttpOutput output = httpService.doGet(clientInput);
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
