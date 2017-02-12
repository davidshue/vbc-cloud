package z9.cloud.http;


import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import z9.cloud.core2.Input;
import z9.cloud.core2.Output;
import z9.cloud.core2.Z9HttpRequest;
import z9.cloud.core2.Z9HttpResponse;

/**
 * Created by dshue1 on 3/18/16.
 */
@FeignClient(value="kafka-node", fallback = NodeServiceFallback.class)
public interface NodeService {
	@RequestMapping(value = "/v1", method=RequestMethod.POST)
	String v1();

	@RequestMapping(value= "/v1/test", method=RequestMethod.POST, consumes = "application/json")
	Output testV1(Input input);

	@RequestMapping(value = "/v1/http", method=RequestMethod.POST, consumes = "application/json")
	Z9HttpResponse httpV1(Z9HttpRequest input);
}


