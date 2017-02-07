package z9.cloud

import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitTestAppConfig {
	@Bean
	ConnectionFactory connectionFactory() {
		new CachingConnectionFactory(addresses: 'localhost', username:'guest', password:'guest')
	}

	@Bean
	rabbitTemplate(ConnectionFactory factory) {
		new RabbitTemplate(factory)
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
