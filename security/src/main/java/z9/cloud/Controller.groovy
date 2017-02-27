package z9.cloud

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@EnableOAuth2Sso
class Controller {

	@RequestMapping('/')
	String home() {

		'Hello World'
	}

}
