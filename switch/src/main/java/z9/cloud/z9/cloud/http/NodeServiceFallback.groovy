package z9.cloud.z9.cloud.http

import org.springframework.stereotype.Component
import z9.cloud.core.HttpInput
import z9.cloud.core.HttpOutput
import z9.cloud.core.Input
import z9.cloud.core.Output

/**
 * Created by dshue1 on 5/30/16.
 */
@Component
class NodeServiceFallback implements NodeService {
	@Override
	String v1() {
		return 'v1 failed'
	}

	@Override
	Output testV1(Input input) {
		return new Output(code: 500, output: 'failed')
	}

	@Override
	HttpOutput httpV1(HttpInput input) {
		return new HttpOutput(title: 'httpV1 failed')
	}
}
