package z9.cloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import z9.cloud.core.Input;
import z9.cloud.core.Output;
import z9.cloud.z9.cloud.http.NodeService;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@RestController
public class SwitchApplication {
    @Autowired
    private NodeService nodeService;

    public static void main(String[] args) {
        SpringApplication.run(SwitchApplication.class, args);
    }

    @RequestMapping(value = "/v1", method= RequestMethod.GET)
    public String v1() {
        return nodeService.v1();
    }

    @RequestMapping(value= "/v1/test", method=RequestMethod.POST)
    public Output testV1(@RequestBody Input input) {
        return nodeService.testV1(input);
    }
}
