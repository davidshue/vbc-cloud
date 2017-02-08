package z9.cloud

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import z9.cloud.core2.Z9Header
import z9.cloud.core2.Z9HttpRequest
import z9.cloud.core2.Z9ProtocolVersion
import z9.cloud.core2.Z9RequestLine

import static org.junit.Assert.assertEquals

@RunWith(SpringRunner.class)
@SpringBootTest
class NodeApplicationTests {
	@Autowired
	private SessionRepository sessionRepository

	@Autowired
	private RevivalRepository revivalRepository

	@Test
	void test() {
		sessionRepository.save(new Session('node-fake', 'abc123'))

		assertEquals 1, sessionRepository.findByNodeId('node-fake').size()

		sessionRepository.save(new Session('node-fake', 'abc789'))

		assertEquals 2, sessionRepository.findByNodeId('node-fake').size()

		assertEquals 1, sessionRepository.deleteByNodeIdAndZid('node-fake', 'abc123')

		assertEquals 1, sessionRepository.deleteByNodeIdAndZid('node-fake', 'abc789')
	}

	@Test
	void testEntry() {
		Z9HttpRequest request = new Z9HttpRequest(
				headers: [new Z9Header(name: 'header1', value: 'value1'), new Z9Header(name: 'header2', value: 'value2')],
				requestLine: new Z9RequestLine(method: 'post', uri: 'http://www.cnn.com', protocolVersion: new Z9ProtocolVersion(protocol: 'https', major: 1, minor: 1)),
				content: 'this is a test'.bytes
		)
		revivalRepository.save(new Revival('zid10001', '/login', request))

		assertEquals 1, revivalRepository.findByZ9SessionId('zid10001').size()

		revivalRepository.deleteByZ9SessionId('zid10001')

		assertEquals 0, revivalRepository.findByZ9SessionId('zid10001').size()
	}

}
