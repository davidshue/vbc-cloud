package z9.cloud.core2

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.apache.http.message.BasicStatusLine

/**
 * Created by david on 1/12/17.
 */
@EqualsAndHashCode
@ToString(includeNames = true, includePackage = false, ignoreNulls = true)
class Z9StatusLine {
    private static final long serialVersionUID = 1L
    Z9ProtocolVersion protocolVersion
    int statusCode
    String reasonPhrase

    BasicStatusLine toBasicStatusLine() {
        new BasicStatusLine(protocolVersion.toProtocolVersion(), statusCode, reasonPhrase)
    }
}
