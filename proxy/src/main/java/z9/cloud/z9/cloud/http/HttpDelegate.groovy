package z9.cloud.z9.cloud.http

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate
import z9.cloud.core.HttpInput
import z9.cloud.core.HttpOutput

import javax.annotation.PostConstruct
/**
 * Created by dshue1 on 3/14/16.
 */
class HttpDelegate {
	private RestTemplate restTemplate
	private HttpHeaders headers = new HttpHeaders(contentType: MediaType.APPLICATION_JSON)

	@Value('${gateway.http.url}')
	private String gatewayHttp

	@Value('${gateway.http.read.timeout}')
	private int readTimeout

	@Value('${gateway.http.connect.timeout}')
	private int connectTimeout


	@PostConstruct
	void postConstruct() {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory()
		factory.setReadTimeout(readTimeout)
		factory.setConnectTimeout(connectTimeout)
		restTemplate = new RestTemplate(factory)
	}

	HttpOutput handle(HttpInput input) {
		HttpEntity<HttpInput> entity = new HttpEntity<>(input, headers)

		ResponseEntity<HttpOutput> output = restTemplate.exchange(gatewayHttp,
			HttpMethod.POST, entity, HttpOutput.class)

		output.body
	}
}
