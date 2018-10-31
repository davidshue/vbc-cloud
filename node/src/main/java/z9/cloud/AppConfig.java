package z9.cloud;

import com.netflix.hystrix.HystrixCircuitBreaker;
import com.netflix.hystrix.HystrixCommandMetrics;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import z9.cloud.core2.HttpRetry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.springframework.boot.actuate.health.Status.DOWN;

/**
 * Created by david on 2/8/17.
 */

@Configuration
@EnableKafka
public class AppConfig {
    @Autowired
    private Environment environment;

    @Value("${kafka.zookeeper}")
    private String zookeeper;

    @Value("${kafka.group.prefix}")
    private String kafkaGroupPrefix;

    @Value("${kafka.autocommit.interval}")
    private String kafkaAutoCommitInterval;

    @Value("${kafka.session.timeout}")
    private String kafkaSessionTimeout;

    @Bean
    public String env() {
        return environment.getActiveProfiles().length == 0 ? "default" : environment.getActiveProfiles()[0];
    }

    @Bean
    public HttpRetry httpRetry() {
        return new HttpRetry();
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Integer, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<Integer, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, zookeeper);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaGroupPrefix + "-" + env());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, kafkaAutoCommitInterval);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, kafkaSessionTimeout);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    @Bean
    public Listener listener() {
        return new Listener();
    }

    @Bean
    public ProducerFactory<Integer, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, zookeeper);
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public KafkaTemplate<Integer, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public AbstractHealthIndicator hystrixHealth() {
        return new AbstractHealthIndicator() {
            @Override
            protected void doHealthCheck(Health.Builder builder) throws Exception {
                List<String> openCircuitBreakers = new ArrayList();
                Iterator var3 = HystrixCommandMetrics.getInstances().iterator();

                while(var3.hasNext()) {
                    HystrixCommandMetrics metrics = (HystrixCommandMetrics)var3.next();
                    HystrixCircuitBreaker circuitBreaker = HystrixCircuitBreaker.Factory.getInstance(metrics.getCommandKey());
                    if (circuitBreaker != null && circuitBreaker.isOpen()) {
                        openCircuitBreakers.add(metrics.getCommandGroup().name() + "::" + metrics.getCommandKey().name());
                    }
                }

                if (openCircuitBreakers.size() > 0) {
                    builder.status(DOWN).withDetail("openCircuitBreakers", openCircuitBreakers);
                } else {
                    builder.up();
                }
            }
        };
    }
}
