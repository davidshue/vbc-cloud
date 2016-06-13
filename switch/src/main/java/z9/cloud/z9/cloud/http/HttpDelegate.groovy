package z9.cloud.z9.cloud.http

import z9.cloud.core.Cookie
import z9.cloud.core.CookieSet
import z9.cloud.core.HttpInput
import z9.cloud.core.HttpOutput

import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by dshue1 on 3/14/16.
 */
class HttpDelegate {
	@Autowired
	private NodeService nodeService

	HttpOutput handle(HttpInput input) {
		CookieSet cookieSet = input.cookieSet
		String z9sessionid = cookieSet.getValue('z9sessionid')
		println "z9sessionid: $z9sessionid"
		String newId = null
		if (!z9sessionid) {
			newId = UUID.randomUUID().toString()
			input.addCookie(new Cookie('z9sessionid', newId))
		}
		HttpOutput output = nodeService.httpV1(input)


		if (!z9sessionid && newId) {
			output.cookies = ["Set-Cookie: z9sessionid=$newId; Path=/; HttpOnly" as String]
			//output.addCookie("Set-Cookie: z9fakeid=$newId; Path=/; HttpOnly")
		}
		return output
	}
}
