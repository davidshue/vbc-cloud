package z9.cloud

import groovy.transform.ToString
import groovy.transform.TupleConstructor
import z9.cloud.core.CookieSet

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Created by dshue1 on 6/19/16.
 */
@ToString(includeNames = true, includePackage = false, ignoreNulls = true)
@TupleConstructor(excludes = ['id'])
@Document(collection = 'session')
class Session {
	@Id
	private String id

	String nodeId

	String zid

	CookieSet cookies

	String getId() {
		return id
	}

}
