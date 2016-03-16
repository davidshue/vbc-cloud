package z9.cloud

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import z9.cloud.z9.cloud.http.HttpDelegate
import z9.cloud.z9.cloud.http.HttpProxyRequestHandler

/**
 * Created by dshue1 on 3/14/16.
 */

@Configuration
class ProxyConfig {
	@Bean
	taskExecutor() {
		new ThreadPoolTaskExecutor(
			corePoolSize: 50,
			maxPoolSize: 100,
			queueCapacity: 2000
		)
	}

	@Bean
	httpDelegate() {
		new HttpDelegate()
	}

	@Bean
	httpHandler() {
		new HttpProxyRequestHandler(httpDelegate())
	}

	@Bean
	httpProxy() {
		ProxyExecutor proxy = new ProxyExecutor(httpHandler(), taskExecutor())
		proxy.startExecutor()

		proxy
	}
}
