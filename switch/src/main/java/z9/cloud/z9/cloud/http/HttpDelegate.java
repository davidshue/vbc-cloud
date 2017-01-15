package z9.cloud.z9.cloud.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import z9.cloud.core2.HttpRetry;
import z9.cloud.core2.Z9HttpRequest;
import z9.cloud.core2.Z9HttpResponse;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Created by dshue1 on 3/14/16.
 */
class HttpDelegate {
	private static Log logger = LogFactory.getLog(HttpDelegate.class);
	@Autowired
	private NodeService nodeService;

	@Autowired
	private HttpRetry httpRetry;

	private SocketAddress endpoint;

	@PostConstruct
	public void afterInit() {
		endpoint = new InetSocketAddress("appazure.zeronines.net", 8080);

	}

	public HttpResponse handle(HttpRequest request) throws IOException, HttpException {
		// TODO: real thing
		Z9HttpResponse z9Resposne = nodeService.httpV1(Z9HttpRequest.toZ9HttpRequest(request));
		return z9Resposne.toBasicHttpResponse();

		/*
		Socket socket = null;
		DefaultBHttpClientConnection activeConn = null;
		try {
			socket = new Socket();
			socket.connect(endpoint, 120000);
			activeConn = new DefaultBHttpClientConnection(8192);
			activeConn.bind(socket);
			activeConn.setSocketTimeout(1000);


			activeConn.sendRequestHeader(request);
			if (request instanceof HttpEntityEnclosingRequest) {
				activeConn.sendRequestEntity((HttpEntityEnclosingRequest)request);
			}
			activeConn.flush();

			HttpResponse response = httpRetry.receiveResponseHeader(activeConn);

			activeConn.receiveResponseEntity(response);

			byte[] bytes = httpRetry.toByteArray(response.getEntity());

			HttpEntity entity = new ByteArrayEntity(bytes);
			response.setEntity(entity);


			logger.info("Received response: {0} " + response);
			return response;

		} finally {
			IOUtils.closeQuietly(activeConn);
			IOUtils.closeQuietly(socket);
		}
		*/
	}
}
