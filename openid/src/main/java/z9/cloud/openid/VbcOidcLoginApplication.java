package z9.cloud.openid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import z9.cloud.openid.utils.YamlLoaderInitializer;

@SpringBootApplication
public class VbcOidcLoginApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(VbcOidcLoginApplication.class);
        ApplicationContextInitializer<ConfigurableApplicationContext> yamlInitializer = new YamlLoaderInitializer("login-application.yml");
        application.addInitializers(yamlInitializer);
        application.run(args);
    }

}
