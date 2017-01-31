package z9.cloud

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
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
    private final Log logger = LogFactory.getLog(getClass())

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

	String swap(HttpRequest request, String z9sessionid) {
		println 'sessionid: ' + z9sessionid
		if (!z9sessionid) return null

		Session session = cookieStore[z9sessionid]
		if (session?.cookies) {
			println 'nodeCookies ' + session.cookies
            request.removeHeaders("Cookie")
            String value  = session.cookies.collect {k, v -> "$k=$v"}.join('; ')
            value += "; z9sessionid=$z9sessionid"
            logger.info("cookie value: $value")
            request.addHeader(new BasicHeader('Cookie', value))

			sessionHelper.renewSessionLease(session.id)
		}
        else {
            cookieStore.remove(z9sessionid)
        }

		return z9sessionid
	}

	void mediate(String z9sessionid, HttpResponse response, HttpClientContext context) {
		if (!z9sessionid || !response.getHeaders('Set-Cookie')) return

        CookieOrigin cookieOrigin = context.cookieOrigin
        CookieSpec cookieSpec = context.cookieSpec

        logger.info response.getHeaders('Set-Cookie').join('-------')
        response.getHeaders('Set-Cookie').each {header ->
            cookieSpec.parse(header, cookieOrigin).each {cookie ->
                if (cookie.name != Z9HttpUtils.Z9_SESSION_ID) {
                    if (cookie.value && cookie.value != '""') {
                        logger.info("adding $cookie for $z9sessionid")
                        cookieStore.get(z9sessionid, new Session(nodeId: nodeId, zid: z9sessionid)).cookies[cookie.name] = cookie.value
                        logger.info('post set-cookies ' + cookieStore[z9sessionid].cookies)
                    }
                    else {
                        logger.info("removing $cookie for $z9sessionid")
                        logger.info('before post delete set-cookies ' + cookieStore[z9sessionid]?.cookies)
                        cookieStore.get(z9sessionid)?.cookies?.remove(cookie.name)
                        logger.info('after post delete set-cookies ' + cookieStore[z9sessionid]?.cookies)
                    }
                }

            }
        }

        sessionHelper.handleSessionsByNodeIdAndZid(cookieStore.get(z9sessionid))

        response.removeHeaders('Set-Cookie')

        if (!cookieStore[z9sessionid]?.cookies) {
            logger.info("removing $cookieStore.z9sessionid")
            cookieStore.remove(z9sessionid)
        }
        else {
            logger.info cookieStore[z9sessionid]
        }
	}

}
