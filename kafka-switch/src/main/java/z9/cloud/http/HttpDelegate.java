package z9.cloud.http;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import z9.cloud.core2.Z9Header;
import z9.cloud.core2.Z9HttpRequest;
import z9.cloud.core2.Z9HttpResponse;
import z9.cloud.core2.Z9HttpUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dshue1 on 3/14/16.
 */
class HttpDelegate {
	private static Log logger = LogFactory.getLog(HttpDelegate.class);

	@Autowired
	private DiscoveryClient client;

	private String gatewayUri;
	private RestTemplate restTemplate;

	@PostConstruct
	public void after() {
		restTemplate = new RestTemplate();

		List<ServiceInstance> gateways = client.getInstances("gateway");
		Assert.notEmpty(gateways, "the gateway is not running, switch startup aborted.");

		// for the time being, get the first gateway, we may need to use feign client if there are multiple gateways.
		ServiceInstance si = gateways.get(0);
		gatewayUri = si.getUri().toString();
		logger.info("################# gateway at " + gatewayUri);
	}

	public HttpResponse handle(HttpRequest request) throws IOException, HttpException {
		Z9HttpRequest z9Request = Z9HttpRequest.toZ9HttpRequest(request);
		if (Z9HttpUtils.getZ9SessionId(request) == null) {
			z9Request.setNewZid(Z9HttpUtils.randomZ9SessionId());
		}
		final HttpHeaders httpHeaders = new HttpHeaders();
		Arrays.stream(request.getHeaders("Cookie")).forEach(header ->
			httpHeaders.add(header.getName(), header.getValue())
		);
		HttpEntity<Z9HttpRequest> he = new HttpEntity<>(z9Request, httpHeaders);

		ResponseEntity<Z9HttpResponse> re = restTemplate.exchange(
				gatewayUri + "/node/v1/http",
				HttpMethod.POST, he, Z9HttpResponse.class);

		Z9HttpResponse z9Response = re.getBody();
		List<String> setCookies = re.getHeaders().get("Set-Cookie");
		if (setCookies != null && setCookies.size() > 0) {
			setCookies.forEach(it -> {
				Z9Header zh = new Z9Header();
				zh.setName("Set-Cookie");
				zh.setValue(it);
				z9Response.getHeaders().add(zh);
			});
		}

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
