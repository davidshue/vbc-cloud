package z9.cloud

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

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
}
