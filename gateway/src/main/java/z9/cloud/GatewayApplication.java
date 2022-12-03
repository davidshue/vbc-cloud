package z9.cloud;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import z9.cloud.utils.YamlLoaderInitializer;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableZuulProxy
@EnableScheduling
public class GatewayApplication {
    private final Log logger = LogFactory.getLog(getClass());


    @Autowired
    private DiscoveryClient client;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(GatewayApplication.class);
        ApplicationContextInitializer<ConfigurableApplicationContext> yamlInitializer = new YamlLoaderInitializer();
        application.addInitializers(yamlInitializer);
        application.run(args);
    }

    @Bean
    public LicenseFilter licenseFilter() {
        return new LicenseFilter();
    }

    @Bean
    public LocationHeaderRewriteFilter locationFilter(@Autowired RouteLocator routerLocator) {
        return new LocationHeaderRewriteFilter(routerLocator);
    }

    @Bean
    public HttpFirewall getHttpFirewall() {
        return new DefaultHttpFirewall();
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
