package z9.cloud

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import z9.cloud.core2.Z9HttpRequest
/**
 * Created by dshue1 on 6/19/16.
 */
@ToString(includeNames = true, includePackage = false, ignoreNulls = true)
@TupleConstructor(excludes = ['id', 'order', 'nodeTimestamps'])
@EqualsAndHashCode(includes = ['z9SessionId', 'url'])
@Document(collection = 'revival')
class Revival {
	@Id
	private String id

	String z9SessionId

	String url

	int order = 0

	Map<String, Long> nodeTimestamps = [:]

	Z9HttpRequest request

	String getId() {
		return id
	}

}
