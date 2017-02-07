package z9.cloud

import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitTestAppConfig {
	@Bean
	connectionFactory(@Value('${spring.rabbitmq.addresses}') String addresses,
					  @Value('${spring.rabbitmq.username}') String username,
					  @Value('${spring.rabbitmq.password}') String password) {
		new CachingConnectionFactory(addresses: addresses, username:username, password:password)
	}
	
	@Bean
	rabbitTemplate() {
		new RabbitTemplate(connectionFactory())
	}

	@Bean
	FanoutExchange exchange() {
		new FanoutExchange('http_exchange')
	}
	
	@Bean
	org.springframework.amqp.core.Queue httpQueue() {
		new org.springframework.amqp.core.Queue('http_queue')
	}

	@Bean
	org.springframework.amqp.core.Binding binding() {
		BindingBuilder.bind(httpQueue()).to(exchange())
	}
}
