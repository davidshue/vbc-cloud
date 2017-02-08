package z9.cloud

import org.apache.http.cookie.CookieOrigin
import org.apache.http.cookie.SM
import org.apache.http.impl.cookie.RFC2109Spec
import org.apache.http.message.BasicHeader
import org.junit.Test

/**
 * Created by dshue1 on 6/15/16.
 */
class CookieParserTest {
	@Test
	void testParse() {
		def cookieSpec = new RFC2109Spec(['EEE dd-MMM-yy HH:mm:ss zzz'].toArray(new String[0]), true)


		def cookies = []


		CookieOrigin origin = new CookieOrigin('www.z9.com', 80, '/', true)

		BasicHeader header = new BasicHeader(SM.SET_COOKIE, 'icbc=MjA1MjM6MTkzOTA2NjYyMjgxODoyZTk1MjExOGJlNzIzZjhkYmM1NjE4NzQyYWNlZDk2OA; Expires=Thu 12-Jun-2031 21:37:02 GMT; Path=/; HttpOnly')

		cookies = cookieSpec.parse(header, origin)

		println cookies
	}
}
