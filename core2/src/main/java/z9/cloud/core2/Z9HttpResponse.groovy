package z9.cloud.core2

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.entity.ContentType
import org.apache.http.message.BasicHttpResponse

import java.nio.charset.StandardCharsets

/**
 * Created by david on 1/11/17.
 */
@EqualsAndHashCode
@ToString(includeNames = true, includePackage = false, ignoreNulls = true)
class Z9HttpResponse implements Serializable {
    private static final long serialVersionUID = 1L

    Z9StatusLine statusLine = null

    Z9Header[] headers = []

    String contentType = null

    byte[] content = []

    BasicHttpResponse toBasicHttpResponse() {
        def out = new BasicHttpResponse(statusLine.toBasicStatusLine())
        if (content)
            out.entity = new ByteArrayEntity(content, new ContentType(contentType, StandardCharsets.UTF_8))
        headers.each {
            out.addHeader(it.toBasicHeader())
        }
        return out
    }

}
