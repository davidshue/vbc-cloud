package z9.cloud.core2

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.apache.http.RequestLine
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

    static Z9RequestLine toZ9RequestLine(RequestLine input) {
        new Z9RequestLine(protocolVersion: Z9ProtocolVersion.toZ9ProtocolVersion(input.protocolVersion),
                method: input.method,
                uri: input.uri
        )
    }
}
