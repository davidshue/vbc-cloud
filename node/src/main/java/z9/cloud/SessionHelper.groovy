package z9.cloud

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

	private Object mutex = new Object()

	@Async
	void handleSessionsByNodeIdAndZid(Session session) {
		if (!session) return
		sessionRepository.deleteByNodeIdAndZid(session.nodeId, session.zid)
		if (session.cookies) {
			sessionRepository.save(session)
		}
	}

	/**
	 * This is to keep the session active in mongoDB. All sessions in MongoDB has a lifetime of 30 min (inactivity
	 * will cause the session to be purged by mongo). By renewing (updating the createDate), active action could
	 * potentially be extended)
	 * @param sessionId
	 */
	@Async
	void renewSessionLease(String sessionId) {
		if (!sessionId) return

		synchronized (mutex) {
			Session current = sessionRepository.findOne(sessionId)
			if (!current) return

			if (System.currentTimeMillis() - current.createDate.time >= 15000) {
				current.createDate = new Date()
				sessionRepository.save(current)
			}
		}

	}
}
