package z9.cloud.z9.cloud.http

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import z9.cloud.core.HttpInput
import z9.cloud.core.HttpOutput

import javax.annotation.PostConstruct
/**
 * Created by dshue1 on 3/14/16.
 */
@Component
class HttpDelegate {
	private RestTemplate restTemplate
	private HttpHeaders headers = new HttpHeaders(contentType: MediaType.APPLICATION_JSON)


	@PostConstruct
	void postConstruct() {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory()
		factory.setReadTimeout(120_000)
		factory.setConnectTimeout(60_000)
		restTemplate = new RestTemplate(factory)
	}

	HttpOutput handle(HttpInput input) {
		HttpEntity<HttpInput> entity = new HttpEntity<>(input, headers)

		restTemplate.exchange('http://localhost:8005/node/v1/http',
			HttpMethod.POST, entity, HttpOutput.class)
	}
}
