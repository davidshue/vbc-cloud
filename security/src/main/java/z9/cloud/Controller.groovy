package z9.cloud

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@EnableOAuth2Sso
class Application {

	@RequestMapping('/')
	String home() {

		'Hello World'
	}

}
