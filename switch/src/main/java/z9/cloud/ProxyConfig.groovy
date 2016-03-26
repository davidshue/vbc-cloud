package z9.cloud

import org.springframework.beans.factory.annotation.Value
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
	@Value('${threadpool.queue.size.core}') private int coreSize
	@Value('${threadpool.queue.size.max}') private int maxSize
	@Value('${threadpool.queue.size.capacity}') private int capacity

	@Bean
	taskExecutor() {
		new ThreadPoolTaskExecutor(
			corePoolSize: coreSize,
			maxPoolSize: maxSize,
			queueCapacity: capacity
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
