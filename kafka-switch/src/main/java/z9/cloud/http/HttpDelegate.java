package z9.cloud.http;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import z9.cloud.core2.Z9HttpRequest;
import z9.cloud.core2.Z9HttpResponse;
import z9.cloud.core2.Z9HttpUtils;

import java.io.IOException;

/**
 * Created by dshue1 on 3/14/16.
 */
class HttpDelegate {
	private static Log logger = LogFactory.getLog(HttpDelegate.class);
	@Autowired
	private NodeService nodeService;

	public HttpResponse handle(HttpRequest request) throws IOException, HttpException {
		Z9HttpRequest z9Request = Z9HttpRequest.toZ9HttpRequest(request);
		if (Z9HttpUtils.getZ9SessionId(request) == null) {
			z9Request.setNewZid(Z9HttpUtils.randomZ9SessionId());
		}
		Z9HttpResponse z9Response = nodeService.httpV1(z9Request);
		HttpResponse response = z9Response.toBasicHttpResponse();

		Header zsessionHeader = response.getFirstHeader("zsession-reset");
		if(zsessionHeader != null) {
			String id = zsessionHeader.getValue();
			if (StringUtils.isBlank(id)) {
				Z9HttpUtils.removeZ9SessionIdFromResponse(response);
			}
			else {
				Z9HttpUtils.addZ9SessionIdToResponse(response, id);
			}
		}
		logger.info("response from " + response.getFirstHeader("node"));
		return response;
	}
}
