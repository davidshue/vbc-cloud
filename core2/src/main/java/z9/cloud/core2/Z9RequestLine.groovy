package z9.cloud.core2

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.apache.http.message.BasicRequestLine
/**
 * Created by david on 1/12/17.
 */
@EqualsAndHashCode
@ToString(includeNames = true, includePackage = false, ignoreNulls = true)
class Z9RequestLine implements Serializable {
    private static final long serialVersionUID = 1L

    Z9ProtocolVersion protocolVersion
    String method
    String uri

    BasicRequestLine toBasicRequestLine() {
        new BasicRequestLine(method, uri, protocolVersion.toProtocolVersion())
    }
}
