package z9.cloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableZuulProxy
public class GatewayApplication {
    @Autowired
    private DiscoveryClient client;

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @PostConstruct
    public void init() {
        client.getServices().forEach(
            service -> {
                client.getInstances(service).forEach(
                    inst -> {
                        System.out.println(service + ": " + inst.getUri());
                    }
                );
            }
        );
    }
}
