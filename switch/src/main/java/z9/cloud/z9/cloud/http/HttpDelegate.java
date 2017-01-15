package z9.cloud.z9.cloud.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by dshue1 on 3/14/16.
 */
class HttpDelegate {
	@Autowired
	private NodeService nodeService;

	HttpResponse handle(HttpRequest request) throws IOException, HttpException {
		// TODO: real thing
		//nodeService.httpV1(input)

		Socket socket = null;
		DefaultBHttpClientConnection activeConn = null;
		try {
			SocketAddress endpoint = new InetSocketAddress("appazure.zeronines.net", 8080);

			socket = new Socket();
			socket.connect(endpoint, 2000);

			activeConn = new DefaultBHttpClientConnection(8192);
			activeConn.bind(socket);
			activeConn.setSocketTimeout(1000);


			activeConn.sendRequestHeader(request);
			if (request instanceof HttpEntityEnclosingRequest) {
				activeConn.sendRequestEntity((HttpEntityEnclosingRequest)request);
			}
			activeConn.flush();

			HttpResponse response = activeConn.receiveResponseHeader();

			activeConn.receiveResponseEntity(response);

			byte[] bytes = EntityUtils.toByteArray(response.getEntity());
			//ContentType type = ContentType.parse(response.getEntity().getContentType().getValue());
			HttpEntity entity = new ByteArrayEntity(bytes);
			response.setEntity(entity);
			//EntityUtils.consume(response.getEntity());


			return response;

		} finally {
			IOUtils.closeQuietly(activeConn);
			IOUtils.closeQuietly(socket);
		}
	}
}
