package z9.cloud.core2

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.entity.ContentType
import org.apache.http.message.BasicHttpEntityEnclosingRequest
import org.apache.http.message.BasicHttpRequest

import java.nio.charset.StandardCharsets

/**
 * Created by david on 1/10/17.
 */
@EqualsAndHashCode
@ToString(includeNames = true, includePackage = false, ignoreNulls = true)
class Z9HttpRequest implements Serializable {
    private static final long serialVersionUID = 1L

    Z9RequestLine requestLine = null

    Z9Header[] headers = []

    String contentType = null

    byte[] content = []

    BasicHttpRequest toBasicHttpRequest() {
        def out
        if (!content)   {
            out = new BasicHttpRequest(requestLine.toBasicRequestLine())
        }
        else {
            out = new BasicHttpEntityEnclosingRequest(requestLine.toBasicRequestLine())
            out.entity = new ByteArrayEntity(content, new ContentType(contentType, StandardCharsets.UTF_8))
        }
        headers.each {
            out.addHeader(it.toBasicHeader())
        }
        return out
    }
}
