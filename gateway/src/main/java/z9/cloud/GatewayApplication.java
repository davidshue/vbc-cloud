package z9.cloud;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableZuulProxy
@EnableScheduling
public class GatewayApplication {
    private final Log logger = LogFactory.getLog(getClass());


    @Autowired
    private DiscoveryClient client;

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public LicenseFilter licenseFilter() {
        return new LicenseFilter();
    }

    @PostConstruct
    public void init() {
        client.getServices().forEach(
            service -> client.getInstances(service).forEach(
                inst -> logger.info(service + ": " + inst.getUri())
            )
        );
    }
}
