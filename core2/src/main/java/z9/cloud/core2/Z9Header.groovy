package z9.cloud.core2

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.apache.http.message.BasicHeader

/**
 * Created by david on 1/12/17.
 */
@EqualsAndHashCode
@ToString(includeNames = true, includePackage = false, ignoreNulls = true)
class Z9Header implements Serializable {
    private static final long serialVersionUID = 1L
    String name
    String value

    BasicHeader toBasicHeader() {
        new BasicHeader(name, value)
    }
}
