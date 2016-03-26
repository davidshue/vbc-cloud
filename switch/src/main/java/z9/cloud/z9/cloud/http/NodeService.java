package z9.cloud.z9.cloud.http;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import z9.cloud.core.HttpInput;
import z9.cloud.core.HttpOutput;
import z9.cloud.core.Input;
import z9.cloud.core.Output;

/**
 * Created by dshue1 on 3/18/16.
 */
@FeignClient("node")
public interface NodeService {
	@RequestMapping(value = "/v1", method=RequestMethod.POST)
	String v1();

	@RequestMapping(value= "/v1/test", method=RequestMethod.POST, consumes = "application/json")
	Output testV1(Input input);

	@RequestMapping(value = "/v1/http", method=RequestMethod.POST, consumes = "application/json")
	HttpOutput httpV1(HttpInput input);
}
