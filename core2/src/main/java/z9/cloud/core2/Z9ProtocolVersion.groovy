package z9.cloud.core2

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.apache.http.ProtocolVersion

/**
 * Created by david on 1/12/17.
 */
@EqualsAndHashCode
@ToString(includeNames = true, includePackage = false, ignoreNulls = true)
class Z9ProtocolVersion {
    private static final long serialVersionUID = 1L
    String protocol
    int major
    int minor

    ProtocolVersion toProtocolVersion() {
        new ProtocolVersion(protocol, major, minor)
    }
}
