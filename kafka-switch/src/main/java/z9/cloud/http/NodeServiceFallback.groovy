package z9.cloud.http

import org.springframework.stereotype.Component
import z9.cloud.core2.Input
import z9.cloud.core2.Output
import z9.cloud.core2.Z9Header
import z9.cloud.core2.Z9HttpRequest
import z9.cloud.core2.Z9HttpResponse
import z9.cloud.core2.Z9ProtocolVersion
import z9.cloud.core2.Z9StatusLine

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
	Z9HttpResponse httpV1(Z9HttpRequest input) {
		return new Z9HttpResponse(
				statusLine: new Z9StatusLine(protocolVersion: new Z9ProtocolVersion(protocol: 'https', major: 1, minor: 1),
						statusCode: 200),
				headers: [new Z9Header(name: 'header1', value: 'value1'), new Z9Header(name: 'header2', value: 'value2')],
				content: 'this is a test'.bytes)
	}
}
