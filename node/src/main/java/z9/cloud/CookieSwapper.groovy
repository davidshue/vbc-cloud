package z9.cloud

import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.cookie.CookieOrigin
import org.apache.http.cookie.CookieSpec
import org.apache.http.message.BasicHeader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import z9.cloud.core2.Z9HttpUtils

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

	String swap(HttpRequest input) {
		String z9sessionid = Z9HttpUtils.getZ9SessionId(input)
		println 'sessionid: ' + z9sessionid
		if (!z9sessionid) return null

		Session session = cookieStore[z9sessionid]
		if (session) {
			println 'nodeCookies ' + session.cookies
			input.removeHeaders('Cookie')
			session.cookies.each {k, v ->
				input.addHeader(new BasicHeader('Cookie', "$k=$v"))
			}
			sessionHelper.renewSessionLease(cookieStore, session)
		}
		return z9sessionid
	}

	void mediate(String z9sessionid, HttpResponse output, HttpClientContext context) {
		if (!z9sessionid || !output.getHeaders('Set-Cookie')) return
        CookieOrigin cookieOrigin = context.cookieOrigin
        CookieSpec cookieSpec = context.cookieSpec

        output.getHeaders('Set-Cookie').each {header ->
            cookieSpec.parse(header, cookieOrigin).each {cookie ->
                if (cookie.name != Z9HttpUtils.Z9_SESSION_ID) {
                    if (cookie.value) {
                        cookieStore.get(z9sessionid, new Session(nodeId: nodeId, zid: z9sessionid)).cookies[cookie.name] = cookie.value
                    }
                    else {
                        cookieStore.get(z9sessionid)?.cookies.remove(cookie.name)
                    }
                }

            }
        }
        sessionHelper.handleSessionsByNodeIdAndZid(cookieStore.get(z9sessionid))

        println cookieStore[z9sessionid]

	}

}
