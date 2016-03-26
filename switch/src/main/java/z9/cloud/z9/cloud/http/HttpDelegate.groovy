package z9.cloud.z9.cloud.http

import org.springframework.beans.factory.annotation.Autowired
import z9.cloud.core.HttpInput
import z9.cloud.core.HttpOutput
/**
 * Created by dshue1 on 3/14/16.
 */
class HttpDelegate {
	@Autowired
	private NodeService nodeService

	HttpOutput handle(HttpInput input) {
		nodeService.httpV1(input)
	}
}
