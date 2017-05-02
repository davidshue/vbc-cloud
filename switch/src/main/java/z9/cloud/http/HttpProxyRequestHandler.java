package z9.cloud.http;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.ConnectionClosedException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpServerConnection;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import z9.cloud.RequestHandler;
import z9.cloud.core2.HttpRetry;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;


public class HttpProxyRequestHandler implements RequestHandler {
	private static Log logger = LogFactory.getLog(HttpProxyRequestHandler.class);
	
	private HttpDelegate httpDelegate;

	@Autowired
    private HttpRetry httpRetry;

	public HttpProxyRequestHandler(HttpDelegate httpDelegate) {
		this.httpDelegate = httpDelegate;
	}

	@Override
	public void handleRequest(Socket socket, boolean secure) {
		DefaultBHttpServerConnection conn = null;
		try {
			conn = new DefaultBHttpServerConnection(8 * 1024);
			conn.setSocketTimeout(2000);
			conn.bind(socket);

			boolean keepAlive = true;
			while( keepAlive && !socket.isClosed() ) {
				// fully read the request, whatever it is
				HttpRequest request = httpRetry.receiveRequestHeader(conn);
				logger.info("Received request: {0} " + request);
				keepAlive = isKeepAlive(request);

				if (secure) {
					request.addHeader(new BasicHeader("onSsl", "1"));
				}
				else {
					request.addHeader(new BasicHeader("onSsl", "0"));
				}

				if (request instanceof HttpEntityEnclosingRequest) {
					conn.receiveRequestEntity((HttpEntityEnclosingRequest) request);
					HttpEntity entity = ((HttpEntityEnclosingRequest) request)
							.getEntity();
					if (entity != null) {
						// consume all content to allow reuse
						byte[] bytes = httpRetry.toByteArray(entity);
						ContentType type = ContentType.parse(entity.getContentType().getValue());
						HttpEntity byteEntity = new ByteArrayEntity(bytes, type);
						((HttpEntityEnclosingRequest) request).setEntity(byteEntity);
					}
				}

				HttpResponse response = handle(request);

				if (request.getRequestLine().getUri().startsWith("/enc/")) {
					logger.debug("require zencode: " + response.getStatusLine());
					Header header = response.getFirstHeader("Location");
					if (header != null) {
						logger.debug("zencode redirect " + header.getValue());
					}
				}

				conn.sendResponseHeader(response);
				conn.sendResponseEntity(response);
				conn.flush();
			}



		} catch (SocketException|ConnectionClosedException e) {
			logger.warn(e.getMessage());
		} catch (Exception e) {
		    logger.error(e.getMessage(), e);
		}
		finally {
			IOUtils.closeQuietly(conn);
			logger.debug("Connection Closed ...");
		}
	}

	protected boolean isKeepAlive(HttpRequest request) {
		for (Header header: request.getAllHeaders()) {
			String name = header.getName().toLowerCase();
			if ("connection".equals(name) || "proxy-connection".equals(name)) {
				String value = header.getValue();
				if ("keep-alive".equalsIgnoreCase(value))
					return true;
			}
		}
		return false;
	}

	protected HttpResponse handle(HttpRequest request) throws IOException, HttpException {
	    return httpDelegate.handle(request);
    }

	protected void sendOkContent(HttpServerConnection conn) throws IOException, HttpException {
		// send a 200 OK with the static content
		BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1,
				HttpStatus.SC_OK, "OK") ;
		BasicHttpEntity entity = new BasicHttpEntity();
		byte[] message = "nice 256".getBytes(Charset.forName("UTF-8"));
		entity.setContent(new ByteArrayInputStream(message));
		entity.setContentLength(message.length);
		response.setEntity(entity);

		// force Content-Length header so the client doesn't expect us to close the connection to end the response
		response.addHeader("Content-Length", String.valueOf(message.length));

		conn.sendResponseHeader(response);
		conn.sendResponseEntity(response);
		conn.flush();
		logger.info("Sent 200 OK");
	}

	protected void rejectMethod(HttpServerConnection conn) throws IOException, HttpException {
		BasicHttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1,
				HttpStatus.SC_METHOD_NOT_ALLOWED, "Must be GET, POST or PUT");
		conn.sendResponseHeader(response);
		conn.flush();
		logger.info("Sent 405 Method Not Allowed");
	}
}
