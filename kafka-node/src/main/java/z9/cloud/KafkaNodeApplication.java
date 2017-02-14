package z9.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableEurekaClient
@EnableAsync
public class KafkaNodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaNodeApplication.class, args);
    }
}