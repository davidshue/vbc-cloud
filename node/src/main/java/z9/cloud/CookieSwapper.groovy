package z9.cloud

import z9.cloud.core.Cookie
import z9.cloud.core.CookieSet
import z9.cloud.core.HttpInput
import z9.cloud.core.HttpOutput

import org.springframework.stereotype.Component

/**
 * Created by dshue1 on 6/12/16.
 */
@Component
class CookieSwapper {
	private Map<String, CookieSet> cookieStore = [:]

	String swap(HttpInput input) {
		CookieSet cookieSet = input.cookieSet
		String z9sessionid = cookieSet.getValue('z9sessionid')
		println 'sessionid: ' + z9sessionid
		if (!z9sessionid) return null

		CookieSet nodeCookieSet = cookieStore[z9sessionid]
		if (nodeCookieSet) {
			println 'nodeCookieSet ' + nodeCookieSet
			input.secondaryCookieSet = nodeCookieSet
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
							cookieStore.get(z9sessionid, new CookieSet()).removeCookie(nv[0])
						}
						if (nv[0] && nv[1]) {
							cookieStore.get(z9sessionid, new CookieSet()).addCookie(new Cookie(nv[0], nv[1]))
						}
					}
				}
			}
			println cookieStore[z9sessionid]
		}

	}
}
