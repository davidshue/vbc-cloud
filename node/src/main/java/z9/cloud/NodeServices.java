package z9.cloud;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import z9.cloud.core2.Input;
import z9.cloud.core2.Output;
import z9.cloud.core2.Z9HttpRequest;
import z9.cloud.core2.Z9HttpUtils;

import java.io.IOException;

/**
 * Created by dshue1 on 3/13/16.
 */
@RestController
public class NodeServices {
	private static Log logger = LogFactory.getLog(NodeServices.class);


	@Autowired
	private AmqpTemplate template;

	@Autowired
	@Qualifier("env")
	private String env;

	@Autowired
	private EventProcessor processor;


	@RequestMapping(value = "/v1", method=RequestMethod.POST)
	public String v1() {
		return "hi there again! from " + env;
	}


	@RequestMapping(value = "/v1/http", method=RequestMethod.POST)
	public Object httpV1(@RequestBody Z9HttpRequest input) throws IOException, HttpException {
		input.setOrigin(env);
		if (StringUtils.isBlank(input.getNewZid())) {
			input.setNewZid(Z9HttpUtils.randomZ9SessionId());
		}
		template.convertAndSend("http_exchange", "broadcast", input);
		return processor.executeHttp(input);
	}

	@RequestMapping(value= "/v1/test", method=RequestMethod.POST)
	public Output testV1(@RequestBody Input input) {
		Output output = new Output();
		output.setOutput(input.getName() + " on " + env);
		output.setCode(200);

		return output;
	}


}
