package z9.cloud

import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class RabbitAppConfig {
	@Bean
	String env(Environment env) {
		env.activeProfiles.length == 0 ? 'default' : env.activeProfiles[0]
	}

	@Bean
	connectionFactory(@Value('${spring.rabbitmq.addresses}') String addresses,
	                  @Value('${spring.rabbitmq.username}') String username,
	                  @Value('${spring.rabbitmq.password}') String password) {
		new CachingConnectionFactory(addresses: addresses, username:username, password:password)
	}
	
	@Bean
	amqpAdmin(ConnectionFactory factory) {
		new RabbitAdmin(factory)
	}

	@Bean
	FanoutExchange exchange() {
		new FanoutExchange('http_exchange')
	}
}
