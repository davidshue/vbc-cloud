package z9.cloud

import com.mongodb.Mongo

import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter
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
	Mongo mongo() throws UnknownHostException {
		new Mongo("localhost")
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
	
	@Bean
	httpQueue() {
		new org.springframework.amqp.core.Queue('http_queue.node.' + env())
	}

	@Bean
	org.springframework.amqp.core.Binding binding() {
		BindingBuilder.bind(httpQueue()).to(exchange())
	}

	@Bean
	messageListenerContainer(EventProcessor processor, ConnectionFactory factory) {

		new SimpleMessageListenerContainer(
			connectionFactory: factory,
			queues : httpQueue(),
			concurrentConsumers: 5,
			maxConcurrentConsumers: 10,
			txSize: 10,
			messageListener: new MessageListenerAdapter(processor, 'processHttp')
		)
	}
}
