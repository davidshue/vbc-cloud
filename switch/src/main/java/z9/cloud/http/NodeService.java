package z9.cloud.http;


import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import z9.cloud.core2.Input;
import z9.cloud.core2.Output;

/**
 * Created by dshue1 on 3/18/16.
 */
@FeignClient(name="gateway", fallback = NodeServiceFallback.class)
public interface NodeService {
	@RequestMapping(value = "/node/v1", method=RequestMethod.POST)
	ResponseEntity<String> v1();

	@RequestMapping(value= "/node/v1/test", method=RequestMethod.POST, consumes = "application/json")
	ResponseEntity<Output> testV1(Input input);
}


