package z9.cloud

import z9.cloud.core.CookieSet

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
/**
 * Created by dshue1 on 6/19/16.
 */
@Component
class SessionHelper {
	@Autowired
	private SessionRepository sessionRepository

	@Async
	void handleSessionsByNodeIdAndZid(String nodeId, String zid, CookieSet cookieSet) {
		sessionRepository.deleteByNodeIdAndZid(nodeId, zid)
		if (cookieSet?.cookies) {
			sessionRepository.save(new Session(nodeId, zid, cookieSet))
		}
	}
}
