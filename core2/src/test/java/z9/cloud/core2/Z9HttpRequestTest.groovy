package z9.cloud.core2

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.entity.ContentType
import org.apache.http.message.BasicHttpEntityEnclosingRequest
import org.apache.http.message.BasicHttpRequest
import org.junit.Test

import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertTrue

/**
 * Created by david on 1/11/17.
 */
class Z9HttpRequestTest {
    private ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
    @Test
    void testEntityEnclosingMapping() {
        Z9HttpRequest request = new Z9HttpRequest(
                headers: [new Z9Header(name: 'header1', value: 'value1'), new Z9Header(name: 'header2', value: 'value2')],
                requestLine: new Z9RequestLine(method: 'post', uri: '/www.cnn.com', protocolVersion: new Z9ProtocolVersion(protocol: 'https', major: 1, minor: 1)),
                contentType: ContentType.DEFAULT_BINARY,
                content: 'this is a test'.bytes
        )
        println request
        def json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request)

        println json

        Z9HttpRequest out = mapper.readValue(json, Z9HttpRequest.class)

        println out

        assertEquals request, out

        assertTrue(request.toBasicHttpRequest() instanceof BasicHttpEntityEnclosingRequest)
        assertTrue(out.toBasicHttpRequest() instanceof BasicHttpEntityEnclosingRequest)
    }

    @Test
    void testMapping() {
        Z9HttpRequest request = new Z9HttpRequest(
                headers: [new Z9Header(name: 'header1', value: 'value1'), new Z9Header(name: 'header2', value: 'value2')],
                requestLine: new Z9RequestLine(method: 'post', uri: '/www.cnn.com', protocolVersion: new Z9ProtocolVersion(protocol: 'https', major: 1, minor: 1))
        )
        println request
        def json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request)

        println json

        Z9HttpRequest out = mapper.readValue(json, Z9HttpRequest.class)

        println out

        assertEquals request, out

        assertTrue(request.toBasicHttpRequest() instanceof BasicHttpRequest)
        assertTrue(out.toBasicHttpRequest() instanceof BasicHttpRequest)
    }
}
