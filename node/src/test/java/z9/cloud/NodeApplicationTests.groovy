package z9.cloud

import z9.cloud.core.CookieSet

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import static org.junit.Assert.assertEquals

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = NodeApplication.class)
public class NodeApplicationTests {
	@Autowired
	private SessionRepository sessionRepository

	@Test
	public void test() {
		sessionRepository.save(new Session('node-fake', 'abc123', new CookieSet('abc=123')))

		assertEquals 1, sessionRepository.findByNodeId('node-fake').size()

		sessionRepository.save(new Session('node-fake', 'abc789', new CookieSet('abc=234')))

		assertEquals 2, sessionRepository.findByNodeId('node-fake').size()

		assertEquals 1, sessionRepository.deleteByZid('abc123')

		assertEquals 1, sessionRepository.deleteByZid('abc789')
	}

}
