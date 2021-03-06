package z9.cloud

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import z9.cloud.core2.Z9HttpRequest

import javax.annotation.PostConstruct
/**
 * Created by dshue1 on 6/19/16.
 */
@Component
class SessionHelper {
	@Autowired
	private SessionRepository sessionRepository

	@Autowired
	private RevivalRepository revivalRepository

	@Value('${http.revive.urls}')
	private String revive

	private List<String> reviveUrls = []

	@PostConstruct
	void afterInit() {
		reviveUrls = revive.split(/[,; ]+/)
	}

	private Object mutex1 = new Object()
	private Object mutex2 = new Object()
	private Object mutex3 = new Object()

	@Async
	void handleSessionsByNodeIdAndZid(Session session) {
		if (!session) return

		if (session.cookies) {
			synchronized (mutex1) {
				sessionRepository.deleteByNodeIdAndZid(session.nodeId, session.zid)
				sessionRepository.save(session)
			}
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

		synchronized (mutex2) {
			Session current = sessionRepository.findOne(sessionId)
			if (!current) return

			if (System.currentTimeMillis() - current.createDate.time >= 15000) {
				current.createDate = new Date()
				sessionRepository.save(current)
			}
		}

	}

	List<Revival> revive(String nodeId, String z9SessionId) {
		List<Revival> revivals = revivalRepository.findByZ9SessionId(z9SessionId)
				.findAll{it.nodeTimestamps.get(nodeId, System.currentTimeMillis() - EventProcessor.ONE_HOUR) < System.currentTimeMillis() - EventProcessor.THIRTY_MIN}
				.sort{a, b -> a.order <=> b.order}
				.each {
					it.nodeTimestamps[nodeId] = System.currentTimeMillis()
					revivalRepository.save(it)
				}
		return revivals
	}

	Revival findRevivalByZ9SessionIdAndUrl(String z9SessionId, String url) {
		List<Revival> revivals = revivalRepository.findByZ9SessionIdAndUrl(z9SessionId, url)
		return revivals ? revivals[0] : null
	}

	@Async
	void saveRevival(Z9HttpRequest request) {
		String z9SessionId = request.z9SessionId
		if (!z9SessionId) {
			return
		}
		UrlOrder urlOrder = getRevivalUrl(request.requestLine.uri)
		if (!urlOrder) {
			return
		}
		synchronized (mutex3) {
			Revival revival = findRevivalByZ9SessionIdAndUrl(z9SessionId, urlOrder.url)
			if (!revival) {
				revival = new Revival(z9SessionId: z9SessionId, url: urlOrder.url, order: urlOrder.order)
			}
			revival.request = request
			revivalRepository.save(revival)
		}

	}

	private UrlOrder getRevivalUrl(String uri) {
		UrlOrder urlOrder = null
		reviveUrls.eachWithIndex{ String prefix, int i ->
			if (urlOrder) {
				return
			}
			if (uri.startsWith(prefix)) {
				urlOrder = new UrlOrder(url: prefix, order: i)
			}
		}
		return urlOrder
	}

	private static class UrlOrder {
		String url
		int order
	}
}
