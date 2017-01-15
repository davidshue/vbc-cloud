package z9.cloud

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
/**
 * Created by dshue1 on 6/12/16.
 */
@Component
class CookieSwapper {
	@Autowired
	private SessionRepository sessionRepository

	@Autowired
	private SessionHelper sessionHelper

	@Autowired
	@Qualifier("env")
	private String env

	private String nodeId

	private Map<String, Session> cookieStore = [:]


	@PostConstruct
	void afterConstruct() {
		nodeId = 'node-' + env
		List<Session> sessions = sessionRepository.findByNodeId(nodeId)
		cookieStore = sessions.collectEntries {[it.zid, it]}
	}
/*
	String swap(HttpInput input) {
		CookieSet cookieSet = input.cookieSet
		String z9sessionid = cookieSet.getValue('z9sessionid')
		println 'sessionid: ' + z9sessionid
		if (!z9sessionid) return null

		Session session = cookieStore[z9sessionid]
		if (session) {
			println 'nodeCookieSet ' + session.cookies
			input.secondaryCookieSet = session.cookies
			sessionHelper.renewSessionLease(cookieStore, session)
		}
		return z9sessionid
	}

	void mediate(String z9sessionid, HttpOutput output) {
		if (!z9sessionid) return

		println output.cookies
		if (output.cookies) {
			output.cookies.each {
				def setcookies =  it.substring(it.indexOf(':') + 2).split(',')
				setcookies.each {sc ->
					String line = sc.substring(0, sc.indexOf(';'))
					println line
					if (line.contains('=')) {
						def nv = line.split('=')
						if (nv[1]) {nv[1] = nv[1].replaceAll('"', '')}
						if (!nv[1]) {
							cookieStore.get(z9sessionid, new Session(nodeId: nodeId, zid: z9sessionid)).cookies.removeCookie(nv[0])

							// Asynchrously talk to mongodb to persist the session info
							sessionHelper.handleSessionsByNodeIdAndZid(cookieStore.get(z9sessionid))
						}
						if (nv[0] && nv[1]) {
							cookieStore.get(z9sessionid, new Session(nodeId: nodeId, zid: z9sessionid)).cookies.removeCookie(nv[0])
							cookieStore.get(z9sessionid).cookies.addCookie(new Cookie(nv[0], nv[1]))

							// Asynchrously talk to mongodb to persist the session info
							sessionHelper.handleSessionsByNodeIdAndZid(cookieStore.get(z9sessionid))
						}
					}
				}
			}
			println cookieStore[z9sessionid]
		}

	}
*/
}
