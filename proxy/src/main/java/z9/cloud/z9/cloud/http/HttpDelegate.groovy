package z9.cloud.z9.cloud.http

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

import javax.annotation.PostConstruct

/**
 * Created by dshue1 on 3/14/16.
 */
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
}
