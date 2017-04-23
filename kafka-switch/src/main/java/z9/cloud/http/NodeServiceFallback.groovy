package z9.cloud.http

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import z9.cloud.core2.Input
import z9.cloud.core2.Output
/**
 * Created by dshue1 on 5/30/16.
 */
@Component
class NodeServiceFallback implements NodeService {
	@Override
	ResponseEntity<String> v1() {
		return 'v1 failed'
	}

	@Override
	ResponseEntity<Output> testV1(Input input) {
		return new Output(code: 500, output: 'failed')
	}
}
