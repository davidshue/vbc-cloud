package z9.cloud.z9.cloud.http;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import z9.cloud.core2.Z9HttpRequest;
import z9.cloud.core2.Z9HttpResponse;
import z9.cloud.core2.Z9HttpUtils;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by dshue1 on 3/14/16.
 */
class HttpDelegate {
	private static Log logger = LogFactory.getLog(HttpDelegate.class);
	@Autowired
	private NodeService nodeService;

	public HttpResponse handle(HttpRequest request) throws IOException, HttpException {
		// TODO: real thing
		String zid = Z9HttpUtils.getZ9SessionId(request);
		boolean woZid = StringUtils.isBlank(zid);
		String newZid = "";
		if (StringUtils.isBlank(zid)) {
			newZid = UUID.randomUUID().toString();
			Z9HttpUtils.addZ9SessionIdToRequest(request, newZid);
		}

		Z9HttpResponse z9Response = nodeService.httpV1(Z9HttpRequest.toZ9HttpRequest(request));
		HttpResponse response = z9Response.toBasicHttpResponse();

		if (woZid) {
			Z9HttpUtils.addZ9SessionIdToResponse(response, newZid);
		}

		return response;

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
