package z9.cloud.z9.cloud.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import z9.cloud.core2.Z9HttpRequest;
import z9.cloud.core2.Z9HttpResponse;

import java.io.IOException;

/**
 * Created by dshue1 on 3/14/16.
 */
class HttpDelegate {
	private static Log logger = LogFactory.getLog(HttpDelegate.class);
	@Autowired
	private NodeService nodeService;

	public HttpResponse handle(HttpRequest request) throws IOException, HttpException {
		Z9HttpResponse z9Response = nodeService.httpV1(Z9HttpRequest.toZ9HttpRequest(request));
		HttpResponse response = z9Response.toBasicHttpResponse();
		return response;
	}
}
