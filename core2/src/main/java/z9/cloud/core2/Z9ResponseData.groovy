package z9.cloud.core2

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode
@ToString(includeNames = true, includePackage = false, ignoreNulls = true)
class Z9ResponseData implements Serializable {
    private static final long serialVersionUID = 1L

    String id
    Long timestamp
    Z9HttpResponse response
}
