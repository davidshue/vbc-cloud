package z9.cloud.core2

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.apache.http.HttpResponse
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.message.BasicHttpResponse
import org.apache.http.util.EntityUtils
/**
 * Created by david on 1/11/17.
 */
@EqualsAndHashCode
@ToString(includeNames = true, includePackage = false, ignoreNulls = true)
class Z9HttpResponse implements Serializable {
    private static final long serialVersionUID = 1L

    Z9StatusLine statusLine = null

    Z9Header[] headers = []

    byte[] content = []

    BasicHttpResponse toBasicHttpResponse() {
        def out = new BasicHttpResponse(statusLine.toBasicStatusLine())
        if (content) {
            out.entity = new ByteArrayEntity(content)
        }
        out.headers = headers.collect{it.toBasicHeader()}

        return out
    }

    static Z9HttpResponse toZ9HttpResponse(HttpResponse input) {
        def out = new Z9HttpResponse(statusLine: Z9StatusLine.toZ9StatusLine(input.statusLine))
        out.headers = input.allHeaders.collect {Z9Header.toZ9Header(it)}

        out.content = EntityUtils.toByteArray(input.entity)

        return out
    }
}
